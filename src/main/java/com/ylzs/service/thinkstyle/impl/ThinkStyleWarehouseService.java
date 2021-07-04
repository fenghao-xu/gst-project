package com.ylzs.service.thinkstyle.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.util.SnowflakeIdUtil;
import com.ylzs.common.util.StringUtils;
import com.ylzs.common.utils.object.ObjectUtils;
import com.ylzs.dao.craftstd.CraftCategoryDao;
import com.ylzs.dao.designPart.DesignPartDao;
import com.ylzs.dao.partCraft.PartCraftDetailDao;
import com.ylzs.dao.partCraft.PartCraftMasterDataDao;
import com.ylzs.dao.partCraft.PartCraftRuleDao;
import com.ylzs.dao.plm.FabricMainDataDao;
import com.ylzs.dao.plm.PlmComponentMaterialDao;
import com.ylzs.dao.plm.PlmMasterDataComponentDao;
import com.ylzs.dao.plm.ProductModelMasterDataDao;
import com.ylzs.dao.sewingcraft.SewingCraftWarehouseDao;
import com.ylzs.dao.staticdata.PartPositionDao;
import com.ylzs.dao.thinkstyle.*;
import com.ylzs.entity.craftmainframe.CraftMainFrame;
import com.ylzs.entity.craftstd.Dictionary;
import com.ylzs.entity.craftstd.PartPosition;
import com.ylzs.entity.designPart.DesignPart;
import com.ylzs.entity.partCraft.PartCraftDetail;
import com.ylzs.entity.partCraft.PartCraftMasterData;
import com.ylzs.entity.partCraft.PartCraftRule;
import com.ylzs.entity.plm.FabricMainData;
import com.ylzs.entity.plm.PlmComponentMaterial;
import com.ylzs.entity.plm.PlmMasterDataComponent;
import com.ylzs.entity.plm.ProductModelMasterData;
import com.ylzs.entity.sewingcraft.SewingCraftWarehouse;
import com.ylzs.entity.thinkstyle.*;
import com.ylzs.service.craftmainframe.ICraftMainFrameService;
import com.ylzs.service.craftstd.IDictionaryService;
import com.ylzs.service.craftstd.IMaxCodeService;
import com.ylzs.service.impl.OriginServiceImpl;
import com.ylzs.service.pi.ISendPiService;
import com.ylzs.service.thinkstyle.IThinkStyleCraftService;
import com.ylzs.service.thinkstyle.IThinkStyleProcessRuleService;
import com.ylzs.service.thinkstyle.IThinkStyleWarehouseService;
import com.ylzs.vo.system.DataChild;
import com.ylzs.vo.system.DataParent;
import com.ylzs.vo.thinkstyle.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.ylzs.common.constant.BusinessConstants.PiReceiveUrlConfig.THINK_STYLE_PUBLISH_URL;
import static com.ylzs.common.constant.BusinessConstants.Status.*;
import static com.ylzs.common.util.Assert.isTrue;
import static com.ylzs.common.util.Assert.notNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;



/**
 * @author ：lyq
 * @description：智库款工艺
 * @date ：2020-03-05 18:51
 */


@Service
@Transactional(rollbackFor = Exception.class)
public class ThinkStyleWarehouseService extends OriginServiceImpl<ThinkStyleWarehouseDao, ThinkStyleWarehouse> implements IThinkStyleWarehouseService {
    private static final Logger logger = LoggerFactory.getLogger(ThinkStyleWarehouseService.class);

    @Resource
    private ThinkStyleWarehouseDao thinkStyleWarehouseDao;


    @Resource
    private ThinkStylePartDao thinkStylePartDao;

    @Resource
    private ThinkStyleCraftDao thinkStyleCraftDao;

    @Resource
    private ThinkStyleProcessRuleDao thinkStyleProcessRuleDao;

    @Resource
    private ThinkStyleFabricDao thinkStyleFabricDao;

    @Resource
    private PartCraftMasterDataDao partCraftMasterDataDao;


    @Resource
    private PartCraftDetailDao partCraftDetailDao;

    @Resource
    private PartCraftRuleDao partCraftRuleDao;


    @Resource
    private SewingCraftWarehouseDao sewingCraftWarehouseDao;

    @Resource
    private ThinkStyleCraftHistoryDao thinkStyleCraftHistoryDao;

    @Resource
    private IThinkStyleProcessRuleService thinkStyleProcessRuleService;


    @Resource
    private IMaxCodeService maxCodeService;

    @Resource
    private IDictionaryService dictionaryService;

    @Resource
    private ProductModelMasterDataDao productModelMasterDataDao;

    @Resource
    private PlmMasterDataComponentDao plmMasterDataComponentDao;

    @Resource
    private PlmComponentMaterialDao plmComponentMaterialDao;

    @Resource
    private FabricMainDataDao fabricMainDataDao;

    @Resource
    private CraftCategoryDao craftCategoryDao;

    @Resource
    private DesignPartDao designPartDao;

    @Resource
    private PartPositionDao partPositionDao;

    @Resource
    private IThinkStyleCraftService thinkStyleCraftService;

    @Resource
    private ISendPiService sendPi;

    @Resource
    private ICraftMainFrameService craftMainFrameService;





