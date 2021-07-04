package com.ylzs.service.partCraft.impl;

import com.alibaba.fastjson.JSONObject;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.process.RuleProcessType;
import com.ylzs.common.util.*;
import com.ylzs.common.utils.object.ObjectUtils;
import com.ylzs.dao.partCraft.PartCraftMasterDataDao;
import com.ylzs.entity.partCraft.*;
import com.ylzs.service.impl.OriginServiceImpl;
import com.ylzs.service.partCraft.*;
import com.ylzs.vo.partCraft.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


@Transactional(rollbackFor = Exception.class)
@Service()
public class PartCraftMasterDataServiceImpl extends OriginServiceImpl<PartCraftMasterDataDao, PartCraftMasterData> implements IPartCraftMasterDataService {

    private final static Logger LOGGER = LoggerFactory.getLogger(PartCraftMasterDataServiceImpl.class);
    @Resource
    private PartCraftMasterDataDao partCraftMasterDataDao;
    @Resource
    private IPartCraftRuleService iPartCraftRuleService;
    @Resource
    private IPartCraftMasterPictureService partCraftMasterPictureService;
    @Resource
    private IPartCraftPositionService partCraftPositionService;
    @Resource
    private IPartCraftDesignPartsService partCraftDesignPartsService;
    @Resource
    private IPartCraftDetailService partCraftDetailService;

    @Resource
    private RedisUtil redisUtil;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public PartCraftMasterData getPartCraftMasterData(Long randomCode) {
        return partCraftMasterDataDao.getPartCraftMasterData(randomCode);
    }

    @Override
    public List<PartCraftMasterBasicVo> searchPartCraftInfo(HashMap param) {
        List<PartCraftMasterBasicVo> list = new ArrayList<>();
        try {
            list = partCraftMasterDataDao.searchPartCraftInfo(param);
            List<Long> randomCodes = new ArrayList<>();
            list.forEach(basic -> randomCodes.add(basic.getRandomCode()));
            Map<Long, List<PartCraftDetailVo>> groupByDetailList = partCraftDetailService.getPartCraftDetailMainRandomCodeByList(randomCodes);
            Map<Long, List<PartCraftDesignPartsVo>> groupByDesignPartList = partCraftDesignPartsService.getDesignPartGroupMainRandomCodeByList(randomCodes);
            Map<Long, List<PartCraftPositionVo>> groupByPositionList = partCraftPositionService.getPartCraftPositionGroupMainRandomCodeByList(randomCodes);
            Map<Long, List<PartCraftRuleVo>> groupByRuleList = iPartCraftRuleService.getPartCraftRuleGroupMainRandomCodeByList(randomCodes);
            Map<Long, List<PartCraftMasterPictureVo>> groupByPictureList = partCraftMasterPictureService.getPictureGroupMainRandomCodeByList(randomCodes);
            list.parallelStream().forEach(basic -> {
                if (groupByDetailList.size() > 0) {
                    for (Long randomCode : groupByDetailList.keySet()) {
                        if (basic.getRandomCode().equals(randomCode)) {
                            basic.setPartCraftDetails(groupByDetailList.get(randomCode));
                        }
                    }
                }
                if (groupByDesignPartList.size() > 0) {
                    for (Long randomCode : groupByDesignPartList.keySet()) {
                        if (basic.getRandomCode().equals(randomCode)) {
                            basic.setCraftDesignParts(groupByDesignPartList.get(randomCode));
                        }
                    }
                }
                if (groupByPositionList.size() > 0) {
                    for (Long randomCode : groupByPositionList.keySet()) {
                        if (basic.getRandomCode().equals(randomCode)) {
                            basic.setCraftPositions(groupByPositionList.get(randomCode));
                        }
                    }
                }
                if (groupByRuleList.size() > 0) {
                    for (Long randomCode : groupByRuleList.keySet()) {
                        if (basic.getRandomCode().equals(randomCode)) {
                            basic.setPartCraftRules(groupByRuleList.get(randomCode));
                        }
                    }
                }
                if (groupByPictureList.size() > 0) {
                    for (Long randomCode : groupByPictureList.keySet()) {
                        if (basic.getRandomCode().equals(randomCode)) {
                            basic.setPictures(groupByPictureList.get(randomCode));
                        }
                    }
                }
            });
        } catch (Exception ex) {
            list = new ArrayList<>();
        }
        return list;
    }

