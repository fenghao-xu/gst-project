package com.ylzs.service.materialCraft.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.constant.RedisContants;
import com.ylzs.common.util.*;
import com.ylzs.dao.materialCraft.MaterialCraftDao;
import com.ylzs.dao.materialCraft.MaterialCraftPropertyDao;
import com.ylzs.dao.materialCraft.MaterialCraftRuleDao;
import com.ylzs.dao.materialCraft.MaterialCraftRulePartDao;
import com.ylzs.dao.system.UserDao;
import com.ylzs.entity.materialCraft.MaterialCraft;
import com.ylzs.entity.materialCraft.MaterialCraftProperty;
import com.ylzs.entity.materialCraft.MaterialCraftRule;
import com.ylzs.entity.materialCraft.MaterialCraftRulePart;
import com.ylzs.entity.materialCraft.req.MaterialCraftReq;
import com.ylzs.entity.materialCraft.req.MaterialCraftRuleAndPartReq;
import com.ylzs.entity.materialCraft.req.QueryMaterialCraftReq;
import com.ylzs.entity.materialCraft.resp.*;
import com.ylzs.entity.partCombCraft.req.UpdatePartCombCraftReq;
import com.ylzs.entity.system.User;
import com.ylzs.service.impl.OriginServiceImpl;
import com.ylzs.service.materialCraft.IMaterialCraftService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * @author weikang
 * @Description 材料工艺
 * @Date 2020/3/5
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MaterialCraftServiceImpl extends OriginServiceImpl<MaterialCraftDao, MaterialCraft> implements IMaterialCraftService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaterialCraftServiceImpl.class);

    @Resource
    private MaterialCraftDao materialCraftDao;

    @Resource
    private MaterialCraftRuleDao materialCraftRuleDao;

    @Resource
    private MaterialCraftPropertyDao craftPropertyDao;

    @Resource
    private MaterialCraftRulePartDao rulePartDao;

    @Resource
    private ThreadPoolTaskExecutor taskExecutor;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private UserDao userDao;


    /**
     * 在新建页面新建草稿，草稿规则：
     * 草稿只有一份，有修改材料类型及材料属性，则生成新的工艺编码，如果没有，覆盖之前的草稿
     *
     * @param req
     * @return
     */
    @Override
    public Result addDraftMaterialCraft(MaterialCraftReq req) {
        MaterialCraft craft = req.getMaterialCraft();
        if (craft == null) {
            LOGGER.info("addDraftMaterialCraft 材料工艺主数据为空");
            return Result.build(BusinessConstants.CommonConstant.ZERO, "材料工艺主数据为空");
        }
        List<MaterialCraftRuleAndPartReq> ruleAndPartReqList = req.getCraftRuleAndPartListReq().getRuleAndPartReqList();
        if (CollUtil.isEmpty(ruleAndPartReqList)) {
            LOGGER.info("addDraftMaterialCraft 材料工艺规则数据为空");
            return Result.build(BusinessConstants.CommonConstant.ZERO, "规则数据为空");
        }
        List<MaterialCraftProperty> craftProperty = req.getCraftProperty();
        if (CollUtil.isEmpty(craftProperty)) {
            LOGGER.info("addDraftMaterialCraft 材料工艺属性值为空");
            return Result.build(BusinessConstants.CommonConstant.ZERO, "属性值为空");
        }
        String userCode = req.getUserCode();
        if (StringUtils.isBlank(userCode)) {
            return Result.build(BusinessConstants.CommonConstant.ZERO, "用户信息为空，不能进行操作");
        }
        if (StringUtils.isBlank(craft.getMaterialVersion())) {
            return Result.build(BusinessConstants.CommonConstant.ZERO, "版本号不能为空");
        }
        List<MaterialCraftAndPropertyResp> oriMaterialCraft = selectCraftAndProperty(req, craft);
        craft.setStatus(BusinessConstants.Status.DRAFT_STATUS);
        String materialCraftCode = craft.getMaterialCraftCode();
        //校验材料属性
        if (StringUtils.isNotBlank(req.getFabricPropertyCodes())) {
            String[] split = req.getFabricPropertyCodes().split(",");
            List<String> propertyList = new ArrayList<>(Arrays.asList(split));
            HashSet<String> set = new HashSet<>(propertyList);
            Boolean propertyResult = set.size() == propertyList.size() ? true : false;
            if (!propertyResult) {
                return Result.build(BusinessConstants.CommonConstant.ZERO, "材料属性不能重复");
            }
        }
        //在另存为草稿时需要校验版本号
        if (StringUtils.isNotBlank(materialCraftCode) && req.getIsEditPage()) {
            String latestVersion = materialCraftDao.selectLatestVersion(materialCraftCode);
            if (StringUtils.isNotBlank(latestVersion)) {
                latestVersion = latestVersion.substring(1);
                String version = craft.getMaterialVersion().substring(1);
                if (Double.valueOf(version) <= Double.valueOf(latestVersion)) {
                    return Result.build(BusinessConstants.CommonConstant.ZERO, "版本号不是最新的");
                }
            }
        }
        //在新建页面校验名称
        if (!req.getIsEditPage()) {
            Integer craftCount = materialCraftDao.selectNameCount(req.getRandomCode(),
                    craft.getMaterialCraftName(), craft.getClothingCategoryCode());
            if (craftCount.intValue() > BusinessConstants.CommonConstant.ZERO) {
                return Result.build(BusinessConstants.CommonConstant.ZERO, "工艺名称重复");
            }
        }
        if (CollUtil.isNotEmpty(oriMaterialCraft)) {
            //表示新建页面
            if (!req.getIsEditPage()) {
                //排除不等于工序编码的草稿
                List<MaterialCraftAndPropertyResp> collect = oriMaterialCraft.stream().filter(
                        craftList -> !craftList.getMaterialCraftCode().equals(materialCraftCode)
                ).collect(Collectors.toList());

                if (collect.size() > BusinessConstants.CommonConstant.ZERO) {
                    LOGGER.error("MaterialCraftServiceImpl.addDraftMaterialCraft 材料工艺存在多份【{}】", craft.toString());
                    return Result.build(BusinessConstants.CommonConstant.ZERO, "材料工艺存在多份");
                }
            }
            oriMaterialCraft = oriMaterialCraft.stream().filter(craftList ->
                    craftList.getStatus().intValue() == BusinessConstants.Status.DRAFT_STATUS.intValue()
                            && craftList.getMaterialCraftCode().equals(materialCraftCode)
            ).collect(Collectors.toList());

            if (oriMaterialCraft.size() == BusinessConstants.CommonConstant.ZERO) {
                Map<String, Object> craftMap = insertMaterialCraft(userCode, craft, materialCraftCode);
                if (craftMap.get("flag").equals(BusinessConstants.CommonConstant.ZERO)) {
                    LOGGER.error("新增材料工艺草稿主数据失败【{}】", craft.toString());
                    return Result.build(BusinessConstants.CommonConstant.ZERO, "新增工艺草稿主数据失败");
                }
                craft = (MaterialCraft) craftMap.get("craft");
            } else if (oriMaterialCraft.size() == BusinessConstants.CommonConstant.ONE) {
                //覆盖之前的草稿
                if (CollUtil.isNotEmpty(oriMaterialCraft)) {
                    craft.setUpdateUser(userCode);
                    Result uResult = updateDraftData(craft, oriMaterialCraft);
                    if (uResult != null) return uResult;
                }
            } else if (oriMaterialCraft.size() >= BusinessConstants.CommonConstant.ONE) {
                LOGGER.error("MaterialCraftServiceImpl.addDraftMaterialCraft 材料工艺存在多份【{}】", craft.toString());
                return Result.build(BusinessConstants.CommonConstant.ZERO, "材料工艺存在多份");
            }
        } else {
            if (!req.getIsEditPage() && StringUtils.isNotBlank(materialCraftCode)) {
                //覆盖之前的草稿
                craft.setUpdateUser(userCode);
                craft.setMaterialCraftCode(materialCraftCode);
                craft.setRandomCode(req.getRandomCode());
                UpdateWrapper<MaterialCraft> craftUpdateWrapper = new UpdateWrapper<>();
                craftUpdateWrapper.eq("random_code", req.getRandomCode());
                int uCraft = materialCraftDao.update(craft, craftUpdateWrapper);
                if (uCraft == BusinessConstants.CommonConstant.ZERO) {
                    LOGGER.error("覆盖材料工艺主数据失败【{}】", craft.toString());
                    return Result.build(BusinessConstants.CommonConstant.ZERO, "覆盖工艺主数据失败");
                }
                Result deleteRes = deleteMaterialRelatedData(craft, req.getRandomCode());
                if (deleteRes != null) return deleteRes;
                return insertMaterialCraftData(userCode, craft, ruleAndPartReqList, craftProperty);
            }
            Map<String, Object> craftMap = insertMaterialCraft(userCode, craft, materialCraftCode);
            if (craftMap.get("flag").equals(BusinessConstants.CommonConstant.ZERO)) {
                LOGGER.error("新增材料工艺草稿主数据失败【{}】", craft.toString());
                return Result.build(BusinessConstants.CommonConstant.ZERO, "新增工艺草稿主数据失败");
            }
            craft = (MaterialCraft) craftMap.get("craft");
        }
        return insertMaterialCraftData(userCode, craft, ruleAndPartReqList, craftProperty);
    }

    /**
     * 发布
     *
     * @param req
     * @return
     */
    @Override
    public Result addPublishMaterialCraft(MaterialCraftReq req) {
        MaterialCraft craft = req.getMaterialCraft();
        if (craft == null) {
            LOGGER.info("addPublishMaterialCraft 材料工艺主数据为空");
            return Result.build(BusinessConstants.CommonConstant.ZERO, "主数据为空");
        }
        List<MaterialCraftRuleAndPartReq> ruleAndPartReqList = req.getCraftRuleAndPartListReq().getRuleAndPartReqList();
        if (CollUtil.isEmpty(ruleAndPartReqList)) {
            LOGGER.info("addPublishMaterialCraft 材料工艺规则数据为空", craft.toString());
            return Result.build(BusinessConstants.CommonConstant.ZERO, "规则数据为空");
        }
        List<MaterialCraftProperty> craftProperty = req.getCraftProperty();
        if (CollUtil.isEmpty(craftProperty)) {
            LOGGER.info("addPublishMaterialCraft 材料工艺属性值为空", craft.toString());
            return Result.build(BusinessConstants.CommonConstant.ZERO, "属性值为空");
        }
        String userCode = req.getUserCode();
        if (StringUtils.isBlank(userCode)) {
            return Result.build(BusinessConstants.CommonConstant.ZERO, "用户信息为空，不能进行操作");
        }
        if (StringUtils.isBlank(craft.getMaterialVersion())) {
            return Result.build(BusinessConstants.CommonConstant.ZERO, "版本号不能为空");
        }
        //从另存为草稿页面保存，不需校验名称
        if (!req.getIsEditPage()) {
            Integer craftCount = materialCraftDao.selectNameCount(req.getRandomCode(),
                    craft.getMaterialCraftName(),
                    craft.getClothingCategoryCode());
            if (craftCount.intValue() > BusinessConstants.CommonConstant.ZERO) {
                return Result.build(BusinessConstants.CommonConstant.ZERO, "工艺名称重复");
            }
        }
        //在另存为草稿时需要校验版本号
        if (StringUtils.isNotBlank(req.getMaterialCraft().getMaterialCraftCode()) && req.getIsEditPage()) {
            String latestVersion = materialCraftDao.selectLatestVersion(req.getMaterialCraft().getMaterialCraftCode());
            if (StringUtils.isNotBlank(latestVersion)) {
                latestVersion = latestVersion.substring(1);
                String version = craft.getMaterialVersion().substring(1);
                if (Double.valueOf(version) <= Double.valueOf(latestVersion)) {
                    return Result.build(BusinessConstants.CommonConstant.ZERO, "版本号不是最新的");
                }
            }
        }
        List<MaterialCraftAndPropertyResp> oriMaterialCraftList = selectCraftAndProperty(req, craft);
        String materialCraftCode = null;
        setStatus(craft, userCode);
        if (CollUtil.isNotEmpty(oriMaterialCraftList)) {
            //查草稿
            List<MaterialCraftAndPropertyResp> collectDraft = oriMaterialCraftList.stream().filter(craftList ->
                    craftList.getStatus().intValue() == BusinessConstants.Status.DRAFT_STATUS.intValue()
            ).collect(Collectors.toList());
            if (collectDraft.size() > BusinessConstants.CommonConstant.ONE) {
                LOGGER.error("MaterialCraftServiceImpl.addPublishMaterialCraft 材料工艺草稿存在多份【{}】", craft.toString());
                return Result.build(BusinessConstants.CommonConstant.ZERO, "发布材料工艺，系统错误");
            }
            if (CollUtil.isNotEmpty(collectDraft)) {
                Result uResult = updateDraftData(craft, collectDraft);
                if (uResult != null) return uResult;
            }

            List<MaterialCraftAndPropertyResp> collectPublish = new ArrayList<>();
            //如果是立即发布，则之前生效的改为失效,即将生效的不用管
            if (craft.getStatus().intValue() == BusinessConstants.Status.PUBLISHED_STATUS) {
                collectPublish = oriMaterialCraftList.stream().filter(craftList ->
//                        craftList.getStatus().intValue() == BusinessConstants.Status.NOT_ACTIVE_STATUS.intValue() ||
                                craftList.getStatus().intValue() == BusinessConstants.Status.PUBLISHED_STATUS.intValue()
                ).collect(Collectors.toList());
                //如果是即将生效，之前即将生效的改为失效，生效的不用管
            } else if (craft.getStatus().intValue() == BusinessConstants.Status.NOT_ACTIVE_STATUS) {
                collectPublish = oriMaterialCraftList.stream().filter(craftList ->
                                craftList.getStatus().intValue() == BusinessConstants.Status.NOT_ACTIVE_STATUS.intValue()
//                                craftList.getStatus().intValue() == BusinessConstants.Status.PUBLISHED_STATUS.intValue()
                ).collect(Collectors.toList());
            }
            //修改材料工艺数据为失效
            if (CollUtil.isNotEmpty(collectPublish)) {
                UpdatePartCombCraftReq updatePartCombCraft = new UpdatePartCombCraftReq();
                updatePartCombCraft.setUserCode(userCode);
                updatePartCombCraft.setOperateType(BusinessConstants.CommonConstant.TWO);
                for (MaterialCraftAndPropertyResp propertyResp : collectPublish) {
                    updatePartCombCraft.setRandomCode(propertyResp.getMaterialCraftRandomCode());
                    Result result = updateCraftStatus(updatePartCombCraft);
                    if (result.getCode() != BusinessConstants.CommonConstant.ONE) {
                        return result;
                    }
                }
                materialCraftCode = oriMaterialCraftList.get(0).getMaterialCraftCode();
            }
            if (CollUtil.isNotEmpty(collectDraft)) {
                return insertMaterialCraftData(userCode, craft, ruleAndPartReqList, craftProperty);
            }
        }
        return getDataResult(userCode, craft, ruleAndPartReqList, craftProperty, materialCraftCode);
    }

    /**
     * 修改材料工艺状态
     *
     * @param req
     * @return
     */
    @Override
    public Result updateMaterialCraftStatus(UpdatePartCombCraftReq req) {
        return updateCraftStatus(req);
    }

    @Override
    public List<QueryMaterialCraftDataResp> selectMaterialCraftChecklist(QueryMaterialCraftReq craftReq) {
        Map<String, Object> params = new HashMap<>();
        params.put("status", craftReq.getStatus());
        params.put("materialCraftCodeAndName", craftReq.getMaterialCraftCodeAndName());
        if (StringUtils.isNotBlank(craftReq.getFabricPropertyCodes())) {
            params.put("fabricPropertyCodes", new ArrayList<String>(Arrays.asList(craftReq.getFabricPropertyCodes().split(","))));
        } else {
            params.put("fabricPropertyCodes", new ArrayList<>());
        }
        if (StringUtils.isNotBlank(craftReq.getMaterialCraftKindCodes())) {
            params.put("materialCraftKindCodes", new ArrayList<String>(Arrays.asList(craftReq.getMaterialCraftKindCodes().split(","))));
        } else {
            params.put("materialCraftKindCodes", new ArrayList<String>());
        }
        List<QueryMaterialCraftDataResp> craftDataResp = materialCraftDao.selectMaterialCraftChecklist(params);
        return craftDataResp;
    }

    @Override
    public Result selectMaterialCraftData(String randomCode) {
        CountDownLatch countDownLatch = new CountDownLatch(BusinessConstants.CommonConstant.FOUR);
        MaterialCraftData data = new MaterialCraftData();
        //查询材料工艺主数据
        taskExecutor.execute(() -> {
            try {
                MaterialCraft craft = materialCraftDao.selectMaterialCraft(Long.parseLong(randomCode));
                if (craft != null) data.setMaterialCraft(craft);
            } catch (Exception e) {
                LOGGER.error("selectMaterialCraftData.materialCraftDao.selectOne fail", e);
            } finally {
                countDownLatch.countDown();
            }
        });

        //查询材料工艺属性值
        taskExecutor.execute(() -> {
            try {
                QueryWrapper<MaterialCraftProperty> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("material_craft_random_code", randomCode);
                List<MaterialCraftProperty> properties = craftPropertyDao.selectList(queryWrapper);
                if (CollUtil.isNotEmpty(properties)) data.setPropertyResps(properties);
            } catch (Exception e) {
                LOGGER.error("selectMaterialCraftData.selectListByCraftRandomCode fail", e);
            } finally {
                countDownLatch.countDown();
            }
        });

        //查询材料工艺规则
        taskExecutor.execute(() -> {
            try {
                Map<Integer, MaterialCraftRuleAndPartResp> map = getRulePartByRandomCode(Long.parseLong(randomCode));
                if (map != null) data.setMap(map);
            } catch (Exception e) {
                LOGGER.error("selectMaterialCraftData.MaterialCraftRuleAndPartResp fail", e);
            } finally {
                countDownLatch.countDown();
            }
        });

        //版本历史记录
        taskExecutor.execute(() -> {
            try {
                List<MaterialCraftHistoryVersionResp> versionResps = materialCraftDao.selectHistoryByRandomCode(Long.parseLong(randomCode));
                if (CollUtil.isNotEmpty(versionResps)) data.setHistoryVersionResps(versionResps);
            } catch (Exception e) {
                LOGGER.error("selectMaterialCraftData.selectHistoryByRandomCode fail", e);
            } finally {
                countDownLatch.countDown();
            }
        });

        try {
            countDownLatch.await();
        } catch (Exception e) {
            LOGGER.error("selectMaterialCraftData system error", e);
            return Result.fail(0);
        }

        return Result.ok(data);
    }

    @Override
    public int updatePublishStatus() {
        return materialCraftDao.updatePublishStatus();
    }

    @Override
    public int updateNotActiveStatus(List<Long> list) {
        return materialCraftDao.updateNotActiveStatus(list);
    }

    @Override
    public List<Long> selectPublishRandomCode() {
        return materialCraftDao.selectPublishRandomCode();
    }

    @Override
    public List<Long> selectRandomCode() {
        return materialCraftDao.selectRandomCode();
    }

    @Override
    public Result selectMaterialCraftRulePart(String randomCode) {
        return Result.ok(getRulePartByRandomCode(Long.parseLong(randomCode)));
    }

    @Override
    public List<MaterialCraftAllDataResp> selectMaterialCraftDataByKindCode(String kindCode) {
        List<MaterialCraftAllDataResp> resps = CollUtil.newArrayList();
        try {
            resps = materialCraftDao.selectListByKindCode(kindCode);
            if (StringUtils.isBlank(kindCode)) {
                return null;
            }
        } catch (Exception e) {
            LOGGER.info("selectMaterialCraftDataByKindCode fails", e);
        }
        return resps;
    }

    /**
     * 查询材料工艺相关数据
     *
     * @param req
     * @param craft
     * @return
     */
    private List<MaterialCraftAndPropertyResp> selectCraftAndProperty(MaterialCraftReq req, MaterialCraft craft) {
        List<FabricPropertyAndPropertyResp> propertyResps = materialCraftDao.selectPropertyAndCategoryCode(craft.getStatus());
        List<Long> collect = new ArrayList<>();
        List<FabricPropertyAndPropertyResp> newList = new ArrayList<>();
        if (CollUtil.isNotEmpty(propertyResps)) {
            propertyResps.stream().forEach(resp -> {
                FabricPropertyAndPropertyResp newPropertyResp = new FabricPropertyAndPropertyResp();
                String[] arrayDataCodes = resp.getFabricPropertyDataCodes().split(",");
                Arrays.sort(arrayDataCodes);
                newPropertyResp.setFabricPropertyDataCodes(StringUtils.join(arrayDataCodes, ","));
                String[] arrayPropertyCodes = resp.getFabricPropertyCodes().split(",");
                Arrays.sort(arrayPropertyCodes);
                newPropertyResp.setFabricPropertyCodes(StringUtils.join(arrayPropertyCodes, ","));
                newPropertyResp.setClothingCategoryCode(resp.getClothingCategoryCode());
                newPropertyResp.setMaterialCraftKindCode(resp.getMaterialCraftKindCode());
                newPropertyResp.setRandomCode(resp.getRandomCode());
                newList.add(newPropertyResp);
            });
            collect = newList.stream().filter(resp ->
                    req.getFabricPropertyCodes().equals(resp.getFabricPropertyCodes())
                            && req.getFabricPropertyDataCodes().equals(resp.getFabricPropertyDataCodes())
                            && craft.getMaterialCraftKindCode().equals(resp.getMaterialCraftKindCode())
                            && craft.getClothingCategoryCode().equals(resp.getClothingCategoryCode())
            ).map(FabricPropertyAndPropertyResp::getRandomCode).collect(Collectors.toList());
        }
        List<MaterialCraftAndPropertyResp> oriMaterialCraftList = new ArrayList<>();
        if (CollUtil.isNotEmpty(collect)) {
            oriMaterialCraftList = materialCraftDao.selectCraftAndProperty(collect);
        }
        return oriMaterialCraftList;
    }

    /**
     * 根据材料工艺randomCode查询材料工艺规则及部件
     *
     * @param randomCode
     * @return
     */
    private Map<Integer, MaterialCraftRuleAndPartResp> getRulePartByRandomCode(Long randomCode) {
        //获取材料工艺方案编号
        QueryWrapper<MaterialCraft> materialCraftQueryWrapper = new QueryWrapper<>();
        materialCraftQueryWrapper.eq("random_code", randomCode);
        MaterialCraft craft = materialCraftDao.selectOne(materialCraftQueryWrapper);
        String[] planNumbers = craft.getPlanNumber().split(",");
        LOGGER.info("材料工艺方案编号【{}】", planNumbers);

        //查询材料规则数据
        Map<Integer, List<MaterialCraftRule>> sewingCraftCollect = new HashMap<>();
        List<MaterialCraftRule> craftRules = materialCraftRuleDao.selectRuleListByCraftRandomCode(randomCode);
        if (CollUtil.isNotEmpty(craftRules)) {
            sewingCraftCollect = craftRules.stream().
                    collect(Collectors.groupingBy(MaterialCraftRule::getSpecialPlanNumber));
            LOGGER.info("获取材料规则数据完成[{}]", sewingCraftCollect);
        }

        //查询设计部件数据
        Map<Integer, List<QueryMaterialCraftRulePartResp>> partPostitionCollect = new HashMap<>();
        List<QueryMaterialCraftRulePartResp> ruleParts = rulePartDao.selectListByCraftRandomCode(randomCode);
        if (CollUtil.isNotEmpty(ruleParts)) {
            //部件和位置
            partPostitionCollect = ruleParts.stream().collect(Collectors.groupingBy(QueryMaterialCraftRulePartResp::getSpecialPlanNumber));
            LOGGER.info("获取设计部件数据完成[{}]", partPostitionCollect);
        }

        Map<Integer, MaterialCraftRuleAndPartResp> map = new HashMap<>();
        //归类
        for (String planNumber : planNumbers) {
            MaterialCraftRuleAndPartResp ruleAndPartResp = new MaterialCraftRuleAndPartResp();
            if (CollUtil.isNotEmpty(sewingCraftCollect.get(Integer.valueOf(planNumber)))) {
                ruleAndPartResp.setSewingCraftResp(sewingCraftCollect.get(Integer.valueOf(planNumber)));
            }
            if (CollUtil.isNotEmpty(partPostitionCollect.get(Integer.valueOf(planNumber)))) {
                ruleAndPartResp.setRuleParts(partPostitionCollect.get(Integer.valueOf(planNumber)));
            }
            map.put(Integer.valueOf(planNumber), ruleAndPartResp);
        }
        return map;
    }

    /**
     * 修改材料工艺失效/作废
     *
     * @param req
     * @return
     */
    private Result updateCraftStatus(UpdatePartCombCraftReq req) {
        MaterialCraft craft = new MaterialCraft();
        //草稿 -->删除作废
        if (req.getOperateType().intValue() == BusinessConstants.CommonConstant.ONE) {
            craft.setStatus(BusinessConstants.Status.IN_VALID);
            //发布-->失效
        } else if (req.getOperateType().intValue() == BusinessConstants.CommonConstant.TWO) {
            craft.setStatus(BusinessConstants.Status.INVALID_STATUS);
            craft.setInvalidTime(new Date());
        }
        craft.setUpdateUser(req.getUserCode());
        UpdateWrapper<MaterialCraft> craftUpdateWrapper = new UpdateWrapper<>();
        craftUpdateWrapper.eq("random_code", req.getRandomCode());
        int uCraft = materialCraftDao.update(craft, craftUpdateWrapper);
        if (uCraft == BusinessConstants.CommonConstant.ZERO) {
            LOGGER.error("修改材料工艺失效失败【{}】", craft.toString());
            return Result.build(BusinessConstants.CommonConstant.ZERO, "修改工艺失效失败");
        }

        //修改材料工艺属性
        MaterialCraftProperty property = new MaterialCraftProperty();
        property.setUpdateUser(req.getUserCode());
        property.setStatus(craft.getStatus());
        UpdateWrapper<MaterialCraftProperty> propertyUpdateWrapper = new UpdateWrapper<>();
        propertyUpdateWrapper.eq("material_craft_random_code", req.getRandomCode());
        int uProperty = craftPropertyDao.update(property, propertyUpdateWrapper);
        if (uProperty == BusinessConstants.CommonConstant.ZERO) {
            LOGGER.error("修改材料工艺属性失败【{}】", craft.toString());
            return Result.build(BusinessConstants.CommonConstant.ZERO, "修改属性失败");
        }

        //修改材料工艺规则
        MaterialCraftRule rule = new MaterialCraftRule();
        rule.setStatus(craft.getStatus());
        rule.setUpdateUser(req.getUserCode());
        UpdateWrapper<MaterialCraftRule> ruleUpdateWrapper = new UpdateWrapper<>();
        ruleUpdateWrapper.eq("material_craft_random_code", req.getRandomCode());
        int uRule = materialCraftRuleDao.update(rule, ruleUpdateWrapper);
        if (uRule == BusinessConstants.CommonConstant.ZERO) {
            LOGGER.error("修改材料工艺规则失败【{}】", craft.toString());
            return Result.build(BusinessConstants.CommonConstant.ZERO, "修改规则失败");
        }
        //修改材料规则对应的设计部件
        QueryWrapper<MaterialCraftRulePart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("material_craft_random_code", req.getRandomCode());
        Integer count = rulePartDao.selectCount(queryWrapper);
        if (count.intValue() > BusinessConstants.CommonConstant.ZERO) {
            MaterialCraftRulePart rulePart = new MaterialCraftRulePart();
            rulePart.setStatus(craft.getStatus());
            rulePart.setUpdateUser(req.getUserCode());
            UpdateWrapper<MaterialCraftRulePart> rulePartUpdateWrapper = new UpdateWrapper<>();
            rulePartUpdateWrapper.eq("material_craft_random_code", req.getRandomCode());
            int uRulePart = rulePartDao.update(rulePart, rulePartUpdateWrapper);
            if (uRulePart == BusinessConstants.CommonConstant.ZERO) {
                LOGGER.error("修改材料工艺规则对应部件失败【{}】", craft.toString());
                return Result.build(BusinessConstants.CommonConstant.ZERO, "修改设计部件失败");
            }
        }
        return Result.ok();
    }

    /**
     * 新增发布材料工艺
     *
     * @param userCode
     * @param craft
     * @param ruleAndPartReqList
     * @param craftProperties
     * @return
     */
    private Result getDataResult(String userCode, MaterialCraft craft, List<MaterialCraftRuleAndPartReq> ruleAndPartReqList, List<MaterialCraftProperty> craftProperties, String materialCraftCode) {
        Map<String, Object> map = insertMaterialCraft(userCode, craft, materialCraftCode);
        if (map.get("flag").equals(BusinessConstants.CommonConstant.ZERO)) {
            LOGGER.error("新增材料工艺主数据失败【{}】", craft.toString());
            return Result.build(BusinessConstants.CommonConstant.ZERO, "新增工艺主数据失败");
        }
        craft = (MaterialCraft) map.get("craft");
        Result iData = insertMaterialCraftData(userCode, craft, ruleAndPartReqList, craftProperties);
        return iData;
    }

    private void setStatus(MaterialCraft craft, String userCode) {
        String formatNow = DateUtil.format(new Date(), "yyyy/MM/dd");
        if (craft.getEffectiveTime() != null) {
            String formatEffect = DateUtil.format(craft.getEffectiveTime(), "yyyy/MM/dd");
            if (formatNow.equals(formatEffect)) {
                craft.setStatus(BusinessConstants.Status.PUBLISHED_STATUS);
                craft.setEffectiveTime(new Date());
            } else {
                craft.setStatus(BusinessConstants.Status.NOT_ACTIVE_STATUS);
            }
        } else {
            craft.setStatus(BusinessConstants.Status.PUBLISHED_STATUS);
            craft.setEffectiveTime(new Date());
        }
        craft.setReleaseUser(userCode);
        craft.setReleaseTime(new Date());
    }

    /**
     * 修改草稿
     *
     * @param craft
     * @param oriMaterialCraft
     * @return
     */
    private Result updateDraftData(MaterialCraft craft, List<MaterialCraftAndPropertyResp> oriMaterialCraft) {
        craft.setMaterialCraftCode(oriMaterialCraft.get(0).getMaterialCraftCode());
        Long craftRandomCode = oriMaterialCraft.get(0).getMaterialCraftRandomCode();
        craft.setId(oriMaterialCraft.get(0).getMaterialCraftId());
        craft.setRandomCode(craftRandomCode);
        craft.setCreateUser(oriMaterialCraft.get(0).getCreateUser());
        craft.setCreateTime(oriMaterialCraft.get(0).getCreateTime());
        //修改材料工艺主数据
        int uCraft = materialCraftDao.updateById(craft);
        if (uCraft == BusinessConstants.CommonConstant.ZERO) {
            LOGGER.error("覆盖材料工艺主数据失败【{}】", craft.toString());
            return Result.build(BusinessConstants.CommonConstant.ZERO, "覆盖工艺主数据失败");
        }
        Result deleteRes = deleteMaterialRelatedData(craft, craftRandomCode);
        return deleteRes;
    }

    /**
     * 删除材料工艺所有关联数据
     *
     * @param craft
     * @param craftRandomCode
     * @return
     */
    private Result deleteMaterialRelatedData(MaterialCraft craft, Long craftRandomCode) {
        //根据材料工艺random_code删除所有材料工艺关联的数据
        Map<String, Object> map = new HashMap<>();
        map.put("material_craft_random_code", craftRandomCode);
        int dProperty = craftPropertyDao.deleteByMap(map);
        if (dProperty == BusinessConstants.CommonConstant.ZERO) {
            LOGGER.error("覆盖材料工艺属性失败【{}】", craft.toString());
            return Result.build(BusinessConstants.CommonConstant.ZERO, "覆盖属性失败");
        }

        int dRule = materialCraftRuleDao.deleteByMap(map);
        if (dRule == BusinessConstants.CommonConstant.ZERO) {
            LOGGER.error("覆盖材料工艺规则失败【{}】", craft.toString());
            return Result.build(BusinessConstants.CommonConstant.ZERO, "覆盖规则失败");
        }

        //查询部件
        QueryWrapper<MaterialCraftRulePart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("material_craft_random_code", craftRandomCode);
        List<MaterialCraftRulePart> ruleParts = rulePartDao.selectList(queryWrapper);
        if (CollUtil.isNotEmpty(ruleParts)) {
            int bRulePart = rulePartDao.deleteByMap(map);
            if (bRulePart == BusinessConstants.CommonConstant.ZERO) {
                LOGGER.error("覆盖材料工艺特殊规则对应部件失败【{}】", craft.toString());
                return Result.build(BusinessConstants.CommonConstant.ZERO, "覆盖部件失败");
            }
        }
        return null;
    }

    /**
     * 新增材料工艺草稿
     *
     * @param craft
     * @return
     */
    private Map insertMaterialCraft(String userCode, MaterialCraft craft, String materialCraftCode) {
        Map<String, Object> map = new HashMap();
        if (StringUtils.isNotBlank(materialCraftCode)) {
            craft.setMaterialCraftCode(materialCraftCode);
        } else {
            craft.setMaterialCraftCode(craft.getClothingCategoryCode() +
                    craft.getMaterialCraftKindCode() + createSerial(craft.getMaterialCraftKindCode()));
        }
        craft.setRandomCode(SnowflakeIdUtil.generateId());
        craft.setCreateUser(userCode);
        craft.setUpdateUser(userCode);
        craft.setUpdateTime(new Date());
        int iCraft = materialCraftDao.insert(craft);
        if (iCraft == BusinessConstants.CommonConstant.ZERO) {
            map.put("flag", 0);
            return map;
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_code", userCode);
        User user = userDao.selectOne(queryWrapper);
        craft.setUpdateUser(user.getUserName());
        map.put("flag", 1);
        map.put("craft", craft);
        return map;
    }

    /**
     * 新增材料工艺关联数据
     *
     * @param userCode
     * @param craft
     * @param ruleAndPartReqList
     * @param craftProperties
     * @return
     */
    private Result insertMaterialCraftData(String userCode, MaterialCraft craft, List<MaterialCraftRuleAndPartReq> ruleAndPartReqList, List<MaterialCraftProperty> craftProperties) {
        //新增材料工艺属性值
        for (MaterialCraftProperty craftProperty : craftProperties) {
            craftProperty.setRandomCode(SnowflakeIdUtil.generateId());
            craftProperty.setMaterialCraftRandomCode(craft.getRandomCode());
            craftProperty.setCreateUser(userCode);
            craftProperty.setUpdateTime(new Date());
            craftProperty.setUpdateUser(userCode);
            craftProperty.setStatus(craft.getStatus());
            int iProperty = craftPropertyDao.insert(craftProperty);
            if (iProperty < BusinessConstants.CommonConstant.ONE) {
                LOGGER.error("新增材料工艺属性值失败【{}】", craftProperty.toString());
                return Result.build(BusinessConstants.CommonConstant.ZERO, "新增属性失败");
            }
        }

        for (MaterialCraftRuleAndPartReq ruleAndPartReq : ruleAndPartReqList) {
            //新增材料规则
            List<MaterialCraftRule> craftRules = ruleAndPartReq.getCraftRules();
            StringBuilder sb = new StringBuilder();
            for (MaterialCraftRule rule : craftRules) {
                rule.setRandomCode(SnowflakeIdUtil.generateId());
                rule.setMaterialCraftRandomCode(craft.getRandomCode());
                rule.setCreateUser(userCode);
                rule.setUpdateTime(new Date());
                rule.setUpdateUser(userCode);
                rule.setStatus(craft.getStatus());
                int iRule = materialCraftRuleDao.insert(rule);
                if (iRule < BusinessConstants.CommonConstant.ONE) {
                    LOGGER.error("新增材料工艺规则失败【{}】", rule.toString());
                    return Result.build(BusinessConstants.CommonConstant.ZERO, "新增规则失败");
                }
                if (rule.getRuleType() == BusinessConstants.CommonConstant.TWO) {
                    if (sb.length() > 0) {
                        sb.append(",");
                    }
                    sb.append(rule.getRandomCode());
                }
            }
            List<MaterialCraftRulePart> craftRuleParts = ruleAndPartReq.getCraftRuleParts();
            if (CollUtil.isNotEmpty(craftRuleParts)) {
                //新增特殊材料工艺规则对应的部件
                for (MaterialCraftRulePart rulePart : craftRuleParts) {
                    rulePart.setMaterialCraftRuleRandomCodes(sb.toString());
                    rulePart.setMaterialCraftRandomCode(craft.getRandomCode());
                    rulePart.setRandomCode(SnowflakeIdUtil.generateId());
                    rulePart.setCreateUser(userCode);
                    rulePart.setUpdateTime(new Date());
                    rulePart.setUpdateUser(userCode);
                    rulePart.setStatus(craft.getStatus());
                    int iRulePart = rulePartDao.insert(rulePart);
                    if (iRulePart < BusinessConstants.CommonConstant.ONE) {
                        LOGGER.error("新增特殊材料工艺规则部件失败【{}】", rulePart.toString());
                        return Result.build(BusinessConstants.CommonConstant.ZERO, "新增设计部件失败");
                    }
                }
            }
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_code", userCode);
        User user = userDao.selectOne(queryWrapper);
        craft.setUpdateUser(user.getUserName());
        if (craft.getStatus() != BusinessConstants.Status.DRAFT_STATUS) {
            craft.setReleaseUser(user.getUserName());
        }
        return Result.ok(craft);
    }

    /**
     * 产生序列号
     */
    private String createSerial(String kindCode) {
        String serialNumber = "";
        boolean getLock;
        String requestId = UUIDUtil.uuid32();
        String key = BusinessConstants.Redis.MATERIAL_CRAFT_SERIAL_NUMBER + kindCode;
        try {
            getLock = redisUtil.tryGetDistributedLock(BusinessConstants.Redis.MATERIAL_CRAFT_LOCK_KEY, requestId, RedisContants.EXPIRE_TIME);
            if (getLock) {

                Object serialno = redisUtil.get(key);
                //第一个序列号生成@Authentication
                if (null == serialno) {
                    serialNumber = String.format("%05d", 1);
                    //将缓存放入
                    redisUtil.set(key, 1);
                } else {
                    //先把序列号加1；
                    redisUtil.incr(key, 1);
                    serialNumber = String.format("%05d", redisUtil.get(key));
                }
                LOGGER.info("---材料工艺编码生成的序列号是:" + serialNumber);
            } else {
                LOGGER.info("---材料工艺编码获取分布式锁失败!!!-----------");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("获取分布式锁失败，" + e.getMessage());
        } finally {
            //一定要释放锁
            redisUtil.releaseDistributedLock(BusinessConstants.Redis.MATERIAL_CRAFT_LOCK_KEY, requestId);
        }
        return serialNumber;
    }

}