    @Override
    public ThinkStyleWarehouse getThinkStyleInfo(String styleCode) {

        //获取款信息
        QueryWrapper<ThinkStyleWarehouse> styleWrapper = new QueryWrapper<>();
        styleWrapper.lambda().eq(ThinkStyleWarehouse::getStyleCode, styleCode)
                .eq(ThinkStyleWarehouse::getStatus, PUBLISHED_STATUS);
        List<ThinkStyleWarehouse> styles = thinkStyleWarehouseDao.selectList(styleWrapper);
        if (ObjectUtils.isEmptyList(styles)) {
            return null;
        }

        //未审核不允许提取
        ThinkStyleWarehouse style = styles.get(0);
        if(StringUtils.isNotEmpty(style.getClothesCategoryCode())) {
            CraftMainFrame craftMainFrame = craftMainFrameService.selectMainFrameByClothesCategoryCode(style.getClothesCategoryCode());
            if(craftMainFrame != null) {
                style.setCraftMainFrameCode(craftMainFrame.getMainFrameCode());
                style.setCraftMainFrameName(craftMainFrame.getMainFrameName());
            }
        }

        //获取部件信息
        QueryWrapper<ThinkStylePart> partWrapper = new QueryWrapper<>();
        partWrapper.lambda().eq(ThinkStylePart::getStyleRandomCode, style.getRandomCode()).orderByAsc(ThinkStylePart::getId);
        List<ThinkStylePart> parts = thinkStylePartDao.selectList(partWrapper);


        int i = 1;
        for (ThinkStylePart part : parts) {
            boolean isSpecial = false;
            if (part.getIsSpecial() != null) {
                isSpecial = part.getIsSpecial();
            } else {
                part.setIsSpecial(false);
            }
            if (part.getIsVirtual() == null) {
                part.setIsVirtual(false);
            }

            if (part.getOrderNum() == null) {
                part.setOrderNum(i++);
            }

            if (isSpecial) {
                //特殊部件工艺取智库款的工艺
                part.setCraftList(getSpecialCrafts(part));

                //获取处理规则
                QueryWrapper<ThinkStyleProcessRule> ruleWrapper = new QueryWrapper<>();
                ruleWrapper.lambda().eq(ThinkStyleProcessRule::getPartRandomCode, part.getRandomCode()).orderByAsc(ThinkStyleProcessRule::getId);
                List<ThinkStyleProcessRule> rules = thinkStyleProcessRuleDao.selectList(ruleWrapper);
                part.setRuleList(rules);

            } else {
                //设置普通部件工艺和处理规则
                setNormalCraftAndRules(part, style.getClothesCategoryCode());
            }

            //保存设计部件代码
            if (part.getCraftList() != null) {
                part.getCraftList().stream().forEach(x->{
                    x.setDesignPartCode(part.getDesignPartCode());
                    if(StringUtils.isNotBlank(part.getPositionCode())){
                        x.setPositionCode(part.getPositionCode());
                    }
                });
            }

        }
        style.setPartList(parts);
        return style;

    }


    /**
     * @param part 智库款部件
     * @return 智库款部件的特殊工艺
     */
    private List<ThinkStyleCraft> getSpecialCrafts(ThinkStylePart part) {
        Long randomCode = part.getRandomCode();
        QueryWrapper<ThinkStyleCraft> craftWrapper = new QueryWrapper<>();
        craftWrapper.lambda().eq(ThinkStyleCraft::getPartRandomCode, randomCode).orderByAsc(ThinkStyleCraft::getLineNo);
        List<ThinkStyleCraft> crafts = thinkStyleCraftDao.selectList(craftWrapper);
        return crafts;
    }

    /**
     * @param part 智库款部件
     * @return 无
     * @desc 设置普通工艺和处理规则
     */
    private void setNormalCraftAndRules(ThinkStylePart part, String clothingCategoryCode) {
        String designPartCode = part.getDesignPartCode();
        String partPosition = part.getPositionCode();
        if (designPartCode == null || designPartCode.isEmpty()) {
            return;
        }

        List<Long> randomCodes = partCraftMasterDataDao.getPartCraftMasterRandomCode(designPartCode, partPosition, clothingCategoryCode, "定制");
        if (ObjectUtils.isEmptyList(randomCodes)) {
            return;
        }

        Long randomCode = randomCodes.get(0);
        QueryWrapper<PartCraftDetail> craftWrapper = new QueryWrapper<>();
        craftWrapper.lambda().eq(PartCraftDetail::getPartCraftMainRandomCode, randomCode).orderByAsc(PartCraftDetail::getId);
        List<PartCraftDetail> craftDetails = partCraftDetailDao.selectList(craftWrapper);
        if (ObjectUtils.isNotEmptyList(craftDetails)) {
            int i = 1;
            List<ThinkStyleCraft> craftList = new ArrayList<>();
            for (PartCraftDetail craft : craftDetails) {
                ThinkStyleCraft itm = new ThinkStyleCraft();
                itm.setLineNo(i++);
                itm.setIsNew(false);
                itm.setCraftCode(craft.getCraftCode());
                if (craft.getCraftFlowNum() != null && !craft.getCraftFlowNum().isEmpty()) {
                    itm.setCraftFlowNum(Integer.parseInt(craft.getCraftFlowNum()));
                }
                itm.setCraftDesc(craft.getCraftRemark());
                //vo.setStandardTime(craft.getStandardTime());
                //vo.setStandardPrice(craft.getStandardPrice());

                itm.setPartRandomCode(part.getRandomCode());
                itm.setStyleRandomCode(part.getStyleRandomCode());

                List<String> codeList = new ArrayList<>();
                codeList.add(craft.getCraftCode());
                List<SewingCraftWarehouse> warehouses = sewingCraftWarehouseDao.getDataByCraftCodeList(codeList);
                SewingCraftWarehouse sewingCraftWarehouse = null;
                if (null != warehouses && warehouses.size() > 0) {
                    sewingCraftWarehouse = warehouses.get(0);
                }
                if (sewingCraftWarehouse != null) {
                    itm.setCraftName(sewingCraftWarehouse.getCraftName());
                }
                craftList.add(itm);
            }
            part.setCraftList(craftList);
        }


        QueryWrapper<PartCraftRule> ruleQueryWrapper = new QueryWrapper<>();
        ruleQueryWrapper.lambda().eq(PartCraftRule::getPartCraftMainRandomCode, randomCode).orderByAsc(PartCraftRule::getId);
        List<PartCraftRule> partCraftRules = partCraftRuleDao.selectList(ruleQueryWrapper);
        if (ObjectUtils.isNotEmptyList(partCraftRules)) {
            List<ThinkStyleProcessRule> ruleList = new ArrayList<>();
            for (PartCraftRule partRule : partCraftRules) {
                ThinkStyleProcessRule rule = new ThinkStyleProcessRule();
                if (partRule.getProcessType() != null) {
                    String val = partRule.getProcessType().toString();
                    rule.setProcessType(Byte.valueOf(val));
                }
                rule.setActionCraftCode(partRule.getActionCraftCode());
                rule.setActionCraftName(partRule.getActionCraftName());
                rule.setSourceCraftCode(partRule.getSourceCraftCode());
                rule.setSourceCraftName(partRule.getSourceCraftName());
                rule.setPartRandomCode(part.getRandomCode());
                rule.setStyleRandomCode(part.getStyleRandomCode());
                ruleList.add(rule);
            }
            part.setRuleList(ruleList);
        }

    }


    @Override
    public List<ThinkStyleWarehouse> selectAllThinkStyle(String keywords, String[] craftCategoryCodes, String[] clothesCategoryCodes,
                                                         Date updateDateStart, Date updateDateStop, Integer status, Boolean isInvalid) {
        return thinkStyleWarehouseDao.selectAllThinkStyle(keywords, craftCategoryCodes, clothesCategoryCodes,
                updateDateStart, updateDateStop, status, isInvalid);

    }