    @Override
    public List<PartCraftMasterExportVo> searchPartCraftInfoExport(HashMap param) {
        List<PartCraftMasterBasicVo> lst = partCraftMasterDataDao.searchPartCraftInfo(param);
        List<PartCraftMasterExportVo> exports = new ArrayList<>();
        for (PartCraftMasterBasicVo itm : lst) {
            try {
                PartCraftMasterExportVo result = new PartCraftMasterExportVo();
                BeanUtils.copyProperties(itm, result);
                exports.add(result);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return exports;

    }

    @Override
    public List<PartCraftMasterBasicVo> searchPartCraftAndDetailInfo(HashMap param) {
        return partCraftMasterDataDao.searchPartCraftAndDetailInfo(param);
    }

    @Override
    public List<PartCraftMasterBasicVo> searchOnlyPartCraftInfo(HashMap param) {
        return partCraftMasterDataDao.searchOnlyPartCraftInfo(param);
    }

    @Override
    public PartCraftMasterData savePartCraftData(PartCraftMasterBasicVo basicVo) throws Exception {
        Boolean result = false;

        //组装部件词库主数据
        PartCraftMasterData masterData = buildCraftMasterData(basicVo);
        List<PartCraftDetail> details = buildPartCraftDetailData(basicVo.getPartCraftDetails(),
                masterData.getRandomCode(), basicVo.getCreateUser());

        List<PartCraftRule> ruleList = buildPartCraftRule(basicVo.getPartCraftRules(), masterData.getRandomCode(), basicVo.getCreateUser());

        List<PartCraftDesignParts> designPartsList = buildPartCraftDesginParts(basicVo.getCraftDesignParts(), masterData.getRandomCode(), basicVo.getCreateUser());

        List<PartCraftPosition> partCraftPositionList = buildPartCraftPositionData(basicVo.getCraftPositions(), masterData.getRandomCode(), basicVo.getCreateUser());

        List<PartCraftMasterPicture> pictureList = buildPartCraftPictureData(basicVo.getPictures(), masterData.getRandomCode());
        List<PartCraftDesignParts> removeDesignPartList = new ArrayList<>();
        List<PartCraftPosition> removePositionList = new ArrayList<>();
        //去重过滤
        details = distinctByPartCraftDetailList(details);
        ruleList = distinctByPartCraftRuleList(ruleList);
        designPartsList = dictinctByDesignPartsList(designPartsList, removeDesignPartList);
        partCraftPositionList = dictinctByPartCrafPositionList(partCraftPositionList, removePositionList);
        removePartCraftRedisList(masterData.getPartType(), removeDesignPartList, removePositionList);
        result = saveOrUpdate(masterData);
        if (ObjectUtils.isNotEmptyList(details)) {
            result = partCraftDetailService.saveOrUpdateBatch(details);
        }
        if (ObjectUtils.isNotEmptyList(ruleList)) {
            result = iPartCraftRuleService.saveOrUpdateBatch(ruleList);
        }
        if (ObjectUtils.isNotEmptyList(designPartsList)) {
            result = partCraftDesignPartsService.saveOrUpdateBatch(designPartsList);
        }
        if (ObjectUtils.isNotEmptyList(partCraftPositionList)) {
            result = partCraftPositionService.saveOrUpdateBatch(partCraftPositionList);
        }

        for (PartCraftMasterPicture picture : pictureList) {
            try {
                PartCraftMasterPicture ps = partCraftMasterPictureService.getPartCraftPicture(picture.getRandomCode());
                if (ps != null) {
                    ps.setStatus(BusinessConstants.Status.DRAFT_STATUS);
                    ps.setPartCraftMainRandomCode(masterData.getRandomCode());
                    ps.setCreateUser(basicVo.getCreateUser());
                    partCraftMasterPictureService.updateById(ps);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
        }
        if (result) {
            if (masterData.getPartType().equalsIgnoreCase(PartCraftPartType.conventionalPartCraft)) {
                if (ObjectUtils.isNotEmptyList(designPartsList)) {
                    List<String> codes = new ArrayList<>();
                    designPartsList.forEach(part -> codes.add(part.getDesignCode()));
                    savePartCraftPartRedisData(masterData.getPartType(), codes, null);
                }
            } else {
                if (ObjectUtils.isNotEmptyList(designPartsList) && ObjectUtils.isNotEmptyList(partCraftPositionList)) {
                    List<String> designPartCode = new ArrayList<>();
                    List<String> designPartPositionCode = new ArrayList<>();
                    designPartsList.forEach(part -> designPartCode.add(part.getDesignCode()));
                    partCraftPositionList.forEach(position -> designPartPositionCode.add(position.getPartPositionCode()));
                    savePartCraftPartRedisData(masterData.getPartType(), designPartCode, designPartPositionCode);
                }
            }

            return masterData;
        } else {
            return null;
        }
    }

    private List<PartCraftMasterPicture> buildPartCraftPictureData(List<PartCraftMasterPictureVo> pictures, Long randomCode) {
        List<PartCraftMasterPicture> list = new ArrayList<>();
        if (ObjectUtils.isNotEmptyList(pictures)) {
            for (PartCraftMasterPictureVo vo : pictures) {
                PartCraftMasterPicture picture = new PartCraftMasterPicture();
                if (vo.getId() != null) {
                    picture.setId(vo.getId());
                }
                if (vo.getRandomCode() != null) {
                    picture.setRandomCode(vo.getRandomCode());
                } else {
                    picture.setRandomCode(SnowflakeIdUtil.generateId());
                    picture.setCreateTime(new Date());
                }
                if (StringUtils.isNotEmpty(vo.getPictureUrl())) {
                    picture.setPictureUrl(vo.getPictureUrl());
                }
                picture.setPartCraftMainRandomCode(randomCode);
                picture.setStatus(vo.getStatus() != null ? vo.getStatus() : BusinessConstants.Status.DRAFT_STATUS);
                picture.setRemark(vo.getRemark() != null ? vo.getRemark() : "");
                list.add(picture);
            }
        }
        return list;
    }

    @Override
    public PartCraftMasterData verifyPartCraftInf(PartCraftMasterBasicVo basicVo) throws Exception {
        PartCraftMasterData masterData = buildCraftMasterData(basicVo);
        Boolean result = false;
        //将数据分组，新增的数据重新组装，已存在的数据更新
        List<PartCraftDetail> details = buildPartCraftDetailData(basicVo.getPartCraftDetails(), masterData.getRandomCode(), basicVo.getCreateUser());
        List<PartCraftRule> ruleList = buildPartCraftRule(basicVo.getPartCraftRules(), masterData.getRandomCode(), basicVo.getCreateUser());
        List<PartCraftDesignParts> designPartsList = buildPartCraftDesginParts(basicVo.getCraftDesignParts(), masterData.getRandomCode(), basicVo.getCreateUser());
        List<PartCraftPosition> partCraftPositionList = buildPartCraftPositionData(basicVo.getCraftPositions(), masterData.getRandomCode(), basicVo.getCreateUser());
        List<PartCraftMasterPicture> pictureList = buildPartCraftPictureData(basicVo.getPictures(), masterData.getRandomCode());
        for (PartCraftMasterPicture picture : pictureList) {
            try {
                PartCraftMasterPicture ps = partCraftMasterPictureService.getPartCraftPicture(picture.getRandomCode());
                if (ps != null) {
                    ps.setStatus(picture.getStatus());
                    ps.setPartCraftMainRandomCode(masterData.getRandomCode());
                    ps.setUpdateUser(basicVo.getCreateUser());
                    partCraftMasterPictureService.updateById(ps);
                }
            } catch (Exception ex) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                ex.printStackTrace();
            }
        }
        List<PartCraftDesignParts> removeDesignPartList = new ArrayList<>();
        List<PartCraftPosition> removePositionList = new ArrayList<>();
        details = distinctByPartCraftDetailList(details);
        ruleList = distinctByPartCraftRuleList(ruleList);
        designPartsList = dictinctByDesignPartsList(designPartsList, removeDesignPartList);
        partCraftPositionList = dictinctByPartCrafPositionList(partCraftPositionList, removePositionList);
        if (masterData.getPartType().equalsIgnoreCase(PartCraftPartType.conventionalPartCraft)) {
            if (ObjectUtils.isNotEmptyList(designPartsList)) {
                List<String> codes = new ArrayList<>();
                designPartsList.forEach(part -> codes.add(part.getDesignCode()));
                savePartCraftPartRedisData(masterData.getPartType(), codes, null);
            }
        } else {
            if (ObjectUtils.isNotEmptyList(designPartsList) && ObjectUtils.isNotEmptyList(partCraftPositionList)) {
                List<String> designPartCode = new ArrayList<>();
                List<String> designPartPositionCode = new ArrayList<>();
                designPartsList.forEach(part -> designPartCode.add(part.getDesignCode()));
                partCraftPositionList.forEach(position -> designPartPositionCode.add(position.getPartPositionCode()));
                savePartCraftPartRedisData(masterData.getPartType(), designPartCode, designPartPositionCode);
            }
        }
        int i = partCraftMasterDataDao.updatePaartCraftMaster(masterData);
        if (i > 0) {
            result = true;
        }
        if (ObjectUtils.isNotEmptyList(details)) {
            result = partCraftDetailService.saveOrUpdateBatch(details);
        }

        if (ObjectUtils.isNotEmptyList(ruleList)) {
            result = iPartCraftRuleService.saveOrUpdateBatch(ruleList);
        }

        if (ObjectUtils.isNotEmptyList(designPartsList)) {
            result = partCraftDesignPartsService.saveOrUpdateBatch(designPartsList);
        }

        if (ObjectUtils.isNotEmptyList(partCraftPositionList)) {
            result = partCraftPositionService.saveOrUpdateBatch(partCraftPositionList);
        }
        if (result) {
            if (masterData.getPartType().equalsIgnoreCase(PartCraftPartType.conventionalPartCraft)) {
                if (ObjectUtils.isNotEmptyList(designPartsList)) {
                    List<String> codes = new ArrayList<>();
                    designPartsList.forEach(part -> codes.add(part.getDesignCode()));
                    savePartCraftPartRedisData(masterData.getPartType(), codes, null);
                }
            } else {
                List<String> designPartCode = new ArrayList<>();
                List<String> designPartPositionCode = new ArrayList<>();
                designPartsList.forEach(part -> designPartCode.add(part.getDesignCode()));
                partCraftPositionList.forEach(position -> designPartPositionCode.add(position.getPartPositionCode()));
                savePartCraftPartRedisData(masterData.getPartType(), designPartCode, designPartPositionCode);
            }
        }
        if (result) {
            return masterData;
        } else {
            return null;
        }
    }

    private List<PartCraftDetail> distinctByPartCraftDetailList(List<PartCraftDetail> details) {
        List<PartCraftDetail> queryList = new ArrayList<>();
        if (ObjectUtils.isNotEmptyList(details)) {
            try {
                List<PartCraftDetail> delDetailList = new ArrayList<>();
                for (int i = 0; i < details.size(); i++) {
                    PartCraftDetail detail = details.get(i);
                    if (detail.getStatus().equals(BusinessConstants.Status.INVALID_STATUS)) {
                        delDetailList.add(detail);
                        details.remove(i);
                        i--;
                    }
                }
                if (ObjectUtils.isNotEmptyList(delDetailList)) {
                    delDetailList.forEach(detail -> {
                        if (detail.getId() != null) {
                            partCraftDetailService.removeById(detail.getId());
                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ObjectUtils.isNotEmptyList(details)) {
                List<Long> randoms = new ArrayList<>();
                details.forEach(p -> randoms.add(p.getRandomCode()));
                queryList = partCraftDetailService.getPartCraftDetailRandomList(randoms);
                List<PartCraftDetail> newList = new ArrayList<>();
                if (ObjectUtils.isNotEmptyList(queryList)) {
                    for (PartCraftDetail oldDetail : details) {
                        for (PartCraftDetail detail : queryList) {
                            if (!oldDetail.getCraftCode().equalsIgnoreCase(detail.getCraftCode())) {
                                newList.add(oldDetail);
                            }
                        }
                    }
                }
                if (ObjectUtils.isNotEmptyList(newList)) {
                    newList = newList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(
                            () -> new TreeSet<>(Comparator.comparing(PartCraftDetail::getCraftCode))), ArrayList::new));
                    details = newList;
                }
                if (ObjectUtils.isEmptyList(newList)) {
                    details = details.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(
                            () -> new TreeSet<>(Comparator.comparing(PartCraftDetail::getCraftCode))), ArrayList::new));
                }
            }
        }
        return details;
    }

    private List<PartCraftRule> distinctByPartCraftRuleList(List<PartCraftRule> ruleList) {
        if (ObjectUtils.isNotEmptyList(ruleList)) {
            try {
                List<PartCraftRule> delRuleList = new ArrayList<>();
                for (int i = 0; i < ruleList.size(); i++) {
                    PartCraftRule rule = ruleList.get(i);
                    if (rule.getStatus().equals(BusinessConstants.Status.INVALID_STATUS)) {
                        delRuleList.add(rule);
                        ruleList.remove(i);
                        i--;
                    }
                }
                if (ObjectUtils.isNotEmptyList(delRuleList)) {
                    delRuleList.forEach(partCraftRule -> {
                        if (partCraftRule.getId() != null) {
                            iPartCraftRuleService.removeById(partCraftRule.getId());
                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ruleList;
    }

    private List<PartCraftDesignParts> dictinctByDesignPartsList(List<PartCraftDesignParts> designPartsList,
                                                                 List<PartCraftDesignParts> delDesignPartList) {
        List<PartCraftDesignParts> queryList = new ArrayList<>();
        if (ObjectUtils.isEmptyList(designPartsList)) {
            try {
                if (ObjectUtils.isNotEmptyList(designPartsList)) delDesignPartList = new ArrayList<>();
                for (int i = 0; i < designPartsList.size(); i++) {
                    PartCraftDesignParts designPart = designPartsList.get(i);
                    if (designPart.getStatus().equals(BusinessConstants.Status.INVALID_STATUS) ||
                            designPart.getStatus().equals(BusinessConstants.Status.IN_VALID)) {
                        delDesignPartList.add(designPart);
                        designPartsList.remove(i);
                        i--;
                    }
                }
                if (delDesignPartList.size() > 0) {
                    delDesignPartList.forEach(designParts -> {
                        if (designParts.getId() != null) {
                            partCraftDesignPartsService.removeById(designParts.getId());
                        }
                    });

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ObjectUtils.isNotEmptyList(designPartsList)) {
                List<Long> randoms = new ArrayList<>();
                designPartsList.forEach(p -> randoms.add(p.getRandomCode()));
                queryList = partCraftDesignPartsService.getDesignPartsRandomList(randoms);
                List<PartCraftDesignParts> newList = new ArrayList<>();
                if (ObjectUtils.isNotEmptyList(queryList)) {
                    for (PartCraftDesignParts oldDesignPart : designPartsList) {
                        for (PartCraftDesignParts designParts : queryList) {
                            if (!oldDesignPart.getDesignCode().equalsIgnoreCase(designParts.getDesignCode())) {
                                newList.add(oldDesignPart);
                            }
                        }
                    }
                }
                if (ObjectUtils.isNotEmptyList(newList)) {
                    newList = newList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(
                            () -> new TreeSet<>(Comparator.comparing(PartCraftDesignParts::getDesignCode))), ArrayList::new));
                    designPartsList = newList;
                }
                if (ObjectUtils.isEmptyList(newList)) {
                    designPartsList = designPartsList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(
                            () -> new TreeSet<>(Comparator.comparing(PartCraftDesignParts::getDesignCode))), ArrayList::new));
                }
            }
        }

        return designPartsList;
    }

    private List<PartCraftPosition> dictinctByPartCrafPositionList(List<PartCraftPosition> partCraftPositionList,
                                                                   List<PartCraftPosition> delPositionList) {
        List<PartCraftPosition> queryList = new ArrayList<>();
        if (ObjectUtils.isNotEmptyList(partCraftPositionList)) {
            try {
                if (ObjectUtils.isEmptyList(delPositionList)) delPositionList = new ArrayList<>();
                for (int i = 0; i < partCraftPositionList.size(); i++) {
                    PartCraftPosition position = partCraftPositionList.get(i);
                    if (position.getStatus().equals(BusinessConstants.Status.INVALID_STATUS) ||
                            position.getStatus().equals(BusinessConstants.Status.IN_VALID)) {
                        delPositionList.add(position);
                        partCraftPositionList.remove(i);
                        i--;
                    }
                }

                if (ObjectUtils.isNotEmptyList(delPositionList)) {
                    delPositionList.forEach(position -> {
                        if (position.getId() != null) {
                            partCraftPositionService.removeById(position.getId());
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ObjectUtils.isNotEmptyList(partCraftPositionList)) {
                List<Long> randoms = new ArrayList<>();
                partCraftPositionList.forEach(p -> randoms.add(p.getRandomCode()));
                queryList = partCraftPositionService.getPartCraftPositionRandomList(randoms);
                List<PartCraftPosition> newList = new ArrayList<>();
                if (ObjectUtils.isNotEmptyList(queryList)) {
                    for (PartCraftPosition oldPosition : partCraftPositionList) {
                        for (PartCraftPosition position : queryList) {
                            if (!oldPosition.getPartPositionCode().equalsIgnoreCase(position.getPartPositionCode())) {
                                newList.add(oldPosition);
                            }
                        }
                    }
                }
                if (ObjectUtils.isNotEmptyList(newList)) {
                    Map<String, PartCraftPosition> posMap = new HashMap<>();
                    for (PartCraftPosition pos : newList) {
                        String key = pos.getPartPositionCode() + "-" + pos.getClothingCategoryCode();
                        if (!posMap.containsKey(key)) {
                            posMap.put(key, pos);
                        }
                    }
                    List<PartCraftPosition> temp = new ArrayList<>();
                    temp.addAll(posMap.values());
                    newList = temp;
                   /* newList = newList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(
                            () -> new TreeSet<>(Comparator.comparing(PartCraftPosition::getPartPositionCode))), ArrayList::new));*/
                    partCraftPositionList = newList;
                }
                if (ObjectUtils.isEmptyList(newList)) {
                    Map<String, PartCraftPosition> posMap = new HashMap<>();
                    List<PartCraftPosition> temp = new ArrayList<>();
                    for (PartCraftPosition pos : partCraftPositionList) {
                        String key = pos.getPartPositionCode() + "-" + pos.getClothingCategoryCode();
                        if (!posMap.containsKey(key)) {
                            posMap.put(key, pos);
                        }
                    }
                    temp.addAll(posMap.values());
                    partCraftPositionList = temp;
                    /*partCraftPositionList = partCraftPositionList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(
                            () -> new TreeSet<>(Comparator.comparing(PartCraftPosition::getPartPositionCode))), ArrayList::new));*/
                }
            }
        }
        return partCraftPositionList;
    }


    @Override
    public Boolean invalidPartCraftData(PartCraftMasterBasicVo basicVo) throws Exception {
        Boolean result = false;
        PartCraftMasterData masterData = getPartCraftMasterData(basicVo.getRandomCode());
        if (masterData == null) {
            return result;
        }

        int status = BusinessConstants.Status.INVALID_STATUS;

        List<PartCraftDetail> details = partCraftDetailService.getPartCraftDetailMainList(masterData.getRandomCode());
        if (!details.isEmpty()) {
            for (PartCraftDetail detail : details) {
                detail.setStatus(status);
            }
            result = partCraftDetailService.updateBatchById(details);
        }
        List<PartCraftRule> ruleList = iPartCraftRuleService.getRulesList(masterData.getRandomCode());
        if (!ruleList.isEmpty()) {
            for (PartCraftRule rule : ruleList) {
                rule.setStatus(status);
            }
            result = iPartCraftRuleService.updateBatchById(ruleList);

        }
        List<PartCraftDesignParts> designPartsList = partCraftDesignPartsService.getDesignPartsMainList(masterData.getRandomCode());
        if (!designPartsList.isEmpty()) {
            for (PartCraftDesignParts parts : designPartsList) {
                parts.setStatus(status);
            }
            result = partCraftDesignPartsService.updateBatchById(designPartsList);
        }
        List<PartCraftPosition> partCraftPositionList = partCraftPositionService.getPartCraftPositionMainList(masterData.getRandomCode());
        if (!partCraftPositionList.isEmpty()) {
            for (PartCraftPosition position : partCraftPositionList) {
                position.setStatus(status);
            }
            result = partCraftPositionService.updateBatchById(partCraftPositionList);
        }
        List<PartCraftMasterPicture> pictureList = partCraftMasterPictureService.getPartCraftPictureMainDataList(masterData.getRandomCode());
        if (!pictureList.isEmpty()) {
            for (PartCraftMasterPicture picture : pictureList) {
                picture.setStatus(status);
            }
            result = partCraftMasterPictureService.updateBatchById(pictureList);
        }
        removePartCraftRedisList(basicVo.getPartType(), designPartsList, partCraftPositionList);
        masterData.setStatus(status);
        result = updateById(masterData);
        return result;
    }

    private void removePartCraftRedisList(String type, List<PartCraftDesignParts> designPartsList,
                                          List<PartCraftPosition> partCraftPositionList) {
        List<String> designPartCodes = (List<String>) redisUtil.get(BusinessConstants.Redis.STORE_PART_CRAFT_SEARIAL + type);
        if (ObjectUtils.isNotEmptyList(designPartCodes)) {
            if (ObjectUtils.isNotEmptyList(designPartsList) && ObjectUtils.isEmptyList(partCraftPositionList)) {
                for (int i = 0; i < designPartCodes.size(); i++) {
                    if(designPartCodes.get(i) == null) {
                        continue;
                    }
                    boolean bol = false;
                    for (PartCraftDesignParts designParts : designPartsList) {
                        if (designPartCodes.get(i).equalsIgnoreCase(designParts.getDesignCode())) {
                            bol = true;
                        }
                    }
                    if (bol) {
                        designPartCodes.remove(i);
                        i--;
                    }
                }
            } else if (ObjectUtils.isNotEmptyList(designPartsList) && ObjectUtils.isNotEmptyList(partCraftPositionList)) {
                List<String> desdoc = new ArrayList<>();
                for (PartCraftDesignParts designParts : designPartsList) {
                    for (PartCraftPosition position : partCraftPositionList) {
                        desdoc.add(designParts.getDesignCode() + "#" + position.getPartPositionCode());
                    }
                }
                for (int i = 0; i < designPartCodes.size(); i++) {
                    boolean bol = false;
                    for (String code : desdoc) {
                        if (designPartCodes.get(i).equalsIgnoreCase(code)) {
                            bol = true;
                        }
                    }
                    if (bol) {
                        designPartCodes.remove(i);
                        i--;
                    }
                }
            }
            redisUtil.set(BusinessConstants.Redis.STORE_PART_CRAFT_SEARIAL + type, designPartCodes);
        }
    }


    private List<PartCraftDetail> buildPartCraftDetailData(List<PartCraftDetailVo> partCraftDetailVos, Long mainRandomCode, String userName) {
        List<PartCraftDetail> list = new ArrayList<>();
        if (!ObjectUtils.isEmpty(partCraftDetailVos) && !partCraftDetailVos.isEmpty()) {
            for (PartCraftDetailVo vo : partCraftDetailVos) {
                PartCraftDetail detail = new PartCraftDetail();
                if (vo.getId() != null) {
                    detail.setId(vo.getId());
                }
                if (vo.getRandomCode() != null) {
                    detail.setRandomCode(vo.getRandomCode());
                } else {
                    detail.setRandomCode(SnowflakeIdUtil.generateId());
                    detail.setCreateUser(userName);
                }
                detail.setStatus(vo.getStatus() != null ? vo.getStatus() : BusinessConstants.Status.DRAFT_STATUS);
                detail.setCraftCode(vo.getCraftCode());
                detail.setPad(vo.getPad());
                detail.setCraftRemark(vo.getCraftRemark());
                detail.setCraftFlowNum(vo.getCraftFlowNum());
                detail.setMachineCode(vo.getMachineCode());
                detail.setCraftName(vo.getCraftName());
                detail.setMachineName(vo.getMachineName());
                detail.setStandardTime(vo.getStandardTime());
                detail.setStandardPrice(vo.getStandardPrice());
                detail.setCraftMainFrameCode(vo.getCraftMainFrameCode());
                detail.setPartCraftMainRandomCode(mainRandomCode);
                detail.setRemark(vo.getRemark() == null || vo.getRemark() == "" ? "" : vo.getRemark());
                if (vo.getIsInvalid() != null) {
                    detail.setIsInvalid(vo.getIsInvalid());
                } else {
                    detail.setIsInvalid(false);
                }
                detail.setUpdateUser(userName);
                detail.setUpdateTime(new Date());
                list.add(detail);
            }
        }
        return list;
    }

    /**
     * 组装部件工艺部件位置主数据
     *
     * @param partCraftPositionVoList
     * @param mainRandomCode
     * @return
     */
    private List<PartCraftPosition> buildPartCraftPositionData(List<PartCraftPositionVo> partCraftPositionVoList, Long mainRandomCode, String userName) {
        List<PartCraftPosition> list = new ArrayList<>();
        if (!ObjectUtils.isEmpty(partCraftPositionVoList) && !partCraftPositionVoList.isEmpty()) {
            for (PartCraftPositionVo partPositionVo : partCraftPositionVoList) {
                PartCraftPosition partCraftPosition = new PartCraftPosition();
                if (partPositionVo.getId() != null) {
                    partCraftPosition.setId(partPositionVo.getId());
                }
                if (partPositionVo.getRandomCode() != null) {
                    partCraftPosition.setRandomCode(partPositionVo.getRandomCode());
                } else {
                    partCraftPosition.setRandomCode(SnowflakeIdUtil.generateId());
                    partCraftPosition.setCreateUser(userName);
                    partCraftPosition.setCreateTime(new Date());
                }

                //保存服装品类代码
                partCraftPosition.setClothingCategoryCode(partPositionVo.getClothingCategoryCode());
                partCraftPosition.setClothingCategoryName(partPositionVo.getClothingCategoryName());


                partCraftPosition.setStatus(partPositionVo.getStatus() != null ? partPositionVo.getStatus() :
                        BusinessConstants.Status.DRAFT_STATUS);
                partCraftPosition.setPartPositionCode(partPositionVo.getPartPositionCode());
                partCraftPosition.setPartPositionName(partPositionVo.getPartPositionName());
                partCraftPosition.setPartCraftMainRandomCode(mainRandomCode);
                if (partPositionVo.getIsInvalid() != null) {
                    partCraftPosition.setIsInvalid(partPositionVo.getIsInvalid());
                } else {
                    partCraftPosition.setIsInvalid(false);
                }
                partCraftPosition.setRemark(partPositionVo.getRemark() != null ? partPositionVo.getRemark() : "");
                partCraftPosition.setUpdateUser(userName);
                list.add(partCraftPosition);
            }
        }
        return list;
    }

    /**
     * 组装部件工艺设计部件主数据
     *
     * @param designPartsVoList
     * @param mainRandomCode
     * @return
     */
    private List<PartCraftDesignParts> buildPartCraftDesginParts(List<PartCraftDesignPartsVo> designPartsVoList, Long mainRandomCode, String userName) {
        List<PartCraftDesignParts> list = new ArrayList<>();
        if (!ObjectUtils.isEmpty(designPartsVoList) && !designPartsVoList.isEmpty()) {
            for (PartCraftDesignPartsVo designPartVo : designPartsVoList) {
                PartCraftDesignParts designParts = new PartCraftDesignParts();
                if (designPartVo.getId() != null) {
                    designParts.setId(designPartVo.getId());
                }
                if (designPartVo.getRandomCode() != null) {
                    designParts.setRandomCode(designPartVo.getRandomCode());
                } else {
                    designParts.setRandomCode(SnowflakeIdUtil.generateId());
                    designParts.setCreateUser(userName);
                }
                designParts.setStatus(designPartVo.getStatus() != null ? designPartVo.getStatus() : null);
                designParts.setDesignCode(designPartVo.getDesignCode());
                designParts.setDesignName(designPartVo.getDesignName());
                designParts.setPatternMode(designPartVo.getPatternMode());
                designParts.setPartCraftMainRandomCode(mainRandomCode);
                designParts.setRemark(designPartVo.getRemark() != null ? designPartVo.getRemark() : "");
                if (designPartVo.getIsInvalid() != null) {
                    designParts.setIsInvalid(designPartVo.getIsInvalid());
                } else {
                    designParts.setIsInvalid(false);
                }
                if (StringUtils.isNotBlank(designPartVo.getPatternMode())) {
                    designParts.setPatternMode(designPartVo.getPatternMode());
                }
                if (StringUtils.isNotBlank(designPartVo.getPatternType())) {
                    designParts.setPatternType(designPartVo.getPatternType());
                }
                if (StringUtils.isNotBlank(designParts.getPatternTechnology())) {
                    designParts.setPatternTechnology(designParts.getPatternTechnology());
                }

                designParts.setUpdateUser(userName);
                list.add(designParts);
            }
        }
        return list;
    }

    /**
     * 组装部件工艺规则数据
     *
     * @param ruleVoList
     * @param mainRandomCode
     * @return
     */
    private List<PartCraftRule> buildPartCraftRule(List<PartCraftRuleVo> ruleVoList, Long mainRandomCode, String userName) {
        List<PartCraftRule> rules = new ArrayList<>();
        if (!ObjectUtils.isEmpty(ruleVoList) && !ruleVoList.isEmpty()) {
            for (PartCraftRuleVo ruleVo : ruleVoList) {
                PartCraftRule craftRule = new PartCraftRule();
                if (ruleVo.getId() != null) {
                    craftRule.setId(ruleVo.getId());
                }
                if (ruleVo.getRandomCode() != null) {
                    craftRule.setRandomCode(ruleVo.getRandomCode());
                } else {
                    craftRule.setRandomCode(SnowflakeIdUtil.generateId());
                    craftRule.setCreateUser(userName);
                }
                craftRule.setStatus(ruleVo.getStatus() != null ? ruleVo.getStatus() : BusinessConstants.Status.DRAFT_STATUS);
                craftRule.setSourceCraftCode(ruleVo.getSourceCraftCode());
                craftRule.setSourceCraftName(ruleVo.getSourceCraftName());
                craftRule.setActionCraftCode(ruleVo.getActionCraftCode());
                craftRule.setActionCraftName(ruleVo.getActionCraftName());
                craftRule.setProcessType(ruleVo.getProcessType());
                craftRule.setRemark(ruleVo.getRemark() != null ? ruleVo.getRemark() : "");
                craftRule.setPartCraftMainRandomCode(mainRandomCode);
                craftRule.setProcessingSortNum(ruleVo.getProcessingSortNum());
                if (ruleVo.getIsInvalid() != null) {
                    craftRule.setIsInvalid(ruleVo.getIsInvalid());
                } else {
                    craftRule.setIsInvalid(false);
                }
                craftRule.setUpdateUser(userName);
                craftRule.setUpdateTime(new Date());
                rules.add(craftRule);
            }
        }
        return rules;
    }

    /**
     * 组装部件工艺主数据
     *
     * @param basicVo
     * @return
     */
    private PartCraftMasterData buildCraftMasterData(PartCraftMasterBasicVo basicVo) {
        PartCraftMasterData masterData = new PartCraftMasterData();
        if (basicVo.getId() != null) {
            masterData.setId(basicVo.getId());
        }
        if (basicVo.getRandomCode() != null) {
            masterData.setRandomCode(basicVo.getRandomCode());
        } else {
            masterData.setRandomCode(SnowflakeIdUtil.generateId());
            masterData.setCreateTime(new Date());
            masterData.setCreateUser(basicVo.getCreateUser());
        }
        masterData.setStatus(basicVo.getStatus());
        if (StringUtils.isNotEmpty(basicVo.getPartCraftMainCode())) {
            masterData.setPartCraftMainCode(basicVo.getPartCraftMainCode());
        } else {
            String code = basicVo.getCraftCategoryCode().substring(0, 1) + basicVo.getCraftPartCode().substring(basicVo.getCraftPartCode().length() - 2);
            masterData.setPartCraftMainCode((createSerial(code)));
        }
        masterData.setPartCraftMainName(basicVo.getPartCraftMainName());
        masterData.setCraftCategoryCode(basicVo.getCraftCategoryCode());
        masterData.setCraftPartCode(basicVo.getCraftPartCode());
        masterData.setPartType(basicVo.getPartType());
        masterData.setBusinessType(basicVo.getBusinessType());
        if (basicVo.getStandardTime() != null) {
            masterData.setStandardTime(basicVo.getStandardTime());
        } else {
            masterData.setStandardTime(BigDecimal.ZERO);
        }
        if (basicVo.getStandardPrice() != null) {
            masterData.setStandardPrice(basicVo.getStandardPrice());
        } else {
            masterData.setStandardPrice(BigDecimal.ZERO);
        }
        masterData.setRemark(basicVo.getRemark() != null ? basicVo.getRemark() : "");
        if (basicVo.getIsInvalid() != null) {
            masterData.setIsInvalid(basicVo.getIsInvalid());
        } else {
            masterData.setIsInvalid(false);
        }
        if (basicVo.getStatus().equals(BusinessConstants.Status.PUBLISHED_STATUS) && StringUtils.isNotBlank(basicVo.getReleaseUser())) {
            masterData.setReleaseUser(basicVo.getReleaseUser());
            masterData.setReleaseTime(basicVo.getReleaseTime());
        }

        if (StringUtils.isNotBlank(basicVo.getVersion())) {
            masterData.setVersion(basicVo.getVersion());
        } else {
            masterData.setVersion(DateUtils.formatDate(DateUtils.yyyyMMdd_HHmmssSSS));
        }

        masterData.setUpdateUser(basicVo.getCreateUser());
        masterData.setUpdateTime(new Date());
        return masterData;
    }

    /**
     * 产生序列号
     */
    private String createSerial(String code) {
        String serialNumber = "";
        boolean getLock = false;
        String requestId = UUIDUtil.uuid32();
        String key = BusinessConstants.Redis.STORE_PART_CRAFT_SEARIAL + code;
        try {
            Object serialno = redisTemplate.opsForValue().get(key);
            //第一个序列号生成
            if (null == serialno) {
                serialNumber = String.format("%05d", 1);
                redisTemplate.opsForValue().set(key, 1);
            } else {
                //先把序列号加1；
                redisTemplate.opsForValue().increment(key);
                serialNumber = String.format("%05d", redisTemplate.opsForValue().get(key));
            }
            LOGGER.info("---部件词库编码生成的序列号是:" + serialNumber);
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error("---部件词库编码生成序列号失败");
        }
        return code + serialNumber;
    }


    @Override
    public List<Long> getPartCraftMasterRandomCode(String designCode, String partPostion, String clothingCategoryCode, String businessType) {
        return partCraftMasterDataDao.getPartCraftMasterRandomCode(designCode, partPostion, clothingCategoryCode, businessType);


    }

    @Override
    public PartCraftMasterBasicVo searchPartCraftInfoRandomCode(HashMap param) {
        return partCraftMasterDataDao.searchPartCraftInfoRandomCode(param);
    }


    @Override
    public JSONObject checkPartCraftDesignPartParments(String type, List<String> craftDesignParts) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cpl", false);
        List<String> discountList = new ArrayList<>();
        if (type.equalsIgnoreCase(PartCraftPartType.conventionalPartCraft)) {
            String key = BusinessConstants.Redis.STORE_PART_CRAFT_SEARIAL + type;
            try {
                List<String> redisOldDesignCode = (List<String>) redisUtil.get(key);
                if (ObjectUtils.isNotEmptyList(redisOldDesignCode)) {
                    for (String craftDesignPart : craftDesignParts) {
                        for (String code : redisOldDesignCode) {
                            if (craftDesignPart.equalsIgnoreCase(code)) {
                                discountList.add(code);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (discountList.size() > 0) {
            discountList.stream().distinct().collect(Collectors.toList());
            jsonObject.put("cpl", true);
            jsonObject.put("msg", "当前设计部件已在其它部件工艺中存在");
            jsonObject.put("parts", discountList);
        }
        return jsonObject;
    }

    @Override
    public JSONObject checkPartCraftDesignPartParments(List<PartCraftDesignPartsVo> designPartCodesList) {
        JSONObject json = new JSONObject();
        json.put("cpl", false);
        boolean isRepeat = false;
        Map<String, List<PartCraftDesignPartsVo>> groupByDesignParts = designPartCodesList.stream()
                .collect(Collectors.groupingBy(PartCraftDesignPartsVo::getDesignCode));

        for (String key : groupByDesignParts.keySet()) {
            if (groupByDesignParts.get(key).size() > 1) {
                isRepeat = true;
                break;
            }
        }
        if (isRepeat) {
            json.put("cpl", true);
            json.put("msg", "当前部件工艺设计部件列表数据重复");
        }
        return json;
    }

    @Override
    public JSONObject checkPartCraftPositionParments(List<PartCraftPositionVo> positionCodeList) {
        JSONObject json = new JSONObject();
        json.put("cpl", false);
        boolean isRepeat = false;
        /*Map<String, List<PartCraftPositionVo>> groupByPositionList = positionCodeList.stream()
                .collect(Collectors.groupingBy(PartCraftPositionVo::getPartPositionCode));
        for (String key : groupByPositionList.keySet()) {
            if (groupByPositionList.get(key).size() > 1) {
                isRepeat = true;
                break;
            }
        }*/
        if(null != positionCodeList && positionCodeList.size()>0){
            Map<String, String> posMap = new HashMap<>();
            for(PartCraftPositionVo pos : positionCodeList){
                String key = pos.getPartPositionCode() + "-" + pos.getClothingCategoryCode();
                if(posMap.containsKey(key)){
                    isRepeat = true;
                    break;
                }else{
                    posMap.put(key,key);
                }
            }
        }
        if (isRepeat) {
            json.put("cpl", true);
            json.put("msg", "当前部件工艺部件位置列表数据重复");
        }
        return json;
    }

    @Override
    public JSONObject checkPartCraftDesignPartPositionParments(String type, List<String> designPartCodesList,
                                                               List<String> positionCodeList) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cpl", false);
        String key = BusinessConstants.Redis.STORE_PART_CRAFT_SEARIAL + type;
        List<String> combCodes = new ArrayList<>();
        try {
            List<String> redisOldDesignCode = (List<String>) redisUtil.get(key);
            List<String> newDesingPartList = getBuildPartCodes(designPartCodesList, positionCodeList);
            if (ObjectUtils.isNotEmptyList(redisOldDesignCode)) {
                for (String code : redisOldDesignCode) {
                    for (String cos : newDesingPartList) {
                        if (code.equalsIgnoreCase(cos)) {
                            combCodes.add(code);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (combCodes.size() > 0) {
            combCodes.stream().distinct().collect(Collectors.toList());
            jsonObject.put("cpl", true);
            jsonObject.put("msg", "设计部件或者设计部件组合存在重复数据");
            jsonObject.put("CombParts", combCodes);
        }
        return jsonObject;
    }

    @Override
    public Boolean checkPartCraftRulePaements(List<PartCraftRuleVo> partCraftRuleVoList) {
        Boolean res = false;
        for (PartCraftRuleVo rule : partCraftRuleVoList) {
            if (rule.getProcessType().equals(RuleProcessType.PROCESS_TYPE_REPLACE) &&
                    StringUtils.isEmpty(rule.getSourceCraftCode())) {
                res = true;
                break;
            }
        }
        return res;
    }

    @Override
    public Boolean checkPartCraftDetailPaements(List<PartCraftDetailVo> partCraftDetailVoList) {
        Boolean isReapt = false;
        if (partCraftDetailVoList.size() > 1) {
            List<PartCraftDetailVo> detailVos = new ArrayList<>();
            partCraftDetailVoList.forEach(del -> {
                if (!del.getStatus().equals(BusinessConstants.Status.INVALID_STATUS)) {
                    detailVos.add(del);
                }
            });
            if (detailVos.size() > 0) {
                Map<String, List<PartCraftDetailVo>> groupCraftDetailMaps = detailVos.stream().collect(Collectors.groupingBy(PartCraftDetailVo::getCraftCode));
                for (String code : groupCraftDetailMaps.keySet()) {
                    if (groupCraftDetailMaps.get(code).size() > 1) {
                        isReapt = true;
                        break;
                    }
                }
            }
        }
        return isReapt;
    }

    /**
     * 保存设计部件和部件位置编码到缓存中
     *
     * @param type
     * @param designPartCodesList
     * @param positionCodeList
     */
    private void savePartCraftPartRedisData(String type, List<String> designPartCodesList,
                                            List<String> positionCodeList) {

        if (type.equalsIgnoreCase(PartCraftPartType.conventionalPartCraft)) {
            String key = BusinessConstants.Redis.STORE_PART_CRAFT_SEARIAL + type;
            List<String> redisOldDesignCode = (List<String>) redisUtil.get(key);
            if (ObjectUtils.isNotEmptyList(redisOldDesignCode)) {
                redisOldDesignCode.addAll(designPartCodesList);
                List<String> listAllDistinct = redisOldDesignCode.stream().distinct().collect(toList());
                redisUtil.set(key, listAllDistinct);
            } else {
                redisUtil.set(key, designPartCodesList);
            }
        }
        if (type.equalsIgnoreCase(PartCraftPartType.threadTracePartCraft)) {
            String key = BusinessConstants.Redis.STORE_PART_CRAFT_SEARIAL + type;
            List<String> redisOldDesignCode = (List<String>) redisUtil.get(key);
            List<String> newDesingPartList = getBuildPartCodes(designPartCodesList, positionCodeList);
            if (ObjectUtils.isNotEmptyList(redisOldDesignCode)) {
                redisOldDesignCode.addAll(newDesingPartList);
                List<String> listAllDistinct = redisOldDesignCode.stream().distinct().collect(toList());
                redisUtil.set(key, listAllDistinct);
            } else {
                redisUtil.set(key, newDesingPartList);
            }
        }
        if (type.equalsIgnoreCase(PartCraftPartType.decorativePartCraft)) {
            String key = BusinessConstants.Redis.STORE_PART_CRAFT_SEARIAL + type;
            List<String> redisOldDesignCode = (List<String>) redisUtil.get(key);
            List<String> newDesingPartList = getBuildPartCodes(designPartCodesList, positionCodeList);
            if (ObjectUtils.isNotEmptyList(redisOldDesignCode)) {
                redisOldDesignCode.addAll(newDesingPartList);
                List<String> listAllDistinct = redisOldDesignCode.stream().distinct().collect(toList());
                redisUtil.set(key, listAllDistinct);
            } else {
                redisUtil.set(key, newDesingPartList);
            }
        }
        if (type.equalsIgnoreCase(PartCraftPartType.patternPartCraft)) {
            String key = BusinessConstants.Redis.STORE_PART_CRAFT_SEARIAL + type;
            List<String> redisOldDesignCode = (List<String>) redisUtil.get(key);
            List<String> newDesingPartList = getBuildPartCodes(designPartCodesList, positionCodeList);
            if (ObjectUtils.isNotEmptyList(redisOldDesignCode)) {
                redisOldDesignCode.addAll(newDesingPartList);
                List<String> listAllDistinct = redisOldDesignCode.stream().distinct().collect(toList());
                redisUtil.set(key, listAllDistinct);
            } else {
                redisUtil.set(key, newDesingPartList);
            }
        }
        if (type.equalsIgnoreCase(PartCraftPartType.hiddenPartCraft)) {
            String key = BusinessConstants.Redis.STORE_PART_CRAFT_SEARIAL + type;
            List<String> redisOldDesignCode = (List<String>) redisUtil.get(key);
            List<String> newDesingPartList = getBuildPartCodes(designPartCodesList, positionCodeList);
            if (ObjectUtils.isNotEmptyList(redisOldDesignCode)) {
                redisOldDesignCode.addAll(newDesingPartList);
                List<String> listAllDistinct = redisOldDesignCode.stream().distinct().collect(toList());
                redisUtil.set(key, listAllDistinct);
            } else {
                redisUtil.set(key, newDesingPartList);
            }
        }

    }


    private List<String> getBuildPartCodes(List<String> designPartCodesList, List<String> positionCodeList) {
        List<String> newDesingPartList = new ArrayList<>();
        try {
            designPartCodesList.forEach(partCode -> {
                positionCodeList.forEach(positonCode -> {
                    String newCode = partCode + "#" + positonCode;
                    newDesingPartList.add(newCode);
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newDesingPartList;
    }

    @Override
    public List<String> getPartCraftCodeNameAll() {
        return partCraftMasterDataDao.getPartCraftCodeNameAll();
    }

    @Override
    public boolean isThinkStyleUsed(long randomCode) {
        return partCraftMasterDataDao.isThinkStyleUsed(randomCode);
    }

    @Override
    public boolean isDelDesignPartUsed(Long masterRandomCode, List<String> designCodes, List<String> positionCodes) {
        return partCraftMasterDataDao.isDelDesignPartUsed(masterRandomCode, designCodes, positionCodes);
    }

    @Override
    public boolean isPartCraftNameUsed(Long masterRandomCode, String categoryCode, String partCraftName) {
        return partCraftMasterDataDao.isPartCraftNameUsed(masterRandomCode, categoryCode, partCraftName);

    }

    @Override
    public Integer updateStatusByDesignPartCode(Integer status, String designPartCode) {
        return partCraftMasterDataDao.updateStatusByDesignPartCode(status, designPartCode);
    }
}