    @Override
    public ThinkStyleWarehouseListVo getThinkStyleWarehouseListVo(ThinkStyleWarehouse obj) {
        ThinkStyleWarehouseListVo result = new ThinkStyleWarehouseListVo();
        try {
            BeanUtils.copyProperties(obj, result);
            Integer status = DRAFT_STATUS;
            if (obj.getStatus() != null) {
                status = obj.getStatus();
            }
            if (status.equals(DRAFT_STATUS)) {
                result.setStatusName("草稿");
            } else if (status.equals(AUDITED_STATUS)) {
                result.setStatusName("已审核");
            } else if (status.equals(PUBLISHED_STATUS)) {
                result.setStatusName("已发布");
            } else if (status.equals(INVALID_STATUS)) {
                result.setStatusName("已失效");
            } else if (status.equals(NOT_ACTIVE_STATUS)) {
                result.setStatusName("未生效");
            }

        } catch (Exception e) {

        }
        return result;

    }

    @Override
    public ThinkStyleWarehouseViewVo getThinkStyleWarehouseViewVo(ThinkStyleWarehouse obj) {
        ThinkStyleWarehouseViewVo result = new ThinkStyleWarehouseViewVo();
        try {
            BeanUtils.copyProperties(obj, result);
            Integer status = DRAFT_STATUS;
            if (obj.getStatus() != null) {
                status = obj.getStatus();
            }
            if (status.equals(DRAFT_STATUS)) {
                result.setStatusName("草稿");
            } else if (status.equals(AUDITED_STATUS)) {
                result.setStatusName("已审核");
            } else if (status.equals(PUBLISHED_STATUS)) {
                result.setStatusName("已发布");
            } else if (status.equals(INVALID_STATUS)) {
                result.setStatusName("已失效");
            } else if (status.equals(NOT_ACTIVE_STATUS)) {
                result.setStatusName("未生效");
            }

        } catch (Exception e) {

        }
        return result;
    }

    @Override
    public ThinkStyleWarehouse getThinkStyleWarehouse(long randomCode) {
        QueryWrapper<ThinkStyleWarehouse> styleQuery = new QueryWrapper<>();
        styleQuery.lambda().eq(ThinkStyleWarehouse::getRandomCode, randomCode);
        ThinkStyleWarehouse style = thinkStyleWarehouseDao.selectOne(styleQuery);
        return style;
    }

    @Override
    public void sendThinkStylePublishInfo(long randomCode) {

        QueryWrapper<ThinkStyleWarehouse> styleQuery = new QueryWrapper<>();
        styleQuery.lambda().eq(ThinkStyleWarehouse::getRandomCode, randomCode);
        ThinkStyleWarehouse style = this.getOne(styleQuery);

        List<ThinkStylePublishCraftVo> craftVos = thinkStyleWarehouseDao.getThinkStylePublishCraftVos(randomCode, style.getClothesCategoryCode());
        if(ObjectUtils.isEmptyList(craftVos)) {
            return;
        }

        ThinkStylePublishInfoVo publishInfoVo = new ThinkStylePublishInfoVo();
        publishInfoVo.setBjdm(style.getStyleCode());
        publishInfoVo.setDetails(craftVos);

        DataParent<ThinkStylePublishInfoVo> parent = new DataParent<>();
        parent.setInterfaceType("pi.StylePartLib");
        parent.setSynTime(new Date());
        parent.setCount("1");

        List<DataChild<ThinkStylePublishInfoVo>> items = new ArrayList<>();
        DataChild<ThinkStylePublishInfoVo> item = new DataChild<ThinkStylePublishInfoVo>();
        item.setItemId("0");
        item.setItem(publishInfoVo);
        items.add(item);
        parent.setItems(items);

        sendPi.postObject(THINK_STYLE_PUBLISH_URL, parent);

    }



    /**
     * @param part                     智库款部件
     * @param thinkStyleCraftVos       智库款工序
     * @param thinkStyleProcessRuleVos 智库款处理规则
     */
    private void getNormalPartInfoVos(ThinkStyleWarehouse style, ThinkStylePart part, List<ThinkStyleCraftVo> thinkStyleCraftVos, List<ThinkStyleProcessRuleVo> thinkStyleProcessRuleVos) {
        String designPartCode = part.getDesignPartCode();
        String partPosition = part.getPositionCode();
        if (designPartCode == null || designPartCode.isEmpty()) {
            return;
        }

        List<Long> randomCodes = partCraftMasterDataDao.getPartCraftMasterRandomCode(designPartCode, partPosition, style.getClothesCategoryCode(), "定制");
        if (ObjectUtils.isEmptyList(randomCodes)) {
            return;
        }

        Long randomCode = randomCodes.get(0);
        List<ThinkStyleCraftVo> craftDetails = partCraftDetailDao.getThinkStyleCraftVos(randomCode);
        if (ObjectUtils.isNotEmptyList(craftDetails)) {
            int i = 1;
            for (ThinkStyleCraftVo vo : craftDetails) {
                vo.setLineNo(i++);
                vo.setIsNew(false);
                vo.setPartRandomCode(part.getRandomCode());
                vo.setStyleRandomCode(part.getStyleRandomCode());
            }
            thinkStyleCraftVos.addAll(craftDetails);
        }

        QueryWrapper<PartCraftRule> ruleWrapper = new QueryWrapper<>();
        ruleWrapper.lambda().eq(PartCraftRule::getPartCraftMainRandomCode, randomCode).orderByAsc(PartCraftRule::getProcessingSortNum);
        List<PartCraftRule> partCraftRules = partCraftRuleDao.selectList(ruleWrapper);
        if (ObjectUtils.isNotEmptyList(partCraftRules)) {
            for (PartCraftRule rule : partCraftRules) {
                ThinkStyleProcessRuleVo vo = new ThinkStyleProcessRuleVo();
                if (rule.getProcessType() != null) {
                    String val = rule.getProcessType().toString();
                    vo.setProcessType(Byte.valueOf(val));

                    if (rule.getProcessType().equals(-1)) {
                        vo.setProcessTypeName("删除");
                    } else if (rule.getProcessType().equals(0)) {
                        vo.setProcessTypeName("替换");
                    } else if (rule.getProcessType().equals(1)) {
                        vo.setProcessTypeName("增加");
                    }
                }
                vo.setSourceCraftRandomCode(null);
                vo.setSourceCraftCode(rule.getSourceCraftCode());
                vo.setSourceCraftName(rule.getSourceCraftName());
                vo.setActionCraftRandomCode(null);
                vo.setActionCraftCode(rule.getActionCraftCode());
                vo.setActionCraftName(rule.getActionCraftName());
                vo.setProcessingSortNum(rule.getProcessingSortNum()==null?0:rule.getProcessingSortNum());
                thinkStyleProcessRuleVos.add(vo);
            }
        }

    }

    /**
     * @param part 智库款部件
     * @return 智库款部件的特殊工艺
     */
    private List<ThinkStyleCraftVo> getSpecialCraftVos(Long partRandomCode) {
        return thinkStyleCraftDao.getSpecialCraftVos(partRandomCode);
    }


    @Override
    public Integer updateThinkStyleWarehouse(ThinkStyleWarehouse style) {
        QueryWrapper<ThinkStyleWarehouse> styleWrapper = new QueryWrapper<>();
        styleWrapper.lambda().eq(ThinkStyleWarehouse::getRandomCode, style.getRandomCode());
        int ret = thinkStyleWarehouseDao.update(style, styleWrapper);
        isTrue(ret == 1, "未找到智库款id");
        if (ObjectUtils.isEmptyList(style.getPartList())) {
            return ret;
        }

        //获取智库款所有工艺
        QueryWrapper<ThinkStyleCraft> styleCraftWrapper = new QueryWrapper<>();
        styleCraftWrapper.lambda().eq(ThinkStyleCraft::getStyleRandomCode, style.getRandomCode())
                .orderByAsc(ThinkStyleCraft::getPartRandomCode)
                .orderByAsc(ThinkStyleCraft::getRandomCode);
        List<ThinkStyleCraft> oldStyleCrafts = thinkStyleCraftDao.selectList(styleCraftWrapper);
        Map<Long, List<ThinkStyleCraft>> oldStyleCraftMap = oldStyleCrafts.parallelStream()
                .collect(Collectors.groupingBy(ThinkStyleCraft::getPartRandomCode));

        //获取智库款所有规则
        QueryWrapper<ThinkStyleProcessRule> styleRulesWrapper = new QueryWrapper<>();
        styleRulesWrapper.lambda().eq(ThinkStyleProcessRule::getStyleRandomCode, style.getRandomCode())
                .orderByAsc(ThinkStyleProcessRule::getPartRandomCode)
                .orderByAsc(ThinkStyleProcessRule::getRandomCode);
        List<ThinkStyleProcessRule> oldStyleRules = thinkStyleProcessRuleDao.selectList(styleRulesWrapper);
        Map<Long, List<ThinkStyleProcessRule>> oldStyleRuleMap = oldStyleRules.parallelStream()
                .collect(Collectors.groupingBy(ThinkStyleProcessRule::getPartRandomCode));




        List<ThinkStyleCraft> emptyCraftList = new ArrayList<>();
        List<ThinkStyleProcessRule> emptyRuleList = new ArrayList<>();




        Map<Long,ThinkStylePart> oldPartMap = thinkStylePartDao.selectThinkStylePart(style.getRandomCode());
        //失效的部件
        List<Long> delPartRandomCodes = oldPartMap.values().stream().filter(x->Boolean.FALSE.equals(x.getIsValid()))
                .map(ThinkStylePart::getRandomCode).collect(Collectors.toList());


        List<ThinkStylePart> newParts = style.getPartList();
        for(int i = 0; i < delPartRandomCodes.size(); i++) {
            Long delRandomCode = delPartRandomCodes.get(i);
            boolean finded = false;
            for(ThinkStylePart itm: newParts) {
                if(delRandomCode.equals(itm.getRandomCode())) {
                    finded = true;
                    break;
                }
            }
            //在新的没有则删除
            if(!finded) {
                QueryWrapper<ThinkStyleCraft> delCraftQry = new QueryWrapper();
                delCraftQry.lambda().eq(ThinkStyleCraft::getStyleRandomCode, style.getRandomCode())
                        .eq(ThinkStyleCraft::getPartRandomCode, delRandomCode);
                thinkStyleCraftDao.delete(delCraftQry);
                QueryWrapper<ThinkStyleProcessRule> delRuleQry = new QueryWrapper<>();
                delRuleQry.lambda().eq(ThinkStyleProcessRule::getStyleRandomCode, style.getRandomCode())
                        .eq(ThinkStyleProcessRule::getPartRandomCode, delRandomCode);
                thinkStyleProcessRuleDao.delete(delRuleQry);

                QueryWrapper<ThinkStyleCraftHistory> delHisQry = new QueryWrapper<>();
                delHisQry.lambda().eq(ThinkStyleCraftHistory::getStyleRandomCode, style.getRandomCode())
                        .eq(ThinkStyleCraftHistory::getPartRandomCode, delRandomCode);
                thinkStyleCraftHistoryDao.delete(delHisQry);

                QueryWrapper<ThinkStylePart> delPartQry = new QueryWrapper<>();
                delPartQry.lambda().eq(ThinkStylePart::getStyleRandomCode, style.getRandomCode())
                        .eq(ThinkStylePart::getRandomCode, delRandomCode);
                thinkStylePartDao.delete(delPartQry);
            }
        }





        for (ThinkStylePart newPart : newParts) {
            notNull(newPart.getRandomCode(), "未找到智库款部件");
            ThinkStylePart oldPart = oldPartMap.getOrDefault(newPart.getRandomCode(), null);
            notNull(oldPart, "未找到智库款部件");

            if (newPart.getIsSpecial() == null) {
                newPart.setIsSpecial(false);
            }
            if (oldPart.getIsSpecial() == null) {
                oldPart.setIsSpecial(false);
            }

            //设置部件未修改
            if(ObjectUtils.equals(newPart.getIsSpecial(),oldPart.getIsSpecial()) &&
                ObjectUtils.equals(newPart.getOrderNum(), oldPart.getOrderNum()) &&
                !newPart.getIsSpecial()) {
                continue;
            }

            //更新部件
            if(!ObjectUtils.equals(newPart.getIsSpecial(),oldPart.getIsSpecial()) ||
                    !ObjectUtils.equals(newPart.getOrderNum(), oldPart.getOrderNum())) {
                QueryWrapper<ThinkStylePart> partWrapper1 = new QueryWrapper<>();
                partWrapper1.lambda().eq(ThinkStylePart::getRandomCode, newPart.getRandomCode());
                thinkStylePartDao.update(newPart, partWrapper1);
            }

            if (!newPart.getIsSpecial().equals(oldPart.getIsSpecial())) {
                if (!newPart.getIsSpecial()) {
                    //特殊部件变成普通部件
                    //删除工艺
                    QueryWrapper<ThinkStyleCraft> craftWrapper = new QueryWrapper<>();
                    craftWrapper.lambda().eq(ThinkStyleCraft::getPartRandomCode, oldPart.getRandomCode());
                    thinkStyleCraftDao.delete(craftWrapper);
                    //删除规则
                    QueryWrapper<ThinkStyleProcessRule> ruleWrapper = new QueryWrapper<>();
                    ruleWrapper.lambda().eq(ThinkStyleProcessRule::getPartRandomCode, oldPart.getRandomCode());
                    thinkStyleProcessRuleDao.delete(ruleWrapper);
                    continue;
                }
            }

            if (!newPart.getIsSpecial()) {
                continue;
            }

            //处理工序
            List<ThinkStyleCraft> oldCrafts = oldStyleCraftMap.getOrDefault(newPart.getRandomCode(), emptyCraftList);
            List<ThinkStyleCraft> newCrafts = newPart.getCraftList();
            if (newCrafts == null) {
                newCrafts = emptyCraftList;
            }

            //新增工序
            Set<Long> changeSet = new TreeSet<>();
            for (ThinkStyleCraft newCraft : newCrafts) {
                boolean isAdd = false;
                ThinkStyleCraft oldCraft = oldCrafts.stream().filter(x ->
                        x.getRandomCode().equals(newCraft.getRandomCode())).findFirst().orElse(null);
                if (oldCraft == null) {
                    isAdd = true;
                }


                if (isAdd) {
                    long randomCode = SnowflakeIdUtil.generateId();
                    newCraft.setRandomCode(randomCode);
                    newCraft.setCreateTime(style.getUpdateTime());
                    newCraft.setCreateUser(style.getUpdateUser());
                    newCraft.setUpdateTime(style.getUpdateTime());
                    newCraft.setUpdateUser(style.getUpdateUser());
                    thinkStyleCraftDao.insert(newCraft);
                } else {
                    boolean isNotChange = true;
                    if(null != oldCraft.getCraftRandomCode()) {
                        isNotChange = oldCraft.getCraftRandomCode().equals(newCraft.getCraftRandomCode());
                    } else {
                        if(null != newCraft.getCraftRandomCode()) {
                            isNotChange = newCraft.getCraftRandomCode().equals(oldCraft.getCraftRandomCode());
                        }
                    }
                    if(isNotChange) {
                        if(null != oldCraft.getCraftFlowNum()) {
                            isNotChange = oldCraft.getCraftFlowNum().equals(newCraft.getCraftFlowNum());
                        } else {
                            if(null != newCraft.getCraftFlowNum()) {
                                isNotChange = newCraft.getCraftFlowNum().equals(oldCraft.getCraftFlowNum());
                            }
                        }
                    }
                    if(isNotChange) {
                        if(null != oldCraft.getCraftName()) {
                            isNotChange = oldCraft.getCraftName().equals(newCraft.getCraftName());
                        } else {
                            if(null != newCraft.getCraftName()) {
                                isNotChange = newCraft.getCraftName().equals(oldCraft.getCraftName());
                            }
                        }
                    }
                    if(!isNotChange) {
                        changeSet.add(oldCraft.getRandomCode());

                        newCraft.setUpdateTime(style.getUpdateTime());
                        newCraft.setUpdateUser(style.getUpdateUser());
                        UpdateWrapper<ThinkStyleCraft> upCraft = new UpdateWrapper<>();
                        upCraft.lambda().eq(ThinkStyleCraft::getRandomCode, newCraft.getRandomCode());
                        thinkStyleCraftDao.update(newCraft, upCraft);

                    }
                }
            }
            //删除工序
            for (ThinkStyleCraft oldCraft : oldCrafts) {
                ThinkStyleCraft newCraft = newCrafts.stream().filter(x ->
                        oldCraft.getRandomCode().equals(x.getRandomCode())
                                ).findFirst().orElse(null);
                boolean isDel = true;
                if(null != newCraft) {
                    if(changeSet.contains(oldCraft.getRandomCode())) {
                        isDel = false;
                    } else {
                        continue;
                    }
                }

                ThinkStyleCraftHistory craftHistory = new ThinkStyleCraftHistory();
                BeanUtils.copyProperties(oldCraft, craftHistory);
                craftHistory.setId(null);
                long randomCode = SnowflakeIdUtil.generateId();
                craftHistory.setRandomCode(randomCode);
                craftHistory.setRemoveTime(new Date());
                craftHistory.setRemoveUser(style.getUpdateUserName());
                if(isDel) {
                    craftHistory.setOperationType("删除");
                } else {
                    craftHistory.setOperationType("修改");
                }
                thinkStyleCraftHistoryDao.insert(craftHistory);

                if(isDel) {
                    QueryWrapper<ThinkStyleCraft> delWrapper = new QueryWrapper<>();
                    delWrapper.lambda().eq(ThinkStyleCraft::getRandomCode, oldCraft.getRandomCode());
                    thinkStyleCraftDao.delete(delWrapper);
                }
            }


            {
//                //新增工序
//                for (ThinkStyleCraft newCraft : newCrafts) {
//                    ThinkStyleCraft oldCraft = null;
//                    boolean isAdd = false;
//                    oldCraft = oldCrafts.stream().filter(x ->
//                            x.getCraftRandomCode().equals(newCraft.getCraftRandomCode())
//                                    && x.getCraftFlowNum().equals(newCraft.getCraftFlowNum())
//                                    && x.getCraftName().equals(newCraft.getCraftName())).findFirst().orElse(null);
//                    if (oldCraft == null) {
//                        isAdd = true;
//                    }
//
//
//                    if (isAdd) {
//                        long randomCode = SnowflakeIdUtil.generateId();
//                        newCraft.setRandomCode(randomCode);
//                        newCraft.setCreateTime(style.getUpdateTime());
//                        newCraft.setCreateUser(style.getUpdateUser());
//                        newCraft.setUpdateTime(style.getUpdateTime());
//                        newCraft.setUpdateUser(style.getUpdateUser());
//                        thinkStyleCraftDao.insert(newCraft);
//                    }
//                }
//                //删除工序
//                for (ThinkStyleCraft oldCraft : oldCrafts) {
//                    ThinkStyleCraft newCraft = newCrafts.stream().filter(x ->
//                            oldCraft.getCraftRandomCode().equals(x.getCraftRandomCode())
//                                    && oldCraft.getCraftFlowNum().equals(x.getCraftFlowNum())
//                                    && oldCraft.getCraftName().equals(x.getCraftName())).findFirst().orElse(null);
//                    if (newCraft == null) {
//                        ThinkStyleCraft newCraft1 = newCrafts.stream().filter(x ->
//                                oldCraft.getCraftRandomCode().equals(x.getCraftRandomCode())).findFirst().orElse(null);
//
//
//                        ThinkStyleCraftHistory craftHistory = new ThinkStyleCraftHistory();
//                        BeanUtils.copyProperties(oldCraft, craftHistory);
//                        craftHistory.setId(null);
//                        long randomCode = SnowflakeIdUtil.generateId();
//                        craftHistory.setRandomCode(randomCode);
//                        craftHistory.setRemoveTime(new Date());
//                        craftHistory.setRemoveUser(style.getUpdateUserName());
//                        if (newCraft1 == null) {
//                            craftHistory.setOperationType("删除");
//                        } else {
//                            craftHistory.setOperationType("修改");
//                        }
//                        thinkStyleCraftHistoryDao.insert(craftHistory);
//
//                        QueryWrapper<ThinkStyleCraft> delWrapper = new QueryWrapper<>();
//                        delWrapper.lambda().eq(ThinkStyleCraft::getRandomCode, oldCraft.getRandomCode());
//                        thinkStyleCraftDao.delete(delWrapper);
//                    }
//                }
            }

            //处理规则
            List<ThinkStyleProcessRule> oldRules = oldStyleRuleMap.getOrDefault(newPart.getRandomCode(), emptyRuleList);
            List<ThinkStyleProcessRule> newRules = newPart.getRuleList();
            if (newRules == null) {
                newRules = emptyRuleList;
            }

            //新增规则
            for (ThinkStyleProcessRule newRule : newRules) {
                ThinkStyleProcessRule oldRule = null;
                boolean isAdd = false;
                if (newRule.getRandomCode() == null) {
                    long randomCode = SnowflakeIdUtil.generateId();
                    newRule.setRandomCode(randomCode);
                    isAdd = true;
                } else {
                    oldRule = oldRules.stream().filter(x -> x.getRandomCode().equals(newRule.getRandomCode())).findFirst().orElse(null);
                    if (oldRule == null) {
                        isAdd = true;
                    }
                }

                newRule.setUpdateTime(style.getUpdateTime());
                newRule.setUpdateUser(style.getUpdateUser());
                if (isAdd) {
                    newRule.setCreateTime(style.getUpdateTime());
                    newRule.setCreateUser(style.getUpdateUser());
                    thinkStyleProcessRuleDao.insert(newRule);
                } else {
                    //规则不更新
                    QueryWrapper<ThinkStyleProcessRule> ruleUpdateQuery = new QueryWrapper<>();
                    ruleUpdateQuery.lambda().eq(ThinkStyleProcessRule::getRandomCode, newRule.getRandomCode());
                    //newRule.setProcessingSortNum(oldRule.getProcessingSortNum());
                    thinkStyleProcessRuleDao.update(newRule, ruleUpdateQuery);
                }
            }

            //删除规则
            for (ThinkStyleProcessRule oldRule : oldRules) {
                ThinkStyleProcessRule newRule = newRules.stream().filter(x -> oldRule.getRandomCode().equals(x.getRandomCode())).findFirst().orElse(null);
                if (newRule == null) {
                    QueryWrapper<ThinkStyleProcessRule> delWrapper = new QueryWrapper<>();
                    delWrapper.lambda().eq(ThinkStyleProcessRule::getRandomCode, oldRule.getRandomCode());
                    thinkStyleProcessRuleDao.delete(delWrapper);
                }
            }
        }

        //更新智库款时间
        thinkStyleWarehouseDao.updateStandardTime(style.getRandomCode(), style.getClothesCategoryCode());

        return ret;
    }

    @Override
    public void getThinkStylePartInfo(ThinkStyleWarehouse style, ThinkStylePart part, List<ThinkStyleCraftVo> thinkStyleCraftVos, List<ThinkStyleProcessRuleVo> thinkStyleProcessRuleVos) {

        boolean isSpecial = false;
        if (part.getIsSpecial() != null) {
            isSpecial = part.getIsSpecial();
        }
        if (isSpecial) {
            //特殊部件工艺取智库款的工艺
            List<ThinkStyleCraftVo> specialCraftsVos = getSpecialCraftVos(part.getRandomCode());
            if (ObjectUtils.isNotEmptyList(specialCraftsVos)) {
                thinkStyleCraftVos.addAll(specialCraftsVos);
            }

            List<ThinkStyleProcessRuleVo> specialRuleVos = thinkStyleProcessRuleService.getProcessRuleVos(part.getRandomCode());
            if (ObjectUtils.isNotEmptyList(specialRuleVos)) {
                thinkStyleProcessRuleVos.addAll(specialRuleVos);
            }

        } else {
            getNormalPartInfoVos(style, part, thinkStyleCraftVos, thinkStyleProcessRuleVos);
        }
    }

    @Override
    public List<ThinkStyleHistoryVo> getThinkStyleHistoryVos(Long randomCode) {
        QueryWrapper<ThinkStyleWarehouse> styleWrapper = new QueryWrapper<>();
        styleWrapper.lambda().eq(ThinkStyleWarehouse::getRandomCode, randomCode)
                .eq(ThinkStyleWarehouse::getStatus, BusinessConstants.Status.INVALID_STATUS)
                .orderByAsc(ThinkStyleWarehouse::getId);
        List<ThinkStyleWarehouse> styleWarehouses = thinkStyleWarehouseDao.selectList(styleWrapper);
        if (ObjectUtils.isEmptyList(styleWarehouses)) {
            return null;
        }

        int i = 1;
        List<ThinkStyleHistoryVo> vos = new ArrayList<>();
        for (ThinkStyleWarehouse style : styleWarehouses) {
            ThinkStyleHistoryVo vo = new ThinkStyleHistoryVo();
            try {
                BeanUtils.copyProperties(style, vo);
                vo.setLine_num(i++);
            } catch (Exception e) {

            }
            vos.add(vo);
        }
        return vos;
    }

    @Override
    public String checkPublish(ThinkStyleWarehouse style) {
        QueryWrapper<ThinkStylePart> partQuery = new QueryWrapper<>();
        partQuery.lambda()
                .eq(ThinkStylePart::getStyleRandomCode, style.getRandomCode())
                .eq(ThinkStylePart::getIsValid, true)
                .orderByAsc(ThinkStylePart::getOrderNum);
        List<ThinkStylePart> parts = thinkStylePartDao.selectList(partQuery);
        if (ObjectUtils.isEmptyList(parts)) {
            return "该智库款无部件不允许发布";
        }


        for (ThinkStylePart part : parts) {
            if (part.getIsVirtual() == null || !part.getIsVirtual()) {
                //普通部件和特殊部件都做检查 特殊部件后续不检查？？？
                List<Long> randomCodes = partCraftMasterDataDao.getPartCraftMasterRandomCode(part.getDesignPartCode(), part.getPositionCode(), style.getClothesCategoryCode(), "定制");
                if (ObjectUtils.isEmptyList(randomCodes)) {
                    return "未找到部件工艺主数据" + part.getDesignPartCode() + " " + part.getPositionCode();
                }
                QueryWrapper<PartCraftMasterData> countQuery = new QueryWrapper<>();
                countQuery.lambda().eq(PartCraftMasterData::getRandomCode, randomCodes.get(0)).eq(PartCraftMasterData::getStatus, PUBLISHED_STATUS);
                int count = partCraftMasterDataDao.selectCount(countQuery);
                if (count != 1) {
                    return "部件主数据未发布" + part.getDesignPartCode() + " " + (null==part.getPositionCode()?"":part.getPositionCode());
                }

            }
        }


        return null;
    }

    private String null2Blank(String val) {
        return val != null ? val : "";
    }

    @Override
    public long addOrUpdateProductModelDataDao(String styleCode) {
        //更新智库款主表
        QueryWrapper<ProductModelMasterData> plmStyleQuery = new QueryWrapper<>();
        plmStyleQuery.lambda().eq(ProductModelMasterData::getCode, styleCode);
        ProductModelMasterData plmStyle = productModelMasterDataDao.selectOne(plmStyleQuery);
        if (plmStyle == null) {
            return 0;
        }

        QueryWrapper<ThinkStyleWarehouse> styleQuery = new QueryWrapper<>();
        List<Integer> statusList = Arrays.asList(null, DRAFT_STATUS, PUBLISHED_STATUS);
        styleQuery.lambda().eq(ThinkStyleWarehouse::getStyleCode, plmStyle.getCode()).in(ThinkStyleWarehouse::getStatus, statusList);
        ThinkStyleWarehouse oldStyle = thinkStyleWarehouseDao.selectOne(styleQuery);

        ThinkStyleWarehouse style = new ThinkStyleWarehouse();
        style.setClothesCategoryCode(plmStyle.getGrandCategory());
        //获取款式大类名称
        if (style.getClothesCategoryCode() != null && !style.getClothesCategoryCode().isEmpty()) {
            List<Dictionary> dicList = dictionaryService.getDictoryByValueAndType(style.getClothesCategoryCode(), "ClothesCategory");
            if (dicList != null) {
                for(Dictionary dic : dicList){
                style.setClothesCategoryName(dic.getValueDesc());
                }
            }
        }

        style.setStyleTypeCode(plmStyle.getStyleType());
        style.setStyleCode(plmStyle.getCode());
        style.setStyleName(plmStyle.getStyleName());
        style.setPictureUrl(plmStyle.getImageURL());
        style.setBrand(plmStyle.getBrand());
        style.setUpdateUser("PLM"); //先默认
        style.setUpdateUserName("PLM");
        style.setUpdateTime(new Date());


        if (oldStyle == null) {
            Long randomCode = SnowflakeIdUtil.generateId();
            if (style.getStyleCode() == null) {
                return 0;
            }

            String preStr = style.getStyleCode().substring(0, Math.min(6, style.getStyleCode().length()));
            String code = maxCodeService.getNextSerialNo("thinkStyleCode", preStr, 2, false);
            style.setThinkStyleCode(code);
            style.setRandomCode(randomCode);
            style.setCreateUser(style.getUpdateUser());
            style.setCreateUserName(style.getUpdateUserName());
            style.setCreateTime(style.getUpdateTime());

            style.setStatus(DRAFT_STATUS);
            thinkStyleWarehouseDao.insert(style);
        } else {
            //影响工艺的发布 状态自动变成草稿
            if ((plmStyle.getAffectCraft() == null ? false : plmStyle.getAffectCraft())) {
                style.setStatus(DRAFT_STATUS);
            }
            style.setRandomCode(oldStyle.getRandomCode());
            QueryWrapper<ThinkStyleWarehouse> styleUpdateQuery = new QueryWrapper<>();
            styleUpdateQuery.lambda().eq(ThinkStyleWarehouse::getRandomCode, oldStyle.getRandomCode());
            thinkStyleWarehouseDao.update(style, styleUpdateQuery);
        }

        //更新智库款部件表
        QueryWrapper<PlmMasterDataComponent> plmPartQuery = new QueryWrapper<>();
        plmPartQuery.lambda().eq(PlmMasterDataComponent::getStyleCode, styleCode).orderByAsc(PlmMasterDataComponent::getId);
        List<PlmMasterDataComponent> plmParts = plmMasterDataComponentDao.selectList(plmPartQuery);
        if (plmParts == null) {
            plmParts = new ArrayList<>();
        }
        //增加虚拟的部件
        int virtualPartCount = plmParts.size();
        plmParts.addAll(getVirtualParts());
        virtualPartCount = plmParts.size() - virtualPartCount;


        if (ObjectUtils.isNotEmptyList(plmParts)) {
            //所有的部件都刷成失效
            UpdateWrapper<ThinkStylePart> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().
                    eq(ThinkStylePart::getStyleRandomCode, style.getRandomCode()).
                    set(ThinkStylePart::getIsValid, false);
            int ret = thinkStylePartDao.update(null, updateWrapper);

            for (int i = 0; i < plmParts.size(); i++) {
                PlmMasterDataComponent plmPart = plmParts.get(i);
                String[] posArr = {""};
                if(StringUtils.isNotBlank(plmPart.getPositionCode())) {
                    posArr = plmPart.getPositionCode().split(",");
                }
                for(String posCode: posArr) {
                    //不存在的部件位置过滤掉
                    String positionName = null;
                    if (StringUtils.isNotBlank(posCode)) {
                        QueryWrapper<PartPosition> positionQueryWrapper = new QueryWrapper<>();
                        positionQueryWrapper.lambda()
                                .eq(PartPosition::getPartPositionCode, posCode)
                                .ne(PartPosition::getPartType, "bjposition");
                        //不用匹配部件类型 .eq(PartPosition::getPartType, plmPart.getPositionType());
                        List<PartPosition> partPositions = partPositionDao.selectList(positionQueryWrapper);
                        if (ObjectUtils.isNotEmptyList(partPositions)) {
                            positionName = partPositions.get(0).getPartPositionName();
                        } else {
                            positionName = "";
                            posCode = "";
                        }
                        
                    }


                    QueryWrapper<ThinkStylePart> partQuery = new QueryWrapper<>();
                    partQuery.lambda().eq(ThinkStylePart::getStyleRandomCode, style.getRandomCode())
                            .eq(ThinkStylePart::getDesignPartCode, null2Blank(plmPart.getComponentCode()))
                            .eq(ThinkStylePart::getPositionCode, null2Blank(posCode))
                            .eq(ThinkStylePart::getTopPartCode, null2Blank(plmPart.getTopPosition()))
                            .eq(ThinkStylePart::getParentPartCode, null2Blank(plmPart.getParentComponentCode()));

                    ThinkStylePart oldPart = thinkStylePartDao.selectOne(partQuery);
                    ThinkStylePart part = new ThinkStylePart();


                    part.setStyleRandomCode(style.getRandomCode());
                    part.setDesignPartCode(null2Blank(plmPart.getComponentCode()));
                    part.setDesignPartName(plmPart.getComponentName());
                    part.setIsDefault(plmPart.getIsDefaultComponent());

                    part.setTopPartCode(null2Blank(plmPart.getTopPosition()));
                    part.setParentPartCode(null2Blank(plmPart.getParentComponentCode()));

                    if(StringUtils.isNotBlank(positionName)) {
                        part.setPositionCode(posCode);
                        part.setPositionType(null2Blank(plmPart.getPositionType()));
                        part.setPositionName(null2Blank(positionName));
                    } else {
                        part.setPositionCode("");
                        part.setPositionType("");
                        part.setPositionName("");
                    }

                    if (part.getDesignPartName() == null && isNotBlank(part.getDesignPartCode())) {
                        QueryWrapper<DesignPart> designPartQueryWrapper = new QueryWrapper<>();
                        designPartQueryWrapper.lambda().eq(DesignPart::getDesignCode, part.getDesignPartCode());
                        List<DesignPart> designParts = designPartDao.selectList(designPartQueryWrapper);
                        if (ObjectUtils.isNotEmptyList(designParts)) {
                            part.setDesignPartName(designParts.get(0).getDesignName());
                        }
                    }

                    part.setIsValid(true);
                    part.setUpdateUser(style.getUpdateUser());
                    part.setUpdateTime(style.getUpdateTime());
                    part.setIsVirtual(i < plmParts.size() - virtualPartCount ? false : true);
                    if (part.getIsVirtual()) {
                        part.setIsSpecial(true);
                        part.setStatus(PUBLISHED_STATUS);
                    }


                    if (oldPart == null) {
                        Long randomCode = SnowflakeIdUtil.generateId();
                        part.setRandomCode(randomCode);
                        part.setCreateTime(part.getUpdateTime());
                        part.setCreateUser(part.getUpdateUser());
                        thinkStylePartDao.insert(part);
                    } else {
                        part.setRandomCode(oldPart.getRandomCode());
                        thinkStylePartDao.update(part, partQuery);
                    }
                }
            }
        }

        //先删除掉面料
        QueryWrapper<ThinkStyleFabric> deleteFabricQuery = new QueryWrapper<>();
        deleteFabricQuery.lambda().eq(ThinkStyleFabric::getStyleRandomCode, style.getRandomCode());
        thinkStyleFabricDao.delete(deleteFabricQuery);

        //更新面料
        QueryWrapper<PlmComponentMaterial> plmMaterialQuery = new QueryWrapper<>();
        plmMaterialQuery.lambda().eq(PlmComponentMaterial::getStyleCode, styleCode).orderByAsc(PlmComponentMaterial::getId);
        List<PlmComponentMaterial> plmMaterials = plmComponentMaterialDao.selectList(plmMaterialQuery);
        if (ObjectUtils.isNotEmptyList(plmMaterials)) {
            int i = 1;
            Set<String> materialCodeSet = new HashSet<>();
            for (PlmComponentMaterial plmMaterial : plmMaterials) {
                if (materialCodeSet.contains(plmMaterial.getDefaultMaterial())) {
                    //已经存在就不处理
                    continue;
                }
                materialCodeSet.add(plmMaterial.getDefaultMaterial());
                QueryWrapper<FabricMainData> fabricMainDataQueryWrapper = new QueryWrapper<>();
                fabricMainDataQueryWrapper.lambda().eq(FabricMainData::getCode, plmMaterial.getDefaultMaterial());
                FabricMainData fabricMainData = fabricMainDataDao.selectOne(fabricMainDataQueryWrapper);

                ThinkStyleFabric fabric = new ThinkStyleFabric();
                fabric.setStyleRandomCode(style.getRandomCode());
                fabric.setLineNo(i++);
                fabric.setUpdateTime(style.getUpdateTime());
                fabric.setUpdateUser(style.getUpdateUser());
                fabric.setMaterialCode(plmMaterial.getDefaultMaterial());
                fabric.setIsMain("1".equals(plmMaterial.getIsMainComponentOrMaterial()));
                fabric.setStyleRandomCode(style.getRandomCode());

                if (fabricMainData != null) {
                    fabric.setMaterialName(fabricMainData.getName());
                    fabric.setFabricScore(fabricMainData.getMaterialGrade()); //面料分值
                    fabric.setWeightGrade(fabricMainData.getWeightGrade()); //克重等级
                    fabric.setLatElasticGrade(fabricMainData.getWeftElasticGrade()); //纬向弹力等级
                    fabric.setMerElasticGrade(fabricMainData.getWarpElasticGrade()); //经向弹力等级
                    fabric.setYarnSlippage(fabricMainData.getHYGrade()); //纱线滑移性
                    fabric.setMaterialClass(fabricMainData.getMidCategory()); //物料中类
                    fabric.setBarType(fabricMainData.getPatternSymmetry()); //条格类型
                    fabric.setFabricColor(fabricMainData.getSystemName()); //面料颜色
                }

                Long randomCode = SnowflakeIdUtil.generateId();
                fabric.setRandomCode(randomCode);
                fabric.setCreateUser(style.getUpdateUser());
                fabric.setCreateTime(style.getUpdateTime());
                thinkStyleFabricDao.insert(fabric);
            }
        }
        //更新智库款时间
        thinkStyleWarehouseDao.updateStandardTime(style.getRandomCode(), style.getClothesCategoryCode());
        return style.getRandomCode();
    }

    @Override
    public List<CraftCategoryVo> getCraftCategoryVos() {
        return thinkStyleWarehouseDao.getCraftCategoryVos();
    }


    private List<PlmMasterDataComponent> getVirtualParts() {
        PlmMasterDataComponent part1 = new PlmMasterDataComponent();
        part1.setComponentCode("TQT001");
        part1.setComponentName("通用部件1");
        part1.setTopPosition("");
        part1.setPositionCode("");
        part1.setParentComponentCode("");


        PlmMasterDataComponent part2 = new PlmMasterDataComponent();
        part2.setComponentCode("TQT002");
        part2.setComponentName("通用部件2");
        part2.setTopPosition("");
        part2.setPositionCode("");
        part2.setParentComponentCode("");
        return Arrays.asList(part1, part2);
    }


}
