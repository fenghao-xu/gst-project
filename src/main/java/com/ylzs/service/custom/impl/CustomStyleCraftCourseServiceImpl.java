package com.ylzs.service.custom.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.constant.OderProcessingStatusConstants;
import com.ylzs.common.emuns.MaterialPropertyEnums;
import com.ylzs.common.process.RuleProcessType;
import com.ylzs.common.sort.CustomStyleCraftCourseCompare;
import com.ylzs.common.util.ListUtils;
import com.ylzs.common.util.RedisUtil;
import com.ylzs.common.util.SnowflakeIdUtil;
import com.ylzs.common.util.StringUtils;
import com.ylzs.common.utils.MapUtils;
import com.ylzs.common.utils.object.ObjectUtils;
import com.ylzs.dao.bigstylecraft.BigStyleAnalyseMasterDao;
import com.ylzs.dao.bigstylecraft.BigStyleAnalysePartCraftDao;
import com.ylzs.dao.bigstylecraft.StyleSewingCraftActionDao;
import com.ylzs.dao.craftstd.CraftCategoryDao;
import com.ylzs.dao.custom.CustomStyleCraftCourseDao;
import com.ylzs.dao.designPart.DesignPartDao;
import com.ylzs.dao.partCraft.PartCraftDesignPartsDao;
import com.ylzs.dao.staticdata.PartPositionDao;
import com.ylzs.entity.bigstylecraft.BigStyleAnalyseMaster;
import com.ylzs.entity.bigstylecraft.BigStyleAnalysePartCraft;
import com.ylzs.entity.bigstylecraft.BigStyleAnalysePartCraftDetail;
import com.ylzs.entity.craftmainframe.CraftMainFrame;
import com.ylzs.entity.craftstd.CraftCategory;
import com.ylzs.entity.craftstd.Machine;
import com.ylzs.entity.craftstd.PartPosition;
import com.ylzs.entity.custom.*;
import com.ylzs.entity.fabricProperty.resp.FabricPropertyDataResp;
import com.ylzs.entity.materialCraft.MaterialCraftProperty;
import com.ylzs.entity.materialCraft.MaterialCraftRule;
import com.ylzs.entity.materialCraft.MaterialCraftRulePart;
import com.ylzs.entity.materialCraft.resp.MaterialCraftAllDataResp;
import com.ylzs.entity.partCombCraft.PartCombCraftProgramDetail;
import com.ylzs.entity.partCombCraft.PartCombCraftRule;
import com.ylzs.entity.partCombCraft.resp.PartCombCraftAllDataResp;
import com.ylzs.entity.partCraft.PartCraftRule;
import com.ylzs.entity.plm.PICustomOrder;
import com.ylzs.entity.plm.PICustomOrderPart;
import com.ylzs.entity.plm.PICustomOrderPartMaterial;
import com.ylzs.entity.plm.PiCutParameterMarkerinfoHems;
import com.ylzs.entity.processCombCraft.ProcessCombCraftProgram;
import com.ylzs.entity.processCombCraft.ProcessCombCraftRule;
import com.ylzs.entity.processCombCraft.resp.ProcessCombCraftAllDataResp;
import com.ylzs.entity.sewingcraft.SewingCraftAction;
import com.ylzs.entity.sewingcraft.SewingCraftWarehouse;
import com.ylzs.entity.staticdata.CraftGradeEquipment;
import com.ylzs.entity.staticdata.PatternSymmetry;
import com.ylzs.entity.thinkstyle.ThinkStyleCraft;
import com.ylzs.entity.thinkstyle.ThinkStylePart;
import com.ylzs.entity.thinkstyle.ThinkStyleProcessRule;
import com.ylzs.entity.thinkstyle.ThinkStyleWarehouse;
import com.ylzs.entity.timeparam.FabricScore;
import com.ylzs.entity.timeparam.OrderGrade;
import com.ylzs.service.craftmainframe.ICraftMainFrameService;
import com.ylzs.service.craftstd.ICraftCategoryService;
import com.ylzs.service.craftstd.IMachineService;
import com.ylzs.service.craftstd.impl.DictionaryService;
import com.ylzs.service.custom.*;
import com.ylzs.service.fabricProperty.IFabricPropertyDataService;
import com.ylzs.service.impl.OriginServiceImpl;
import com.ylzs.service.materialCraft.IMaterialCraftService;
import com.ylzs.service.orderprocessing.OrderProcessingStatusService;
import com.ylzs.service.partCombCraft.IPartCombCraftService;
import com.ylzs.service.partCraft.IPartCraftDesignPartsService;
import com.ylzs.service.partCraft.IPartCraftPositionService;
import com.ylzs.service.partCraft.IPartCraftRuleService;
import com.ylzs.service.plm.IPICustomOrderPartMaterialService;
import com.ylzs.service.plm.IPICustomOrderService;
import com.ylzs.service.plm.IPiCutParameterMarkerinfoHemsService;
import com.ylzs.service.processCombCraft.IProcessCombCraftService;
import com.ylzs.service.sewingcraft.SewingCraftWarehouseService;
import com.ylzs.service.staticdata.PatternSymmetryService;
import com.ylzs.service.thinkstyle.IThinkStyleWarehouseService;
import com.ylzs.service.timeparam.CraftGradeEquipmentService;
import com.ylzs.service.timeparam.FabricGradeService;
import com.ylzs.service.timeparam.FabricScoreService;
import com.ylzs.service.timeparam.IOrderGradeService;
import com.ylzs.vo.DictionaryVo;
import com.ylzs.vo.bigstylereport.CraftVO;
import com.ylzs.vo.craftstd.FactoryVO;
import com.ylzs.vo.designpart.DesignPartVo;
import com.ylzs.vo.partCraft.DesignPartVO;
import com.ylzs.vo.sewing.VSewingCraftVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service()
@Transactional(rollbackFor = Exception.class)
public class CustomStyleCraftCourseServiceImpl extends OriginServiceImpl<CustomStyleCraftCourseDao, CustomStyleCraftCourse> implements ICustomStyleCraftCourseService {

    private static final Logger log = LoggerFactory.getLogger(CustomStyleCraftCourseServiceImpl.class);
    @Autowired
    private CustomStyleCraftCourseDao customStyleCraftCourseDao;
    @Autowired
    private IPartCombCraftService partCombCraftService;
    @Autowired
    private IProcessCombCraftService processCombCraftService;
    @Autowired
    private IThinkStyleWarehouseService thinkStyleWarehouseService;
    @Autowired
    private IPiCutParameterMarkerinfoHemsService hemsService;
    @Autowired
    private IPartCraftRuleService partCraftRuleService;
    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private IMaterialCraftService materialCraftService;
    @Autowired
    private IFabricPropertyDataService fabricPropertyDataService;
    @Autowired
    private SewingCraftWarehouseService sewingCraftWarehouseService;
    @Autowired
    private DesignPartDao designPartDao;
    @Autowired
    private IMachineService machineService;
    @Autowired
    private CraftGradeEquipmentService gradeEquipmentService;
    @Autowired
    private PatternSymmetryService patternSymmetryService;
    @Autowired
    private ICustomStyleRuleService styleRuleService;
    @Autowired
    private ICustomStylePartCraftService stylePartCraftService;
    @Autowired
    private ICustomStylePartService stylePartService;
    @Autowired
    private ICustomStylePartCraftMotionService stylePartCraftMotionService;
    @Autowired
    private ICustomStyleSewPositionService stylePartCraftPositionService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ICraftCategoryService craftCategoryService;
    @Autowired
    private IPICustomOrderPartMaterialService piCustomOrderPartMaterialService;
    @Autowired
    private IOrderGradeService orderGradeService;
    @Autowired
    private FabricScoreService fabricScoreService;
    @Autowired
    private FabricGradeService fabricGradeService;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Resource
    private IPartCraftDesignPartsService partCraftDesignPartsService;

    @Resource
    private CraftCategoryDao craftCategoryDao;

    @Resource
    private OrderProcessingStatusService orderProcessingStatusService;

    @Resource
    private PartCraftDesignPartsDao partCraftDesignPartsDao;

    @Resource
    private IPartCraftPositionService partCraftPositionService;

    @Resource
    private PartPositionDao partPositionDao;

    @Resource
    private BigStyleAnalyseMasterDao bigStyleAnalyseMasterDao;
    @Resource
    private BigStyleAnalysePartCraftDao bigStyleAnalysePartCraftDao;
    @Resource
    private StyleSewingCraftActionDao styleSewingCraftActionDao;

    @Resource
    private IPICustomOrderService piCustomOrderService;

    @Resource
    private ICraftMainFrameService craftMainFrameService;


    private static final String CRAFT_KEY = "craftList";
    private static final String PART_KEY = "parttList";
    private static final String RULE_KEY = "ruleList";
    private static final String CLOTHES_CODE_KEY = "clothesCategoryCode";
    private static final String CLOTHES_NAME_KEY = "clothesCategoryName";
    private static final String SEW_POSITION_KEY = "sewPosition";
    private static final String MAIN_FABRIC_KEY = "mainFabricObj";
    private static final String THINK_STYLE_NAME = "think_style_name";
    private static final String MAIN_MATERIAL_CODE = "mainMaterialCode";
    private static final String THINK_STYLE_INFO = "thinkStyleInfo";
    private static final String ORDER_EXCEPTION_FLAG = "orderExceptionFlag";

    private static List<MaterialCraftAllDataResp> excipientsMaterialList = new ArrayList<>();
    private static List<MaterialCraftAllDataResp> fabricsMaterialList = new ArrayList<>();
    static String excipientsCode = "";
    static String materialCode = "";

    private void init() {
        List<DictionaryVo> dictionaryVoList = dictionaryService.getDictoryAll("MaterialType");
        for (DictionaryVo vo : dictionaryVoList) {
            if (vo.getValueDesc().equalsIgnoreCase("面料")) {
                materialCode = vo.getDicValue();
            } else if (vo.getValueDesc().equalsIgnoreCase("辅料")) {
                excipientsCode = vo.getDicValue();
            }
        }
    }

    /**
     * 创建定制款工艺路径生成
     * 根据定制款订单部件数据获取智库款工艺数据及规则
     * 根据缝边数据或疏工艺
     * 过滤智库款工艺规则
     * 查询订单符合的部件工艺组合
     * 根据过滤条件替换工艺规则
     * 查询工序组合根据符合条件过滤工序规则
     * 查询辅料工艺过滤规则
     * 查询面料材料工艺过滤规则
     *
     * @param customOrder
     * @return
     */
    @Override
    public synchronized boolean createCustomStyleCratCouresData(final PICustomOrder customOrder) {
        if (getCustomOrderLine(customOrder.getOrderId(), customOrder.getOrderLineId()) > 0) {
            //      return true;
        }
        //收集在订单工艺创建过程中的数据,
        final Map<String, Object> resultMap = new HashMap<>();
        //初始状态是没有异常
        resultMap.put(ORDER_EXCEPTION_FLAG, Boolean.FALSE);

        //部件组合
        Map<String, Object> param = analyseThinkStyleCraft(customOrder, resultMap);
        try {

            if (null == param) {
                return false;
            }
            param = processCombPartCraftData(customOrder, param, resultMap);
            //处理工序组合工艺
            param = processDataByCategory(customOrder, param);
            //材料组合
            param = processMaterialCategoryExcipients(customOrder, param);
            param = processMaterialCategoryFabrics(customOrder, param);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return saveCustomStyleCraftCourse(customOrder, param, resultMap);
    }

    @Override
    public List<CustomStyleCraftCourse> searchStyleCraftCourseList(HashMap params) {
        List<CustomStyleCraftCourse> list = new ArrayList<>();
        try {
            list = customStyleCraftCourseDao.searchCustomStyleCraftMasterAll(params);
            list = list.stream().collect(Collectors.collectingAndThen(
                    Collectors.toCollection(() -> new TreeSet<>(Comparator.
                            comparing(x -> x.getRandomCode()))), ArrayList::new));
            if (null != list && list.size() > 0) {
                Collections.sort(list, new CustomStyleCraftCourseCompare());
            }
            List<CraftCategory> craftCategories = craftCategoryService.getAll();
            if (ObjectUtils.isNotEmptyList(craftCategories)) {
                for (CustomStyleCraftCourse vo : list) {
                    vo.setCustomOrderNo(vo.getOrderNo() + "-" + vo.getOrderLineId());
                    for (CraftCategory craftCategory : craftCategories) {
                        if (vo.getCraftCategoryCode().equalsIgnoreCase(craftCategory.getCraftCategoryCode())) {
                            vo.setCraftCategoryName(craftCategory.getCraftCategoryName());
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }


    //处理智库款工序
    private Map<String, Object> analyseThinkStyleCraft(PICustomOrder customOrder, Map<String, Object> resultMap) {
        Map<String, Object> param = new HashMap<>();
        ThinkStyleWarehouse thinkStyleInfo = thinkStyleWarehouseService.getThinkStyleInfo(customOrder.getStyleCode());
        if (ObjectUtils.isEmpty(thinkStyleInfo)) {
            try {
                //更新节点信息
                resultMap.put(ORDER_EXCEPTION_FLAG, Boolean.TRUE);
                orderProcessingStatusService.addOrUpdate(customOrder.getOrderId() + "-" + customOrder.getOrderLineId(), OderProcessingStatusConstants.CusOrderStatusName.CUS_ORDER_NAME_1150, OderProcessingStatusConstants.CusOrderStatus.CUS_ORDER_1150, "", "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        List<ThinkStyleCraft> thinkStyleCraftList = new ArrayList<>();
        //遍历对标定制订单部件与智库款设计部件数据，将相同设计部件、工序、规则取出
        //根据智库款号，设计部件，部件位置提取处理规则
        List<ThinkStylePart> handleStylePartList = new ArrayList<>();
        List<CustomStylePart> customStylePartList = new ArrayList<>();
        StringBuffer strBuf = new StringBuffer();
        StringBuffer manyBuf = new StringBuffer();
        //是否有设计部件未发布状态
        boolean hasPartNotPublished = false;
        //部件编码，部件位置，服装品类 三个条件一起查出来有多个的情况
        boolean hasTooManyPart = false;
        List<PartPosition> excludeList = partPositionDao.getPartPositionByType("bjposition");
        Map<String, String> posMap = new HashMap<>();
        if (null != excludeList && excludeList.size() > 0) {
            for (PartPosition pos : excludeList) {
                posMap.put(pos.getPartPositionCode(), pos.getPartPositionCode());
            }
        }
        //取出绣花部件
        Map<String, DesignPartVo> embroideryParts = designPartDao.getAllEmbroideryParts();
        //遍历订单和智库款的部件，放map里面
        //Map<String, String> orderPartMap = new HashMap<>();

        /*for (PICustomOrderPart orderPart : customOrder.getUnits()) {
            String orderDesignCode = orderPart.getUnitCode();
            if (StringUtils.isNotEmpty(orderDesignCode)) {
                orderPartMap.put(orderDesignCode, orderDesignCode);
            }
        }*/
        Map<String, String> thinkStylePartMap = new HashMap<>();
        for (ThinkStylePart stylePart : thinkStyleInfo.getPartList()) {
            String part = stylePart.getDesignPartCode();
            if (StringUtils.isNotEmpty(part)) {
                thinkStylePartMap.put(part, part);
            }
        }
        for (PICustomOrderPart orderPart : customOrder.getUnits()) {
            //处理订单里面设计部件的状态
            try {
                String orderDesignCode = orderPart.getUnitCode();
                String orderPosition = orderPart.getUnitPositionCode();
                //处理绣花部件
                try {
                    //绣花部件先从智库款取工艺，取不到的才从部件工艺里获取
                    if (!thinkStylePartMap.containsKey(orderDesignCode) && embroideryParts.containsKey(orderDesignCode)) {
                        //通过设计部件，部件位置，和服装品类找到部件工艺，然后再部件工艺里面取工序
                        List<ThinkStyleCraft> crafts = partCraftDesignPartsService.getPartCraftByDesignPartAndPosition(orderDesignCode, orderPosition, thinkStyleInfo.getClothesCategoryCode());
                        //通过设计部件，部件位置，和服装品类找到部件工艺，然后再部件工艺里面取工序规则
                        List<ThinkStyleProcessRule> rules = partCraftDesignPartsService.getPartCraftRulesByDesignPartAndPosition(orderDesignCode, orderPosition, thinkStyleInfo.getClothesCategoryCode());
                        //通过设计部件，部件位置，和服装品类找到部件工艺，然后再部件工艺里面取标准时间标准单价
                        List<ThinkStylePart> partList = partCraftDesignPartsService.getPartPriceAndTImeByDesignPartAndPosition(orderDesignCode, orderPosition, thinkStyleInfo.getClothesCategoryCode());
                        ThinkStylePart part = new ThinkStylePart();
                        if (null != crafts && crafts.size() > 0) {
                            part.setCraftList(crafts);
                        }
                        if (null != rules && rules.size() > 0) {
                            part.setRuleList(rules);
                        }
                        part.setPositionCode(orderPosition);
                        part.setDesignPartCode(orderDesignCode);
                        part.setIsVirtual(false);
                        part.setIsSpecial(false);
                        part.setIsDefault(false);
                        part.setOrderNum(1000);
                        if (null != partList && partList.size() > 0) {
                            part.setStandardPrice(partList.get(0).getStandardPrice());
                            part.setStandardTime(partList.get(0).getStandardTime());
                        }
                        //放到智库款工艺里面
                        if (thinkStyleInfo.getPartList() != null) {
                            thinkStyleInfo.getPartList().add(part);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("在部件工艺里面取绣花部件失败，" + e.getMessage());

                }

                //部件位置为空
                if (!posMap.containsKey(orderPosition)) {
                    if (StringUtils.isEmpty(orderPosition)) {
                        List<DesignPartVO> designPartVOList = partCraftDesignPartsService.getDataByDesignCode(orderDesignCode);
                        //此时订单里面这个设计部件的部件位置为空，所以要在部件工艺里面，找到这个设计部件，同时部件位置也为空
                        boolean hasPosition = true;
                        if (designPartVOList != null && designPartVOList.size() > 0) {
                            for (DesignPartVO partVO : designPartVOList) {
                                //必须要找到一个等于0的，也就是这个部件位置没有
                                Integer count = partCraftPositionService.getNumberByPartCraftMainRandomCode(partVO.getPartCraftMainRandomCode());
                                if (0 == count) {
                                    hasPosition = false;
                                }
                            }
                            //说明不存在部件位置为空的，所以这个设计部件编码在部件工艺里面找不到符合条件的，判断未发布
                            if (hasPosition) {
                                hasPartNotPublished = true;
                                strBuf.append("部件" + orderDesignCode + ";");
                            }
                        } else {
                            hasPartNotPublished = true;
                            strBuf.append("部件" + orderDesignCode + ";");
                        }
                    } else {
                        Integer count = partCraftDesignPartsDao.getCountByDesignPartAndPosition(orderDesignCode, orderPosition, thinkStyleInfo.getClothesCategoryCode());
                        if (count == 0) {
                            hasPartNotPublished = true;
                            strBuf.append("部件" + orderDesignCode + " " + orderPosition + ";");
                        } else if (count > 1) {
                            hasTooManyPart = true;
                            manyBuf.append("部件" + orderDesignCode + " " + orderPosition + "有" + count + "个;");
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            for (ThinkStylePart stylePart : thinkStyleInfo.getPartList()) {
                log.info("stylePartCode: " + stylePart.getDesignPartCode());
                log.info("orderPartCode: " + orderPart.getUnitCode());


                //判断当前是设计部件是否与定制款设计部件相当或者当起智库款设计部件是否为虚拟部件
                if (!stylePart.getIsVirtual() && StringUtils.isBlank(stylePart.getPositionCode()) &&
                        StringUtils.isBlank(orderPart.getUnitPositionCode()) &&
                        stylePart.getDesignPartCode().equalsIgnoreCase(orderPart.getUnitCode())) {
                    CustomStylePart part = new CustomStylePart();
                    part.setIsSpecial(stylePart.getIsSpecial());
                    part.setDesignPartCode(stylePart.getDesignPartCode());
                    part.setDesignPartName(stylePart.getDesignPartName());
                    part.setIsVirtual(stylePart.getIsVirtual());
                    //将符合条件的设计部件
                    handleStylePartList.add(stylePart);
                    customStylePartList.add(part);

                } else if (!stylePart.getIsVirtual() && StringUtils.isNotBlank(stylePart.getPositionCode()) &&
                        StringUtils.isNotBlank(orderPart.getUnitPositionCode()) &&
                        stylePart.getDesignPartCode().equalsIgnoreCase(orderPart.getUnitCode()) &&
                        orderPart.getUnitPositionCode().equalsIgnoreCase(stylePart.getPositionCode())) {
                    CustomStylePart part = new CustomStylePart();
                    part.setIsSpecial(stylePart.getIsSpecial());
                    part.setDesignPartCode(stylePart.getDesignPartCode());
                    part.setDesignPartName(stylePart.getDesignPartName());
                    part.setIsVirtual(stylePart.getIsVirtual());
                    part.setPositionCode(stylePart.getPositionCode());
                    part.setPositionName(stylePart.getPositionName());
                    //将符合条件的设计部件
                    handleStylePartList.add(stylePart);
                    customStylePartList.add(part);
                }
            }
        }
        if (hasPartNotPublished) {
            try {
                resultMap.put(ORDER_EXCEPTION_FLAG, Boolean.TRUE);
                orderProcessingStatusService.addOrUpdate(customOrder.getOrderId() + "-" + customOrder.getOrderLineId(), "有部件位没有发布，" + strBuf.toString(),
                        OderProcessingStatusConstants.CusOrderStatus.CUS_ORDER_1170, "", "");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if (hasTooManyPart) {
            try {
                resultMap.put(ORDER_EXCEPTION_FLAG, Boolean.TRUE);
                orderProcessingStatusService.addOrUpdate(customOrder.getOrderId() + "-" + customOrder.getOrderLineId(), manyBuf.toString(),
                        1165, "", "");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        for (ThinkStylePart stylePart : thinkStyleInfo.getPartList()) {
            log.info("stylePartCode: " + stylePart.getDesignPartCode());
            //将虚拟部件另存
            if (stylePart.getIsVirtual()) {
                //将符合条件的设计部件
                CustomStylePart part = new CustomStylePart();
                part.setIsSpecial(stylePart.getIsSpecial());
                part.setDesignPartCode(stylePart.getDesignPartCode());
                part.setDesignPartName(stylePart.getDesignPartName());
                part.setPositionCode(stylePart.getPositionCode());
                part.setPositionName(stylePart.getPositionName());
                part.setIsVirtual(stylePart.getIsVirtual());
                customStylePartList.add(part);
                handleStylePartList.add(stylePart);

            }
        }
        handleStylePartList = handleStylePartList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(
                () -> new TreeSet<>(Comparator.comparing(o -> o.getDesignPartCode() + "#" + o.getPositionCode()))), ArrayList::new));
        customStylePartList = customStylePartList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(
                () -> new TreeSet<>(Comparator.comparing(o -> o.getDesignPartCode() + "#" + o.getPositionCode()))), ArrayList::new));
        for (ThinkStylePart stylePart : handleStylePartList) {
            if (ObjectUtils.isNotEmptyList(stylePart.getCraftList())) {
                thinkStyleCraftList.addAll(stylePart.getCraftList());
            }
        }
        //对提取的工序进行去重处理
        thinkStyleCraftList = thinkStyleCraftList.stream().collect(Collectors.collectingAndThen(
                Collectors.toCollection(() -> new TreeSet<>(Comparator.
                        comparing(x -> x.getCraftCode() + "#" + x.getDesignPartCode() + "#" + x.getPositionCode()))), ArrayList::new));
        thinkStyleCraftList.stream().forEach(craft -> {
            craft.setSourceCraftName("智库款工艺");
        });
        //处理CAD缝边数据
        param = filterPositionRule(thinkStyleCraftList, customOrder.getOrderId(), customOrder.getOrderLineId(), thinkStyleInfo.getClothesCategoryCode(), resultMap);
        thinkStyleCraftList = MapUtils.getList(param, CRAFT_KEY);
        List<CustomStyleRule> rules = MapUtils.getList(param, RULE_KEY);
        if (ObjectUtils.isEmptyList(rules)) rules = new ArrayList<>();

        //对提取的智库款部件进行排序
        handleStylePartList = handleStylePartList.stream().sorted(Comparator.comparing(ThinkStylePart::getOrderNum)).collect(Collectors.toList());
        List<CustomStyleRule> partCraftRuleList = new ArrayList<>();
        Map<String, Object> resultParam = new HashMap<>();
        for (ThinkStylePart part : handleStylePartList) {
            if (ObjectUtils.isNotEmptyList(part.getRuleList())) {
                List<CustomStyleRule> tempList = ListUtils.copyList(part.getRuleList(), CustomStyleRule.class);
                tempList.forEach(rule -> {
                    rule.setDesignPartDesc(part.getDesignPartCode() + "" + part.getDesignPartName());
                    rule.setRemark("智库款工序"+ thinkStyleInfo.getThinkStyleCode());
                    rule.setRandomCode(SnowflakeIdUtil.generateId());
                    rule.setNember(2);
                });
                resultParam = getFilterThinkStyleCrafts(thinkStyleCraftList, tempList, "智库款工序");
                tempList.clear();
                thinkStyleCraftList = MapUtils.getList(resultParam, CRAFT_KEY);
                tempList.addAll(MapUtils.getList(resultParam, RULE_KEY));
                if (ObjectUtils.isNotEmptyList(tempList)) {
                    Iterator<CustomStyleRule> it = tempList.iterator();
                    while (it.hasNext()) {
                        CustomStyleRule key = it.next();
                        if (!key.getWhetherToExecute()) {
                            it.remove();
                        }
                    }
                   /* for (int i = 0; i < tempList.size(); i++) {
                        if (!tempList.get(i).getWhetherToExecute()) {
                            tempList.remove(i);
                            i--;
                        }
                    }*/
                    if (ObjectUtils.isNotEmptyList(tempList)) {
                        partCraftRuleList.addAll(tempList);
                    }
                }
            }
        }
        if (ObjectUtils.isNotEmptyList(partCraftRuleList)) {
            rules.addAll(partCraftRuleList);
        }
        PICustomOrderPartMaterial mainFabric = piCustomOrderPartMaterialService.getMainMaterialData(customOrder.getOrderId(),
                customOrder.getOrderLineId(), customOrder.getMainMaterialCode());
        if (ObjectUtils.isNotEmpt(mainFabric)) {
            if (StringUtils.isNotBlank(customOrder.getPatternSymmetry())) {
                mainFabric.setPatternSymmetry(customOrder.getPatternSymmetry());
            }
            param.put(MAIN_FABRIC_KEY, mainFabric);
        }
        param.put(CRAFT_KEY, thinkStyleCraftList);
        param.put(RULE_KEY, rules);
        param.put(PART_KEY, customStylePartList);
        param.put(CLOTHES_CODE_KEY, thinkStyleInfo.getClothesCategoryCode());
        param.put(CLOTHES_NAME_KEY, thinkStyleInfo.getClothesCategoryName());
        param.put(THINK_STYLE_NAME, thinkStyleInfo.getStyleName());
        param.put(MAIN_MATERIAL_CODE, customOrder.getMainMaterialCode());
        param.put(THINK_STYLE_INFO, thinkStyleInfo);
        return param;
    }

    //处理部件组合工艺
    private Map<String, Object> processCombPartCraftData(PICustomOrder customOrder, Map<String, Object> param, Map<String, Object> resultMap) {
        List<ThinkStyleCraft> thinkStyleCraftList = MapUtils.getList(param, CRAFT_KEY);
        String[] partCodeArray = new String[customOrder.getUnits().size()];
        String[] partCodeArrays = new String[customOrder.getUnits().size()];
        for (int i = 0; i < customOrder.getUnits().size(); i++) {
            PICustomOrderPart part = customOrder.getUnits().get(i);
            partCodeArray[i] = part.getUnitCode();
            partCodeArrays[i] = StringUtils.isNotBlank(part.getUnitPositionCode()) ?
                    part.getUnitCode() + "#" + part.getUnitPositionCode() : part.getUnitCode();
        }
        List<PartCombCraftAllDataResp> partCombCraftList = partCombCraftService.selectPartDataByCategoryCode(
                MapUtils.getString(param, CLOTHES_CODE_KEY), partCodeArray);
        if (ObjectUtils.isEmptyList(partCombCraftList)) {
            return param;
        }

        if (null != partCombCraftList && partCombCraftList.size() > 0) {
            boolean hasNoPublished = false;
            StringBuffer buf = new StringBuffer();
            for (PartCombCraftAllDataResp vo : partCombCraftList) {
                if (!BusinessConstants.Status.PUBLISHED_STATUS.equals(vo.getStatus())) {
                    hasNoPublished = true;
                    buf.append(vo.getPartCombCraftCode() + ";");
                }
            }
            //部件组合未发布XXXX（X表示部件组合编码，多个部件组合编码用，隔开）
            if (hasNoPublished) {
                try {
                    resultMap.put(ORDER_EXCEPTION_FLAG, Boolean.TRUE);
                    orderProcessingStatusService.addOrUpdate(customOrder.getOrderId() + "-" + customOrder.getOrderLineId(), buf.toString() + " " + OderProcessingStatusConstants.CusOrderStatusName.CUS_ORDER_NAME_1190,
                            OderProcessingStatusConstants.CusOrderStatus.CUS_ORDER_1190, "", "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        List<PartCombCraftRule> combCraftRuleList = new ArrayList<>();
        partCombCraftList.stream().forEach(resp -> {
            if (resp.getDetails() != null && resp.getDetails().size() > 0) {
                Map<Integer, List<PartCombCraftProgramDetail>> groupByDetail = resp.getDetails().stream().collect(
                        Collectors.groupingBy(PartCombCraftProgramDetail::getPartNumber));

                for (Integer key : groupByDetail.keySet()) {
                    List<PartCombCraftProgramDetail> detailList = groupByDetail.get(key);
                    int detailSize = detailList.size();
                    String[] detailArray = new String[detailSize];
                    for (int i = 0; i < detailList.size(); i++) {
                        PartCombCraftProgramDetail detail = detailList.get(i);
                        String detailPartCodes = StringUtils.isNotBlank(detail.getPartPositionCode()) ?
                                detail.getDesignCode() + "#" + detail.getPartPositionCode() : detail.getDesignCode();
                        detailArray[i] = detailPartCodes;
                    }
//                    if (detailSize == 1) {
//                        String detailArrayCode = detailArray[0];
//                        String[] ass = permutation(partCodeArrays, 1);
//                        if (Arrays.stream(ass).filter(str -> str.equalsIgnoreCase(detailArrayCode)).count() == 1) {
//                            combCraftRuleList.addAll(resp.getRules());
//                            break;
//                        }
//                    } else if (detailSize == 2) {
//                        String detailArrayCode = detailArray[0] + "_" + detailArray[1];
//                        String[] ass = permutation(partCodeArrays, 2);
//                        if (Arrays.stream(ass).filter(str -> str.equalsIgnoreCase(detailArrayCode)).count() == 1) {
//                            combCraftRuleList.addAll(resp.getRules());
//                            break;
//                        }
//                    } else if (detailSize == 3) {
//                        String detailArrayCode = detailArray[0] + "_" + detailArray[1] + "_" + detailArray[2];
//                        String[] ass = permutation(partCodeArrays, 3);
//                        if (Arrays.stream(ass).filter(str -> str.equalsIgnoreCase(detailArrayCode)).count() == 1) {
//                            combCraftRuleList.addAll(resp.getRules());
//                            break;
//                        }
//                    }
                    if(detailSize > 0) {
                        boolean finded = true;
                        for(String code: detailArray) {
                            finded = Arrays.stream(partCodeArrays).anyMatch(x->x.equalsIgnoreCase(code));
                            if(!finded) {
                                break;
                            }
                        }
                        if(finded) {
                            combCraftRuleList.addAll(resp.getRules());
                            break;
                        }
                    }



                }
            }
        });
        List<CustomStyleRule> ruleList = MapUtils.getList(param, RULE_KEY);
        if (ruleList == null) {
            ruleList = new ArrayList<>();
        }
        if (combCraftRuleList.size() > 0) {
            Map<String, Object> resultParam = new HashMap<>();
            List<CustomStyleRule> styleRuleList = ListUtils.copyList(combCraftRuleList, CustomStyleRule.class);
            if (ObjectUtils.isNotEmptyList(styleRuleList)) {
                int i=0;
                for (CustomStyleRule rule : styleRuleList) {
                    if (null != rule) {
                        PartCombCraftRule partCompRule = combCraftRuleList.get(i);
                        PartCombCraftAllDataResp partComb = partCombCraftList.stream()
                                .filter(x->partCompRule.getPartCombCraftRandomCode().equals(x.getRandomCode()))
                                .findFirst()
                                .orElse(null);

                        rule.setRemark("部件组合工艺"+(partComb!=null?partComb.getPartCombCraftCode():""));
                        rule.setRandomCode(SnowflakeIdUtil.generateId());
                        rule.setNember(3);
                    }
                    i++;
                }
            }
            resultParam = getFilterThinkStyleCrafts(thinkStyleCraftList, styleRuleList, "部件组合工艺");
            styleRuleList.clear();
            thinkStyleCraftList = MapUtils.getList(resultParam, CRAFT_KEY);
            styleRuleList = MapUtils.getList(resultParam, RULE_KEY);
            if (ObjectUtils.isNotEmptyList(styleRuleList)) {

                Iterator<CustomStyleRule> it = styleRuleList.iterator();
                while (it.hasNext()) {
                    if (!it.next().getWhetherToExecute()) {
                        it.remove();
                    }
                }
                /*for (int i = 0; i < styleRuleList.size(); i++) {
                    if (!styleRuleList.get(i).getWhetherToExecute()) {
                        styleRuleList.remove(i);
                        i--;
                    }
                }*/
                if (ObjectUtils.isNotEmptyList(styleRuleList)) {
                    ruleList.addAll(styleRuleList);
                }
            }
        }
        param.put(RULE_KEY, ruleList);
        param.put(CRAFT_KEY, thinkStyleCraftList);
        return param;
    }

    //处理工序组合工艺
    private Map<String, Object> processDataByCategory(PICustomOrder customOrder, Map<String, Object> param) {

        List<ThinkStyleCraft> thinkStyleCraftList = MapUtils.getList(param, CRAFT_KEY);
        List<ProcessCombCraftAllDataResp> combCraftList = processCombCraftService.selectProcessDataByCategoryCode(MapUtils.getString(param, CLOTHES_CODE_KEY));
        if (combCraftList == null || combCraftList.size() < 1) {
            return param;
        }
        String[] styleCraftArray = new String[thinkStyleCraftList.size()];
        for (int i = 0; i < thinkStyleCraftList.size(); i++) {
            styleCraftArray[i] = thinkStyleCraftList.get(i).getCraftCode();
        }
        List<ProcessCombCraftRule> combCraftRules = new ArrayList<>();
        combCraftList.stream().forEach(rese -> {
            Map<Integer, List<ProcessCombCraftProgram>> groupByDetail = rese.getPrograms().stream().collect(
                    Collectors.groupingBy(ProcessCombCraftProgram::getProcessNumber));
            for (Integer key : groupByDetail.keySet()) {
                List<ProcessCombCraftProgram> combCraftPrograms = groupByDetail.get(key);
                int craftGramSize = combCraftPrograms.size();
                String[] combCraftArray = new String[combCraftPrograms.size()];
                for (int i = 0; i < combCraftPrograms.size(); i++) {
                    combCraftArray[i] = combCraftPrograms.get(i).getProcessCraftCode();
                }
//                if (craftGramSize == 1) {
//                    if (Arrays.stream(styleCraftArray).filter(str -> str.equalsIgnoreCase(combCraftArray[0])).count() == 1) {
//                        combCraftRules.addAll(rese.getRules());
//                        break;
//                    }
//                }
//                if (craftGramSize == 2) {
//                    String codes = combCraftArray[0] + "_" + combCraftArray[1];
//                    String[] styleTwoArray = permutation(styleCraftArray, 2);
//                    if (Arrays.stream(styleTwoArray).filter(str -> str.equalsIgnoreCase(codes)).count() == 1) {
//                        combCraftRules.addAll(rese.getRules());
//                        break;
//                    }
//                }
//                if (craftGramSize == 3) {
//                    String codes = combCraftArray[0] + "_" + combCraftArray[1];
//                    String[] styleTwoArray = permutation(styleCraftArray, 3);
//                    if (Arrays.stream(styleTwoArray).filter(str -> str.equalsIgnoreCase(codes)).count() == 1) {
//                        combCraftRules.addAll(rese.getRules());
//                        break;
//                    }
//                }
                if(craftGramSize > 0) {
                    boolean finded = true;
                    for(String code: combCraftArray) {
                        finded = Arrays.stream(styleCraftArray).anyMatch(x->x.equalsIgnoreCase(code));
                        if(!finded) {
                            break;
                        }
                    }
                    if(finded) {
                        combCraftRules.addAll(rese.getRules());
                        break;
                    }
                }
            }
        });
        List<CustomStyleRule> ruleList = MapUtils.getList(param, RULE_KEY);
        if (ruleList == null) {
            ruleList = new ArrayList<>();
        }
        if (combCraftRules.size() > 0) {
            Map<String, Object> resultParam = new HashMap<>();
            List<CustomStyleRule> styleRuleList = ListUtils.copyList(combCraftRules, CustomStyleRule.class);
            int i=0;
            for (CustomStyleRule rule : styleRuleList) {
                ProcessCombCraftRule processCombRule = combCraftRules.get(i);
                ProcessCombCraftAllDataResp prcessComb = combCraftList.stream()
                        .filter(x->processCombRule.getProcessCombCraftRandomCode().equals(x.getRandomCode()))
                        .findFirst()
                        .orElse(null);

                rule.setRemark("工序组合工艺"+(prcessComb!=null?prcessComb.getProcessCombCraftCode():""));
                rule.setRandomCode(SnowflakeIdUtil.generateId());
                rule.setNember(4);
                i++;
            }
            resultParam = getFilterThinkStyleCrafts(thinkStyleCraftList, styleRuleList, "工序组合工艺");
            styleRuleList.clear();
            thinkStyleCraftList = MapUtils.getList(resultParam, CRAFT_KEY);
            styleRuleList = MapUtils.getList(resultParam, RULE_KEY);
            if (ObjectUtils.isNotEmptyList(styleRuleList)) {
                Iterator<CustomStyleRule> it = styleRuleList.iterator();
                while (it.hasNext()) {
                    if (!it.next().getWhetherToExecute()) {
                        it.remove();
                    }
                }
                /*for (int i = 0; i < styleRuleList.size(); i++) {
                    if (!styleRuleList.get(i).getWhetherToExecute()) {
                        styleRuleList.remove(i);
                        i--;
                    }
                }*/
                if (ObjectUtils.isNotEmptyList(styleRuleList)) {
                    ruleList.addAll(styleRuleList);
                }
            }
        }
        param.put(RULE_KEY, ruleList);
        param.put(CRAFT_KEY, thinkStyleCraftList);
        return param;
    }

    //处理材料工艺辅料
    private Map<String, Object> processMaterialCategoryExcipients(PICustomOrder customOrder, Map<String, Object> param) {

        init();
        List<ThinkStyleCraft> thinkStyleCraftList = MapUtils.getList(param, CRAFT_KEY);


        excipientsMaterialList = materialCraftService.selectMaterialCraftDataByKindCode(excipientsCode);
        List<FabricPropertyDataResp> propertyDataResps = fabricPropertyDataService.selectMateriLCodePropertyDataList(excipientsCode);
        if (ObjectUtils.isEmptyList(excipientsMaterialList)) return param;


        List<MaterialCraftAllDataResp> newExcipientsMaterialList = new ArrayList<>();
        List<MaterialCraftRule> filterMaterialRule = new ArrayList<>();

        for (PICustomOrderPart units : customOrder.getUnits()) {
            List<PICustomOrderPartMaterial> excipientsList = new ArrayList<>();
            if (ObjectUtils.isNotEmptyList(units.getUms())) {
                for (PICustomOrderPartMaterial material : units.getUms()) {
                    if (material.getGrandCategory().equalsIgnoreCase("F") && StringUtils.isNotBlank(material.getSmallCategory())) {
                        excipientsList.add(material);
                    }
                }
                if (ObjectUtils.isNotEmptyList(excipientsList)) {
                    //提取辅料材料数据
                    for (PICustomOrderPartMaterial material : excipientsList) {
                        for (MaterialCraftAllDataResp dataResp : excipientsMaterialList) {
                            if (ObjectUtils.isEmptyList(dataResp.getProperties())) continue;
                            boolean rept = false;
                            for (MaterialCraftProperty property : dataResp.getProperties()) {
                                if (material.getSmallCategory().equalsIgnoreCase(property.getFabricPropertyDataCode())) {

                                    rept = true;

                                    break;
                                }
                            }

                            if (rept) {
                                int specialNum = 0;
                                if (ObjectUtils.isNotEmptyList(dataResp.getParts())) {
                                    for (MaterialCraftRulePart part : dataResp.getParts()) {
                                        if (StringUtils.isNotBlank(part.getPartPositionCode()) &&
                                                StringUtils.isNotBlank(units.getUnitPositionCode()) &&
                                                part.getDesignCode().equalsIgnoreCase(units.getUnitCode()) &&
                                                part.getPartPositionCode().equalsIgnoreCase(units.getUnitPositionCode())) {
                                            specialNum = part.getSpecialPlanNumber();
                                            break;
                                        } else if (StringUtils.isBlank(part.getPartPositionCode()) &&
                                                StringUtils.isBlank(units.getUnitPositionCode()) &&
                                                part.getDesignCode().equalsIgnoreCase(units.getUnitCode())) {
                                            specialNum = part.getSpecialPlanNumber();
                                            break;
                                        }
                                    }
                                }
                                for (MaterialCraftRule rule : dataResp.getRules()) {
                                    if (rule.getSpecialPlanNumber().equals(specialNum)) {
                                        filterMaterialRule.add(rule);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        List<CustomStyleRule> ruleList = MapUtils.getList(param, RULE_KEY);
        if (ObjectUtils.isEmptyList(ruleList)) ruleList = new ArrayList<>();

        if (ObjectUtils.isNotEmptyList(filterMaterialRule)) {
            Map<String, Object> resultParam = new HashMap<>();
            List<CustomStyleRule> styleRuleList = ListUtils.copyList(filterMaterialRule, CustomStyleRule.class);
            int i=0;
            for (CustomStyleRule rule : styleRuleList) {
                MaterialCraftRule materialCraftRule = filterMaterialRule.get(i);
                MaterialCraftAllDataResp materialCraft = excipientsMaterialList.stream()
                        .filter(x->materialCraftRule.getMaterialCraftRandomCode().equals(x.getRandomCode()))
                        .findFirst()
                        .orElse(null);


                rule.setRemark("辅料工艺"+(materialCraft!=null?materialCraft.getMaterialCraftCode():""));
                rule.setRandomCode(SnowflakeIdUtil.generateId());
                rule.setNember(5);
                i++;
            }
            styleRuleList = styleRuleList.stream().collect(Collectors.collectingAndThen(
                    Collectors.toCollection(() -> new TreeSet<>(Comparator.
                            comparing(x -> x.getSourceCraftCode() + "#" + x.getProcessType() + "#" + x.getActionCraftCode()))), ArrayList::new));
            resultParam = getFilterThinkStyleCrafts(thinkStyleCraftList, styleRuleList, "辅料工艺");
            styleRuleList.clear();
            thinkStyleCraftList = MapUtils.getList(resultParam, CRAFT_KEY);
            styleRuleList = MapUtils.getList(resultParam, RULE_KEY);
            if (ObjectUtils.isNotEmptyList(styleRuleList)) {
                Iterator<CustomStyleRule> it = styleRuleList.iterator();
                while (it.hasNext()) {
                    if (!it.next().getWhetherToExecute()) {
                        it.remove();
                    }
                }
                /*for (int i = 0; i < styleRuleList.size(); i++) {
                    if (!styleRuleList.get(i).getWhetherToExecute()) {
                        styleRuleList.remove(i);
                        i--;
                    }
                }*/
                if (ObjectUtils.isNotEmptyList(styleRuleList)) {
                    ruleList.addAll(styleRuleList);
                }
            }

        }
        param.put(RULE_KEY, ruleList);
        param.put(CRAFT_KEY, thinkStyleCraftList);
        excipientsMaterialList.clear();
        return param;
    }


    //处理材料工艺面料
    private Map<String, Object> processMaterialCategoryFabrics(PICustomOrder customOrder, Map<String, Object> param) {
        init();
        List<ThinkStyleCraft> thinkStyleCraftList = MapUtils.getList(param, CRAFT_KEY);
        for (PICustomOrderPart units : customOrder.getUnits()) {
            if (ObjectUtils.isNotEmptyList(units.getUms())) {
                Iterator<PICustomOrderPartMaterial> it = units.getUms().iterator();
                while (it.hasNext()) {
                    if (it.next().getGrandCategory().equalsIgnoreCase("M")) {
                        it.remove();
                    }
                }
                /*for (int i = 0; i < units.getUms().size(); i++) {
                    if (!units.getUms().get(i).getGrandCategory().equalsIgnoreCase("M")) {
                        units.getUms().remove(i);
                        i--;
                    }
                }*/
            }
        }
        fabricsMaterialList = materialCraftService.selectMaterialCraftDataByKindCode(materialCode);
        List<FabricPropertyDataResp> propertyDataResps = fabricPropertyDataService.selectMateriLCodePropertyDataList(materialCode);
        if (ObjectUtils.isEmptyList(fabricsMaterialList) || ObjectUtils.isEmptyList(propertyDataResps)) {
            return param;
        }
        List<MaterialCraftAllDataResp> newExcipientsMaterialList = new ArrayList<>();
        List<MaterialCraftRule> filterMaterialRule = new ArrayList<>();
        PICustomOrderPartMaterial mainFabric = MapUtils.getObject(param, MAIN_FABRIC_KEY);
        //颜色材料属性
        String colorPropertyCode = "";
        //材料中类
        String smallCategoryCode = "";
        //移滑性材料属性
        String hyGradeCode = "";
        //克重等级材料属性
        String weightGradeCode = "";
        //维向伸长率等级
        String weftElasticGradeCode = "";
        //经向伸长率等级
        String warpElasticGradeCode = "";
        //条格对称
        String patternSymmetryCode = "";

        if (mainFabric != null) {
            //根据面料属性值获取对应面料属性编码
            for (FabricPropertyDataResp resp : propertyDataResps) {
                //颜色材料属性
                if (StringUtils.isNotBlank(mainFabric.getColorSystemCode())) {
                    if (resp.getFabricPropertyCode().equalsIgnoreCase(MaterialPropertyEnums.PrimaryHue.getCode()) &&
                            resp.getPropertyValueCode().equalsIgnoreCase(mainFabric.getColorSystemCode())) {
                        colorPropertyCode = resp.getFabricPropertyCode() + "#" + resp.getPropertyValueCode();
                    }
                }
                //中类材料属性
                if (StringUtils.isNotBlank(mainFabric.getSmallCategory())) {
                    if (mainFabric.getSmallCategory().equalsIgnoreCase(resp.getPropertyValueCode())) {
                        smallCategoryCode = resp.getFabricPropertyCode() + "#" + resp.getPropertyValueCode();
                    }
                }
                //移滑性材料属性
                if (StringUtils.isNotBlank(mainFabric.getHYGrade())) {
                    if (resp.getFabricPropertyCode().equalsIgnoreCase(MaterialPropertyEnums.YarnSlippage.getCode()) &&
                            resp.getPropertyValueCode().equalsIgnoreCase(mainFabric.getHYGrade())) {
                        hyGradeCode = resp.getFabricPropertyCode() + "#" + resp.getPropertyValueCode();
                    }
                }
                //克重材料属性
                if (StringUtils.isNotBlank(mainFabric.getWeightGrade())) {
                    if (resp.getFabricPropertyCode().equalsIgnoreCase(MaterialPropertyEnums.WeightGrade.getCode()) &&
                            resp.getPropertyValueCode().equalsIgnoreCase(mainFabric.getWeightGrade())) {
                        weightGradeCode = resp.getFabricPropertyCode() + "#" + resp.getPropertyValueCode();
                    }
                }
                //经向伸长登记率属性
                if (StringUtils.isNotBlank(mainFabric.getWarpElasticGrade())) {
                    if (resp.getFabricPropertyCode().equalsIgnoreCase(MaterialPropertyEnums.WarpExtensionRatio.getCode()) &&
                            resp.getPropertyValueCode().equalsIgnoreCase(mainFabric.getWarpElasticGrade())) {
                        warpElasticGradeCode = resp.getFabricPropertyCode() + "#" + resp.getPropertyValueCode();
                    }
                }
                //维向伸长等级率属性
                if (StringUtils.isNotBlank(mainFabric.getWeftElasticGrade())) {
                    if (resp.getFabricPropertyCode().equalsIgnoreCase(MaterialPropertyEnums.WeftExtensionRatio.getCode()) &&
                            resp.getPropertyValueCode().equalsIgnoreCase(mainFabric.getWeftElasticGrade())) {
                        weftElasticGradeCode = resp.getFabricPropertyCode() + "#" + resp.getPropertyValueCode();
                    }
                }
                //条格对称等级率属性
                if (StringUtils.isNotBlank(mainFabric.getPatternSymmetry())) {
                    if (resp.getFabricPropertyCode().equalsIgnoreCase(MaterialPropertyEnums.patternSymmetry.getCode()) &&
                            resp.getPropertyValueCode().equalsIgnoreCase(mainFabric.getPatternSymmetry())) {
                        patternSymmetryCode = resp.getFabricPropertyCode() + "#" + resp.getPropertyValueCode();
                    }
                }
            }
            String[] mainFabricPropertyArray = {colorPropertyCode, smallCategoryCode, hyGradeCode, weightGradeCode, weftElasticGradeCode, warpElasticGradeCode, patternSymmetryCode};
            mainFabricPropertyArray = filterNullPropertyArrayData(mainFabricPropertyArray);
            Arrays.sort(mainFabricPropertyArray);
            //根据面料属性和属性值对比面料工艺取出规则
            for (MaterialCraftAllDataResp mas : fabricsMaterialList) {
                if (ObjectUtils.isEmptyList(mas.getProperties())) continue;

                int propertiesSize = mas.getProperties().size();
                String[] propertyArray = new String[propertiesSize];
                for (int i = 0; i < mas.getProperties().size(); i++) {
                    MaterialCraftProperty property = mas.getProperties().get(i);
                    propertyArray[i] = property.getFabricPropertyCode() + "#" + property.getFabricPropertyDataCode();
                }
                Arrays.sort(propertyArray);
                if (propertiesSize == 1) {
                    //颜色材料属性
                    if (StringUtils.isNotBlank(colorPropertyCode) && colorPropertyCode.equalsIgnoreCase(propertyArray[0])) {
                        if (mas.getPlanNumber().contains(",")) {
                            newExcipientsMaterialList.add(mas);
                        } else {
                            filterMaterialRule.addAll(mas.getRules());
                        }
                    }
                    //中类材料属性
                    if (StringUtils.isNotBlank(smallCategoryCode) && smallCategoryCode.equalsIgnoreCase(propertyArray[0])) {
                        if (mas.getPlanNumber().contains(",")) {
                            newExcipientsMaterialList.add(mas);
                        } else {
                            filterMaterialRule.addAll(mas.getRules());
                        }
                    }
                    //移滑性材料属性
                    if (StringUtils.isNotBlank(hyGradeCode) && hyGradeCode.equalsIgnoreCase(propertyArray[0])) {
                        if (mas.getPlanNumber().contains(",")) {
                            newExcipientsMaterialList.add(mas);
                        } else {
                            filterMaterialRule.addAll(mas.getRules());
                        }
                    }
                    //克重材料属性
                    if (StringUtils.isNotBlank(weightGradeCode) && weightGradeCode.equalsIgnoreCase(propertyArray[0])) {
                        if (mas.getPlanNumber().contains(",")) {
                            newExcipientsMaterialList.add(mas);
                        } else {
                            filterMaterialRule.addAll(mas.getRules());
                        }
                    }
                    //经向伸长登记率属性
                    if (StringUtils.isNotBlank(warpElasticGradeCode) && warpElasticGradeCode.equalsIgnoreCase(propertyArray[0])) {
                        if (mas.getPlanNumber().contains(",")) {
                            newExcipientsMaterialList.add(mas);
                        } else {
                            filterMaterialRule.addAll(mas.getRules());
                        }

                    }
                    //维向伸长等级率属性
                    if (StringUtils.isNotBlank(weftElasticGradeCode) && weftElasticGradeCode.equalsIgnoreCase(propertyArray[0])) {
                        if (mas.getPlanNumber().contains(",")) {
                            newExcipientsMaterialList.add(mas);
                        } else {
                            filterMaterialRule.addAll(mas.getRules());
                        }
                    }
                    //条格对称等级率属性
                    if (StringUtils.isNotBlank(patternSymmetryCode) && patternSymmetryCode.equalsIgnoreCase(propertyArray[0])) {
                        if (mas.getPlanNumber().contains(",")) {
                            newExcipientsMaterialList.add(mas);
                        } else {
                            filterMaterialRule.addAll(mas.getRules());
                        }
                    }
                } else if (propertiesSize == 2) {
                    if (Arrays.stream(mainFabricPropertyArray).filter(str -> str.contains(propertyArray[0])).count() == 1 &&
                            Arrays.stream(mainFabricPropertyArray).filter(str -> str.contains(propertyArray[1])).count() == 1) {
                        if (mas.getPlanNumber().contains(",")) {
                            newExcipientsMaterialList.add(mas);
                        } else {
                            filterMaterialRule.addAll(mas.getRules());
                        }

                    }

                } else if (propertiesSize == 3) {
                    if (Arrays.stream(mainFabricPropertyArray).filter(str -> str.contains(propertyArray[0])).count() == 1 &&
                            Arrays.stream(mainFabricPropertyArray).filter(str -> str.contains(propertyArray[1])).count() == 1 &&
                            Arrays.stream(mainFabricPropertyArray).filter(str -> str.contains(propertyArray[2])).count() == 1) {
                        if (mas.getPlanNumber().contains(",")) {
                            newExcipientsMaterialList.add(mas);
                        } else {
                            filterMaterialRule.addAll(mas.getRules());
                        }

                    }
                }
            }
        }

        if (newExcipientsMaterialList.size() > 0) {
            for (PICustomOrderPart units : customOrder.getUnits()) {
                if (ObjectUtils.isEmptyList(units.getUms())) continue;
                for (MaterialCraftAllDataResp dataResp : newExcipientsMaterialList) {
                    Map<Integer, List<MaterialCraftRule>> groupByMateriLCraftRules = dataResp.getRules().stream()
                            .collect(Collectors.groupingBy(MaterialCraftRule::getRuleType));
                    for (MaterialCraftRulePart rulePart : dataResp.getParts()) {
                        if (StringUtils.isNotBlank(units.getUnitPositionCode()) &&
                                StringUtils.isNotBlank(rulePart.getPartPositionCode()) &&
                                units.getUnitCode().equalsIgnoreCase(rulePart.getDesignCode()) &&
                                units.getUnitPositionCode().equalsIgnoreCase(rulePart.getPartPositionCode())) {
                            filterMaterialRule.addAll(groupByMateriLCraftRules.get(2));
                            String[] ruleMainCodeArray = rulePart.getMaterialCraftRuleRandomCodes().contains(",") ?
                                    rulePart.getMaterialCraftRuleRandomCodes().split(",") : null;
                            if (ruleMainCodeArray != null) {
                                List<MaterialCraftRule> rules = groupByMateriLCraftRules.get(2);
                                for (int i = 0; i < ruleMainCodeArray.length; i++) {
                                    for (MaterialCraftRule rule : rules) {
                                        if (ruleMainCodeArray[i].equalsIgnoreCase(rule.getRandomCode().toString())) {
                                            filterMaterialRule.add(rule);
                                        }
                                    }
                                }
                            }
                        } else if (units.getUnitCode().equalsIgnoreCase(rulePart.getDesignCode())) {
                            filterMaterialRule.addAll(groupByMateriLCraftRules.get(2));
                            String[] ruleMainCodeArray = rulePart.getMaterialCraftRuleRandomCodes().contains(",") ?
                                    rulePart.getMaterialCraftRuleRandomCodes().split(",") : null;
                            if (ruleMainCodeArray != null) {
                                List<MaterialCraftRule> rules = groupByMateriLCraftRules.get(2);
                                for (int i = 0; i < ruleMainCodeArray.length; i++) {
                                    for (MaterialCraftRule rule : rules) {
                                        if (ruleMainCodeArray[i].equalsIgnoreCase(rule.getRandomCode().toString())) {
                                            filterMaterialRule.add(rule);
                                        }
                                    }
                                }
                            }
                        } else {
                            //   filterMaterialRule.addAll(groupByMateriLCraftRules.get(1));
                        }
                    }
                }
            }
        }
        List<CustomStyleRule> ruleList = MapUtils.getList(param, RULE_KEY);
        if (ruleList == null) {
            ruleList = new ArrayList<>();
        }
        if (filterMaterialRule.size() > 0) {
            Map<String, Object> resultParam = new HashMap<>();
            List<CustomStyleRule> styleRuleList = ListUtils.copyList(filterMaterialRule, CustomStyleRule.class);
            int i=0;
            for (CustomStyleRule rule : styleRuleList) {
                MaterialCraftRule materialCraftRule = filterMaterialRule.get(i);
                MaterialCraftAllDataResp materialCraft = fabricsMaterialList.stream()
                        .filter(x->materialCraftRule.getMaterialCraftRandomCode().equals(x.getRandomCode()))
                        .findFirst()
                        .orElse(null);

                rule.setRemark("面料工艺"+(materialCraft!=null?materialCraft.getMaterialCraftCode():""));
                rule.setRandomCode(SnowflakeIdUtil.generateId());
                rule.setNember(6);
                i++;
            }
            resultParam = getFilterThinkStyleCrafts(thinkStyleCraftList, styleRuleList, "面料工艺");
            styleRuleList.clear();
            thinkStyleCraftList = MapUtils.getList(resultParam, CRAFT_KEY);
            styleRuleList = MapUtils.getList(resultParam, RULE_KEY);
            if (ObjectUtils.isNotEmptyList(styleRuleList)) {
                Iterator<CustomStyleRule> it = styleRuleList.iterator();
                while (it.hasNext()) {
                    if (!it.next().getWhetherToExecute()) {
                        it.remove();
                    }
                }
                /*for (int i = 0; i < styleRuleList.size(); i++) {
                    if (!styleRuleList.get(i).getWhetherToExecute()) {
                        styleRuleList.remove(i);
                        i--;
                    }
                }*/
                if (ObjectUtils.isNotEmptyList(styleRuleList)) {
                    ruleList.addAll(styleRuleList);
                }
            }
            param.put(RULE_KEY, ruleList);
        }
        param.put(CRAFT_KEY, thinkStyleCraftList);
        fabricsMaterialList.clear();
        return param;
    }

    private String[] filterNullPropertyArrayData(String[] mainFabricPropertyArray) {
        List<String> tmp = new ArrayList<String>();
        for (String str : mainFabricPropertyArray) {
            if (str != null && str.length() != 0) {
                tmp.add(str);
            }
        }
        return mainFabricPropertyArray = tmp.toArray(new String[0]);
    }


    //根据缝边位置，过滤工序规则
    private Map<String, Object> filterPositionRule(List<ThinkStyleCraft> thinkStyleCraftList, String orderId, String orderLineId, String clothingCagegoryCode, Map<String, Object> resultMap) {
        Map<String, Object> param = new HashMap<>();
        //查询CAD缝边信息
        List<PiCutParameterMarkerinfoHems> hemsList = hemsService.getOrderMarkerInfoHemosAll(orderId, orderLineId);
        if (ObjectUtils.isEmptyList(hemsList)) {
            param.put(CRAFT_KEY, thinkStyleCraftList);
            return param;
        }
        // String craftCategoryCode = craftCategoryService.getCraftCategoryCode(clothingCagegoryCode);
        List<PartCraftRule> craftRules = new ArrayList<>();
        if (ObjectUtils.isNotEmptyList(hemsList)) {
            boolean hasNotPublished = false;
            StringBuffer stringBuffer = new StringBuffer();
            for (PiCutParameterMarkerinfoHems hems : hemsList) {
                List<PartCraftRule> craftRule = partCraftRuleService.getPartPositionRuleAll(hems.getHemsCode(), clothingCagegoryCode);
                if (ObjectUtils.isNotEmptyList(craftRule)) {
                    try {
                        for (PartCraftRule rule : craftRule) {
                            //如果状态是未发布的，就提示
                            if (!BusinessConstants.Status.PUBLISHED_STATUS.equals(rule.getPartCraftMainStatus())) {
                                hasNotPublished = true;
                                stringBuffer.append(hems.getHemsCode() + " " + clothingCagegoryCode + ";");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    craftRules.addAll(craftRule);
                }
            }
            //隐线部件未发布，请处理！
            if (hasNotPublished) {
                try {
                    resultMap.put(ORDER_EXCEPTION_FLAG, Boolean.TRUE);
                    orderProcessingStatusService.addOrUpdate(orderId + "-" + orderLineId, stringBuffer.toString() + OderProcessingStatusConstants.CusOrderStatusName.CUS_ORDER_NAME_1180,
                            OderProcessingStatusConstants.CusOrderStatus.CUS_ORDER_1180, "", "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (ObjectUtils.isNotEmptyList(craftRules)) {
            Map<String, Object> resultParam = new HashMap<>();
            List<CustomStyleRule> styleRuleList = ListUtils.copyList(craftRules, CustomStyleRule.class);
            for (CustomStyleRule rule : styleRuleList) {
                rule.setRemark("部件工艺" + (rule.getRemark()!=null?rule.getRemark():"")); //缝边工序
                rule.setRandomCode(SnowflakeIdUtil.generateId());
                rule.setNember(1);
            }
            //执行替换规则
            resultParam = getFilterThinkStyleCrafts(thinkStyleCraftList, styleRuleList, "缝边工序");
            styleRuleList.clear();
            thinkStyleCraftList = MapUtils.getList(resultParam, CRAFT_KEY);
            styleRuleList = MapUtils.getList(resultParam, RULE_KEY);
            if (ObjectUtils.isNotEmptyList(styleRuleList)) {
                Iterator<CustomStyleRule> it = styleRuleList.iterator();
                while (it.hasNext()) {
                    if (!it.next().getWhetherToExecute()) {
                        it.remove();
                    }
                }
                /*for (int i = 0; i < styleRuleList.size(); i++) {
                    if (!styleRuleList.get(i).getWhetherToExecute()) {
                        styleRuleList.remove(i);
                        i--;
                    }
                }*/
                if (ObjectUtils.isNotEmptyList(styleRuleList)) {
                    param.put(RULE_KEY, styleRuleList);
                }
            }
        }
        param.put(CRAFT_KEY, thinkStyleCraftList);
        param.put(SEW_POSITION_KEY, hemsList);
        return param;
    }

    //执行替换规则
    private Map<String, Object> getFilterThinkStyleCrafts(List<ThinkStyleCraft> thinkStyleCraftList, List<CustomStyleRule> partCraftRuleList, String sourceCraftName) {
        Map<String, Object> params = new HashMap<>();
        //根据processingSortNum排序，如果为空，就默认赋值0
        List<CustomStyleRule> sortedByProcessingSortNumPartCraftRule = partCraftRuleList.stream().map(x -> {
            if (x.getProcessingSortNum() == null) x.setProcessingSortNum(0);
            return x;
        }).sorted(Comparator.comparing(CustomStyleRule::getProcessingSortNum)).collect(Collectors.toList());
        List<CustomStyleRule> newRules = new ArrayList<>();
        if (ObjectUtils.isNotEmptyList(thinkStyleCraftList) && ObjectUtils.isNotEmptyList(sortedByProcessingSortNumPartCraftRule)) {
            for (CustomStyleRule rule : sortedByProcessingSortNumPartCraftRule) {
                //执行减少工序操作
                if (RuleProcessType.PROCESS_TYPE_CUT_BACK.equals(rule.getProcessType())) {
                    List<CustomStyleRule> cutCraftRules = new ArrayList<>();
                    cutCraftRules.add(rule);
                    if (ObjectUtils.isNotEmptyList(cutCraftRules) && ObjectUtils.isNotEmptyList(thinkStyleCraftList)) {
                        params = handleThinkStyleCutCraftRule(thinkStyleCraftList, cutCraftRules);
                        thinkStyleCraftList = MapUtils.getList(params, CRAFT_KEY);
                        newRules.addAll(MapUtils.getList(params, RULE_KEY));
                    }
                } else if (RuleProcessType.PROCESS_TYPE_ADD.equals(rule.getProcessType())) {
                    //执行增加工序操作
                    List<CustomStyleRule> addCraftRules = new ArrayList<>();
                    addCraftRules.add(rule);
                    params = handleThinkStyleAddCraftRule(thinkStyleCraftList, addCraftRules, sourceCraftName);
                    thinkStyleCraftList = MapUtils.getList(params, CRAFT_KEY);
                    newRules.addAll(MapUtils.getList(params, RULE_KEY));
                } else if (RuleProcessType.PROCESS_TYPE_REPLACE.equals(rule.getProcessType())) {
                    List<CustomStyleRule> replaceCraftRules = new ArrayList<>();
                    replaceCraftRules.add(rule);
                    params = handleThinkStyleReplaceCraftRule(thinkStyleCraftList, replaceCraftRules, sourceCraftName);
                    thinkStyleCraftList = MapUtils.getList(params, CRAFT_KEY);
                    newRules.addAll(MapUtils.getList(params, RULE_KEY));
                }
            }
        }


      /*Map<Integer, List<CustomStyleRule>> groupByPartCraftRule = partCraftRuleList.stream().collect(Collectors.groupingBy(CustomStyleRule::getProcessType));
        List<CustomStyleRule> cutCraftRules = MapUtils.getList(groupByPartCraftRule, RuleProcessType.PROCESS_TYPE_CUT_BACK);
        List<CustomStyleRule> addCraftRules = MapUtils.getList(groupByPartCraftRule, RuleProcessType.PROCESS_TYPE_ADD);
        List<CustomStyleRule> replaceCraftRules = MapUtils.getList(groupByPartCraftRule, RuleProcessType.PROCESS_TYPE_REPLACE);

        List<CustomStyleRule> newRules = new ArrayList<>();
        //执行减少工序操作
        if (ObjectUtils.isNotEmptyList(cutCraftRules) && ObjectUtils.isNotEmptyList(thinkStyleCraftList)) {
            params = handleThinkStyleCutCraftRule(thinkStyleCraftList, cutCraftRules);
            thinkStyleCraftList = MapUtils.getList(params, CRAFT_KEY);
            newRules.addAll(MapUtils.getList(params, RULE_KEY));
        }
        //执行替换工序操作
        if (ObjectUtils.isNotEmptyList(replaceCraftRules) && ObjectUtils.isNotEmptyList(thinkStyleCraftList)) {
            params = handleThinkStyleReplaceCraftRule(thinkStyleCraftList, replaceCraftRules, sourceCraftName);
            thinkStyleCraftList = MapUtils.getList(params, CRAFT_KEY);
            newRules.addAll(MapUtils.getList(params, RULE_KEY));
        }
        //执行增加工序操作
        if (ObjectUtils.isNotEmptyList(addCraftRules) && ObjectUtils.isNotEmptyList(thinkStyleCraftList)) {
            params = handleThinkStyleAddCraftRule(thinkStyleCraftList, addCraftRules, sourceCraftName);
            thinkStyleCraftList = MapUtils.getList(params, CRAFT_KEY);
            newRules.addAll(MapUtils.getList(params, RULE_KEY));
        }*/
        params.clear();
        params.put(CRAFT_KEY, thinkStyleCraftList);
        params.put(RULE_KEY, newRules);
        return params;
    }

    //处理智库款替换工序操作
    private Map<String, Object> handleThinkStyleReplaceCraftRule(List<ThinkStyleCraft> thinkStyleCraftList, List<CustomStyleRule> cutCraftRules, String sourceCraftName) {
        Map<String, Object> params = new HashMap<>();
        List<String> cutCraftCodeList = new ArrayList<>();
        List<ThinkStyleCraft> repayCraftCodeList = new ArrayList<>();
        for (CustomStyleRule cutRule : cutCraftRules) {
            Boolean repate = false;
            if (StringUtils.isBlank(cutRule.getSourceCraftCode())) {
                cutRule.setWhetherToExecute(false);
                continue;
            }
            String sourceCraft = cutRule.getSourceCraftCode();
            String actionCraft = cutRule.getActionCraftCode();
            String[] sourceCraftArray = sourceCraft.contains("#") ? sourceCraft.split("#") : null;
            String[] actionCraftArray = actionCraft.contains("#") ? actionCraft.split("#") : null;

            if (ObjectUtils.isEmpty(sourceCraftArray)) {
                boolean res = useList(thinkStyleCraftList, sourceCraft);
                if (res) {
                    cutCraftCodeList.add(sourceCraft);
                    if (ObjectUtils.isEmpty(actionCraftArray)) {
                        ThinkStyleCraft craft = new ThinkStyleCraft();
                        craft.setCraftCode(actionCraft);
                        repayCraftCodeList.add(craft);
                        repate = true;
                    }
                    if (!ObjectUtils.isEmpty(actionCraftArray)) {
                        ThinkStyleCraft craftA = new ThinkStyleCraft();
                        craftA.setCraftCode(actionCraftArray[0]);
                        ThinkStyleCraft craftB = new ThinkStyleCraft();
                        craftB.setCraftCode(actionCraftArray[1]);
                        repayCraftCodeList.add(craftA);
                        repayCraftCodeList.add(craftB);
                        repate = true;
                    }
                }
            }
            if (!ObjectUtils.isEmpty(sourceCraftArray)) {
                boolean b1 = useList(thinkStyleCraftList, sourceCraftArray[0]);
                boolean b2 = useList(thinkStyleCraftList, sourceCraftArray[1]);
                if (b1 && b2) {
                    cutCraftCodeList.add(sourceCraftArray[0]);
                    cutCraftCodeList.add(sourceCraftArray[1]);
                    if (ObjectUtils.isEmpty(actionCraftArray)) {
                        ThinkStyleCraft craft = new ThinkStyleCraft();
                        craft.setCraftCode(actionCraft);
                        repayCraftCodeList.add(craft);
                        repate = true;
                    }
                    if (!ObjectUtils.isEmpty(actionCraftArray)) {
                        ThinkStyleCraft craftA = new ThinkStyleCraft();
                        craftA.setCraftCode(actionCraftArray[0]);
                        ThinkStyleCraft craftB = new ThinkStyleCraft();
                        craftB.setCraftCode(actionCraftArray[1]);
                        repayCraftCodeList.add(craftA);
                        repayCraftCodeList.add(craftB);
                        repate = true;
                    }
                }
            }
            if (!repate) {
                cutRule.setWhetherToExecute(false);
            }
        }
        if (ObjectUtils.isNotEmptyList(cutCraftCodeList)) {
            for (String code : cutCraftCodeList) {
                Iterator<ThinkStyleCraft> it = thinkStyleCraftList.iterator();
                while (it.hasNext()) {
                    ThinkStyleCraft craft = it.next();
                    if (craft.getCraftCode().equalsIgnoreCase(code)) {
                        it.remove();
                    }
                }
               /* for (int i = 0; i < thinkStyleCraftList.size(); i++) {
                    ThinkStyleCraft craft = thinkStyleCraftList.get(i);
                    if (craft.getCraftCode().equalsIgnoreCase(code)) {
                        thinkStyleCraftList.remove(i);
                        i--;
                    }
                }*/
            }
        }

        repayCraftCodeList.stream().forEach(craft -> {
            craft.setSourceCraftName(sourceCraftName);
        });
        thinkStyleCraftList.addAll(repayCraftCodeList);
        thinkStyleCraftList = thinkStyleCraftList.stream().collect(Collectors.collectingAndThen(
                Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(ThinkStyleCraft::getCraftCode))), ArrayList::new));
        params.put(CRAFT_KEY, thinkStyleCraftList);
        params.put(RULE_KEY, cutCraftRules);
        return params;
    }

    //处理智库款增加工序操作
    private Map<String, Object> handleThinkStyleAddCraftRule(List<ThinkStyleCraft> thinkStyleCraftList, List<CustomStyleRule> cutCraftRules, String sourceCraftName) {
        Map<String, Object> params = new HashMap<>();
        List<ThinkStyleCraft> newCraftCodeList = new ArrayList<>();
        Boolean repate = false;
        for (CustomStyleRule addRule : cutCraftRules) {
            String sourceCraft = addRule.getSourceCraftCode() != null ? addRule.getSourceCraftCode() : null;
            String actionCraft = addRule.getActionCraftCode();
            String[] sourceCraftArray = sourceCraft != null ? (sourceCraft.contains("#") ? sourceCraft.split("#") : null) : null;
            String[] actionCraftArray = actionCraft.contains("#") ? actionCraft.split("#") : null;

            if (StringUtils.isBlank(sourceCraft)) {
                if (ObjectUtils.isEmpty(actionCraftArray)) {
                    ThinkStyleCraft ca = new ThinkStyleCraft();
                    ca.setCraftCode(actionCraft);
                    newCraftCodeList.add(ca);
                    repate = true;
                }
                if (!ObjectUtils.isEmpty(actionCraftArray)) {
                    ThinkStyleCraft ca = new ThinkStyleCraft();
                    ThinkStyleCraft cb = new ThinkStyleCraft();
                    ca.setCraftCode(actionCraftArray[0]);
                    cb.setCraftCode(actionCraftArray[1]);
                    newCraftCodeList.add(ca);
                    newCraftCodeList.add(cb);
                    repate = true;
                }
            }
            if (StringUtils.isNotBlank(sourceCraft) && ObjectUtils.isEmpty(sourceCraftArray)) {
                boolean res = useList(thinkStyleCraftList, sourceCraft);
                if (res) {
                    if (ObjectUtils.isEmpty(actionCraftArray)) {
                        ThinkStyleCraft ca = new ThinkStyleCraft();
                        ca.setCraftCode(actionCraft);
                        newCraftCodeList.add(ca);
                        repate = true;
                    }
                    if (!ObjectUtils.isEmpty(actionCraftArray)) {
                        ThinkStyleCraft ca = new ThinkStyleCraft();
                        ThinkStyleCraft cb = new ThinkStyleCraft();
                        ca.setCraftCode(actionCraftArray[0]);
                        cb.setCraftCode(actionCraftArray[1]);
                        newCraftCodeList.add(ca);
                        newCraftCodeList.add(cb);
                        repate = true;
                    }
                }
            }
            if (!ObjectUtils.isEmpty(sourceCraftArray)) {
                boolean b1 = useList(thinkStyleCraftList, sourceCraftArray[0]);
                boolean b2 = useList(thinkStyleCraftList, sourceCraftArray[1]);
                if (b1 && b2) {
                    if (ObjectUtils.isEmpty(actionCraftArray)) {
                        ThinkStyleCraft ca = new ThinkStyleCraft();
                        ca.setCraftCode(actionCraft);
                        newCraftCodeList.add(ca);
                        repate = true;
                    }
                    if (!ObjectUtils.isEmpty(actionCraftArray)) {
                        ThinkStyleCraft ca = new ThinkStyleCraft();
                        ThinkStyleCraft cb = new ThinkStyleCraft();
                        ca.setCraftCode(actionCraftArray[0]);
                        cb.setCraftCode(actionCraftArray[1]);
                        newCraftCodeList.add(ca);
                        newCraftCodeList.add(cb);
                        repate = true;
                    }
                }
            }
            if (!repate) {
                addRule.setWhetherToExecute(false);
            }
        }
        newCraftCodeList.stream().forEach(craft -> {
            craft.setSourceCraftName(sourceCraftName);
        });
        thinkStyleCraftList.addAll(newCraftCodeList);
        thinkStyleCraftList = thinkStyleCraftList.stream().collect(Collectors.collectingAndThen(
                Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(ThinkStyleCraft::getCraftCode))), ArrayList::new));
        params.put(CRAFT_KEY, thinkStyleCraftList);
        params.put(RULE_KEY, cutCraftRules);
        return params;
    }

    //处理减少工序规则
    private Map<String, Object> handleThinkStyleCutCraftRule(List<ThinkStyleCraft> thinkStyleCraftList, List<CustomStyleRule> cutCraftRules) {
        Map<String, Object> params = new HashMap<>();
        List<String> newCraftCode = new ArrayList<>();
        for (CustomStyleRule cutRule : cutCraftRules) {
            String sourceCraft = cutRule.getSourceCraftCode() != null ? cutRule.getSourceCraftCode() : null;
            String actionCraft = cutRule.getActionCraftCode();
            String[] sourceCraftArray = sourceCraft != null ? (sourceCraft.contains("#") ? sourceCraft.split("#") : null) : null;
            String[] actionCraftArray = actionCraft.contains("#") ? actionCraft.split("#") : null;
            Boolean repate = false;
            //原工序代码为空，直接执行新增工序
            if (StringUtils.isBlank(sourceCraft) || StringUtils.isBlank(sourceCraft.trim())) {
                if (ObjectUtils.isEmpty(actionCraftArray)) {
                    newCraftCode.add(actionCraft);
                    repate = true;
                }
                if (!ObjectUtils.isEmpty(actionCraftArray)) {
                    newCraftCode.add(actionCraftArray[0]);
                    newCraftCode.add(actionCraftArray[1]);
                    repate = true;
                }
            }
            if (StringUtils.isNotBlank(sourceCraft) && ObjectUtils.isEmpty(sourceCraftArray)) {
                boolean res = useList(thinkStyleCraftList, sourceCraft);
                if (res) {
                    if (ObjectUtils.isEmpty(actionCraftArray)) {
                        newCraftCode.add(actionCraft);
                        repate = true;
                    }
                    if (!ObjectUtils.isEmpty(actionCraftArray)) {
                        newCraftCode.add(actionCraftArray[0]);
                        newCraftCode.add(actionCraftArray[1]);
                        repate = true;
                    }
                }
            }
            if (!ObjectUtils.isEmpty(sourceCraftArray)) {
                boolean b1 = useList(thinkStyleCraftList, sourceCraftArray[0]);
                boolean b2 = useList(thinkStyleCraftList, sourceCraftArray[1]);
                if (b1 && b2) {
                    if (ObjectUtils.isEmpty(actionCraftArray)) {
                        newCraftCode.add(actionCraft);
                        repate = true;
                    }
                    if (!ObjectUtils.isEmpty(actionCraftArray)) {
                        newCraftCode.add(actionCraftArray[0]);
                        newCraftCode.add(actionCraftArray[1]);
                        repate = true;
                    }
                }
            }
            if (!repate) {
                cutRule.setWhetherToExecute(false);
            }
        }
        if (ObjectUtils.isNotEmptyList(newCraftCode)) {
            for (String code : newCraftCode) {
                Iterator<ThinkStyleCraft> it = thinkStyleCraftList.iterator();
                while (it.hasNext()) {
                    ThinkStyleCraft craft = it.next();
                    if (craft.getCraftCode().equalsIgnoreCase(code)) {
                        it.remove();
                    }
                }
                /*for (int i = 0; i < thinkStyleCraftList.size(); i++) {
                    ThinkStyleCraft styleCraft = thinkStyleCraftList.get(i);
                    if (styleCraft.getCraftCode().equalsIgnoreCase(code)) {
                        thinkStyleCraftList.remove(i);
                        i--;
                    }
                }*/
            }
        }
        params.put(CRAFT_KEY, thinkStyleCraftList);
        params.put(RULE_KEY, cutCraftRules);
        return params;
    }

    //保存定制款工艺路径信息
    private Boolean saveCustomStyleCraftCourse(PICustomOrder piCustomOrder, Map<String, Object> param, Map<String, Object> resultMap) {
        Boolean reqPet = true;
        //获取工序列表
        List<ThinkStyleCraft> thinkStyleCraftList = MapUtils.getList(param, CRAFT_KEY);
        //规则列表
        List<CustomStyleRule> styleRuleList = MapUtils.getList(param, RULE_KEY);
        //主面料函数
        PICustomOrderPartMaterial mainFabric = MapUtils.getObject(param, MAIN_FABRIC_KEY);
        //缝边位置列表
        List<PiCutParameterMarkerinfoHems> hemsList = MapUtils.getList(param, SEW_POSITION_KEY);
        //智库款对象
        ThinkStyleWarehouse thinkStyleWarehouse = MapUtils.getObject(param, THINK_STYLE_INFO);
        if (ObjectUtils.isNotEmptyList(styleRuleList)) {
            styleRuleList = buileStyleRuleList(styleRuleList);
        }


        try {
            //组装定制款订单工艺主数据
            CustomStyleCraftCourse custom = new CustomStyleCraftCourse();
            custom.setRandomCode(SnowflakeIdUtil.generateId());
            String craftCategoryCode = craftCategoryService.getCraftCategoryCode(MapUtils.getString(param, CLOTHES_CODE_KEY));
            custom.setCraftCategoryCode(craftCategoryCode);
            if (Boolean.FALSE.equals(resultMap.get(ORDER_EXCEPTION_FLAG))) {
                custom.setStatus(BusinessConstants.Status.DRAFT_STATUS);
            } else {
                custom.setStatus(BusinessConstants.Status.ORDER_EXCEPTION);
            }
            List<CraftMainFrame> craftMainFrames = craftMainFrameService.getByCraftCategoryAndType(craftCategoryCode, BusinessConstants.BusinessType.CUSTOMIZE);
            custom.setStyleCode(piCustomOrder.getCustStyleCode());
            custom.setThinkStyleName(MapUtils.getString(param, THINK_STYLE_NAME));
            custom.setStyleDescript(thinkStyleWarehouse.getThinkStyleDesc());
            custom.setOrderNo(piCustomOrder.getOrderId());
            custom.setOrderLineId(piCustomOrder.getOrderLineId());
            custom.setCustomerName(piCustomOrder.getCustomerName());
            custom.setClothesCategoryCode(MapUtils.getString(param, CLOTHES_CODE_KEY));
            custom.setClothesCategoryName(MapUtils.getString(param, CLOTHES_NAME_KEY));
            custom.setMainMaterialCode(MapUtils.getString(param, MAIN_MATERIAL_CODE));

            custom.setFabricsScore(mainFabric.getMaterialGrade());
            custom.setBrand(dictionaryService.getBrandName(thinkStyleWarehouse.getBrand()));
            custom.setStylePicture(thinkStyleWarehouse.getPictureUrl());
            //  custom.setMainFrameCode(thinkStyleWarehouse.getCraftMainFrameCode());
            // custom.setMainFrameName(thinkStyleWarehouse.getCraftMainFrameName());
            //重新刷新工艺主框架
            if (null != craftMainFrames && craftMainFrames.size() > 0) {
                custom.setMainFrameCode(craftMainFrames.get(0).getMainFrameCode());
                custom.setMainFrameName(craftMainFrames.get(0).getMainFrameName());
            }
            custom.setFabricDirection(piCustomOrder.getFabricDirection());
            custom.setReleaseVersion("1.0");
            custom.setCreateUser("plm");
            custom.setRemark("系统自动生成");
            for (CustomStyleRule rule : styleRuleList) {
                rule.setCustomMainRandomCode(custom.getRandomCode());
                rule.setCreateUser("plm");
                rule.setUpdateUser(null);
                rule.setAuditTime(null);
                rule.setAuditUser(null);
                rule.setStatus(custom.getStatus());
                rule.setId(null);
            }
            //组装定制款订单部件数据
            List<CustomStylePart> customStylePartList = MapUtils.getList(param, PART_KEY);
            List<PatternSymmetry> patternSymmetryList = patternSymmetryService.getAllPatternSymmetrys();
            String patternSummertyName = "";
            if (ObjectUtils.isNotEmptyList(patternSymmetryList) && StringUtils.isNotBlank(mainFabric.getPatternSymmetry())) {
                for (PatternSymmetry patternSymmetry : patternSymmetryList) {
                    if (patternSymmetry.getCode().equalsIgnoreCase(mainFabric.getPatternSymmetry())) {
                        patternSummertyName = patternSymmetry.getName();
                        break;
                    }
                }
            }
            for (int i = 0; i < customStylePartList.size(); i++) {
                CustomStylePart part = customStylePartList.get(i);
                part.setRandomCode(SnowflakeIdUtil.generateId());
                part.setCustomMainRandomCode(custom.getRandomCode());
                part.setStatus(custom.getStatus());
                part.setCreateUser(custom.getCreateUser());
                part.setUpdateUser(null);
                part.setAuditTime(null);
                part.setAuditUser(null);
                part.setStatus(custom.getStatus());
                part.setId(null);
            }
            String[] craftCodeArray = new String[thinkStyleCraftList.size()];
            List<CustomStylePartCraft> customStylePartCraftList = new ArrayList<>();
            //把经过工序组合，材料工艺，部件组合处理的工序存起来，因为要做工序流的刷新
            Map<String, String> specialMap = new HashMap<>();
            List<String> sprcialList = new ArrayList<>();

            for (int i = 0; i < thinkStyleCraftList.size(); i++) {
                ThinkStyleCraft thinkStyleCraft = thinkStyleCraftList.get(i);
                craftCodeArray[i] = thinkStyleCraft.getCraftCode();
                sprcialList.add(thinkStyleCraft.getCraftCode());
                if ("面料工艺".equals(thinkStyleCraft.getSourceCraftName()) || "辅料工艺".equals(thinkStyleCraft.getSourceCraftName())
                        || "工序组合工艺".equals(thinkStyleCraft.getSourceCraftName()) || "部件组合工艺".equals(thinkStyleCraft.getSourceCraftName())) {
                    specialMap.put(thinkStyleCraft.getCraftCode(), thinkStyleCraft.getCraftCode());
                }
            }
            //查询经过规则处理过的工序，新的工序流
            Map<String, SewingCraftWarehouse> map = new HashMap<>();
            if (null != craftMainFrames && craftMainFrames.size() > 0) {
                map = sewingCraftWarehouseService.getCraftFlowNumByCraftCodeAndMainFrameCode(sprcialList, craftMainFrames.get(0).getMainFrameCode());
            }

            List<VSewingCraftVo> sewingCraftWarehouseList = sewingCraftWarehouseService.getCraftCodeDataAll(craftCodeArray,
                    mainFabric.getMaterialGrade(), thinkStyleWarehouse.getCraftMainFrameCode());
            List<CustomStylePartCraftMotion> motionList = new ArrayList<>();
            List<CustomStyleSewPosition> craftPositionList = new ArrayList<>();
            //组装缝边位置数据
            if (ObjectUtils.isNotEmptyList(hemsList)) {
                for (PiCutParameterMarkerinfoHems hems : hemsList) {
                    CustomStyleSewPosition partCraftPosition = new CustomStyleSewPosition();
                    partCraftPosition.setRandomCode(SnowflakeIdUtil.generateId());
                    partCraftPosition.setStatus(custom.getStatus());
                    partCraftPosition.setSewPositionCode(hems.getHemsCode());
                    partCraftPosition.setCustomMainRandomCode(custom.getRandomCode());
                    partCraftPosition.setSewPositionValue(hems.getValue());
                    craftPositionList.add(partCraftPosition);
                }
            }

            if (ObjectUtils.isNotEmptyList(sewingCraftWarehouseList)) {
                // sewingCraftWarehouseList.stream().forEach(craftvo -> {
                for (VSewingCraftVo craftvo : sewingCraftWarehouseList) {
                    craftvo.setRandomCode(SnowflakeIdUtil.generateId());
                    craftvo.setStatus(custom.getStatus());
                    craftvo.setStandardTime(craftvo.getStandardTime() != null ? craftvo.getStandardTime() : BigDecimal.ZERO.setScale(3));
                    craftvo.setStandardPrice(craftvo.getStandardPrice() != null ? craftvo.getStandardPrice() : BigDecimal.ZERO.setScale(3));
                    if (ObjectUtils.isNotEmptyList(craftvo.getMotionsList())) {
                        List<CustomStylePartCraftMotion> list = ListUtils.copyList(craftvo.getMotionsList(), CustomStylePartCraftMotion.class);
                        list.stream().forEach(motion -> {
                            motion.setId(null);
                            motion.setStatus(custom.getStatus());
                            motion.setCreateUser(custom.getCreateUser());
                            motion.setRandomCode(SnowflakeIdUtil.generateId());
                            motion.setPartCraftRandomCode(craftvo.getRandomCode());
                            motion.setUpdateUser(null);
                            motion.setAuditDate(null);
                            motion.setAuditTime(null);
                            motion.setAuditUser(null);
                            motion.setUpdateUser(null);
                        });
                        motionList.addAll(list);
                    }
                    //开始刷新工序流
                    if (map.containsKey(craftvo.getCraftCode()) && map.get(craftvo.getCraftCode()).getCraftFlowNum() != null) {
                        craftvo.setCraftFlowNum(map.get(craftvo.getCraftCode()).getCraftFlowNum().toString());
                    }
                }
            }
            customStylePartCraftList = ListUtils.copyList(sewingCraftWarehouseList, CustomStylePartCraft.class);
            List<OrderGrade> orderGradeList = orderGradeService.getAllOrderGrade();
            for (CustomStylePartCraft craft : customStylePartCraftList) {
                craft.setFabricScore(custom.getFabricsScore());
                for (ThinkStyleCraft thinkStyleCraft : thinkStyleCraftList) {
                    if (craft.getCraftCode().equalsIgnoreCase(thinkStyleCraft.getCraftCode())) {
                        craft.setDesignPartCode(thinkStyleCraft.getDesignPartCode());
                        craft.setSourceCraftName(thinkStyleCraft.getSourceCraftName());
                        if (StringUtils.isNotBlank(thinkStyleCraft.getPositionCode())) {
                            craft.setPartPositionCode(thinkStyleCraft.getPositionCode());
                        }
                    }
                }
                if (ObjectUtils.isNotEmptyList(orderGradeList)) {
                    for (OrderGrade orderGrade : orderGradeList) {
                        if (craft.getFabricScore() >= orderGrade.getMinValue() && craft.getFabricScore() <= orderGrade.getMaxValue()) {
                            craft.setOrderGrade(Integer.valueOf(orderGrade.getOrderCode()));
                            break;
                        }
                    }
                }
                if (StringUtils.isNotBlank(craft.getFabricScorePlanCode())) {
                    try {
                        List<FabricScore> fabricScores = fabricScoreService.getFabricScoreByPlanCode(craft.getFabricScorePlanCode());
                        if (ObjectUtils.isNotEmptyList(fabricScores)) {
                            for (FabricScore fabricScore : fabricScores) {
                                if (custom.getFabricsScore() >= fabricScore.getMinValue() && custom.getFabricsScore() <= fabricScore.getMaxValue()) {
                                    craft.setFabricGrade(fabricScore.getFabricGradeCode());
                                    craft.setFabricTimeCoefficient(fabricGradeService.fabricTimeConfficientByCode(fabricScore.getFabricGradeCode()));
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
            customStylePartCraftList = buildStyleCraftData(custom, customStylePartList, customStylePartCraftList);


            customStylePartCraftList = calculateCost(null, mainFabric, customStylePartCraftList, motionList, craftPositionList);
            BigDecimal totalStandardTime = BigDecimal.ZERO.setScale(3);
            BigDecimal totalStandardPrice = BigDecimal.ZERO.setScale(3);
            for (CustomStylePartCraft craft : customStylePartCraftList) {
                totalStandardTime = totalStandardTime.add(craft.getStandardTime()).setScale(3, BigDecimal.ROUND_HALF_UP);
                totalStandardPrice = totalStandardPrice.add(craft.getStandardPrice()).setScale(3, BigDecimal.ROUND_HALF_UP);
            }
            custom.setStanderTime(totalStandardTime);
            custom.setTotalPrice(totalStandardPrice);
            customStylePartCraftList = customStylePartCraftList.stream().collect(Collectors.collectingAndThen(
                    Collectors.toCollection(() -> new TreeSet<>(Comparator.
                            comparing(x -> x.getCraftCode() + "#" + x.getCraftFlowNum()))), ArrayList::new));
            for (CustomStylePart part : customStylePartList) {
                if (StringUtils.isNotBlank(patternSummertyName)) {
                    part.setPatternSymmetry(mainFabric.getPatternSymmetry());
                    part.setPatternSymmetryName(patternSummertyName);
                }
            }
            List<CustomStyleCraftCourse> courses = customStyleCraftCourseDao.getDataByUniqueKey(custom.getOrderNo(), custom.getOrderLineId(), custom.getReleaseVersion());
            if (courses != null && courses.size() > 0) {
                for (CustomStyleCraftCourse vo : courses) {
                    try {
                        customStyleCraftCourseDao.deleteById(vo.getId());
                        TimeUnit.MILLISECONDS.sleep(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            //定制订单---判断工序流,是否重复或者为空
            boolean hasTQTWJ001 = false;
            Map<String, Integer> flowMap = new HashMap<>(0);
            Map<String, String> nullFlowMap = new HashMap<>(0);
            if (null != customStylePartCraftList && customStylePartCraftList.size() > 0) {
                for (CustomStylePartCraft vo : customStylePartCraftList) {
                    if ("TQTWJ001".equals(vo.getCraftCode())) {
                        hasTQTWJ001 = true;
                    }
                    //统计每一个工序流的个数，
                    String flow = vo.getCraftFlowNum();
                    if (StringUtils.isEmpty(flow)) {
                        nullFlowMap.put(vo.getCraftCode(), vo.getCraftCode());
                    }
                    if (flowMap.containsKey(flow)) {
                        flowMap.put(flow, flowMap.get(flow) + 1);
                    } else {
                        flowMap.put(flow, 1);
                    }
                }
            }
            //是否有工序流重复，重复就是大于1
            boolean hasSameFlow = false;
            StringBuffer buf = new StringBuffer();
            Iterator<String> it = flowMap.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                if (flowMap.get(key) > 1) {
                    hasSameFlow = true;
                    buf.append(key + ";");
                }
            }
            if (hasSameFlow || nullFlowMap.size() > 0) {
                String statusName = "";
                if (hasSameFlow) {
                    statusName = buf.toString() + OderProcessingStatusConstants.CusOrderStatusName.CUS_ORDER_NAME_1240;
                }
                if (nullFlowMap.size() > 0) {
                    StringBuffer buf1 = new StringBuffer();
                    Iterator<String> it1 = nullFlowMap.keySet().iterator();
                    while (it1.hasNext()) {
                        buf1.append(it1.next() + ",");
                    }
                    statusName = statusName + " " + buf1.toString() + "工序流为空!请处理!";
                }
                try {

                    orderProcessingStatusService.addOrUpdate(piCustomOrder.getOrderId() + "-" + piCustomOrder.getOrderLineId(), statusName,
                            OderProcessingStatusConstants.CusOrderStatus.CUS_ORDER_1240, "", "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                resultMap.put(ORDER_EXCEPTION_FLAG, Boolean.TRUE);
                custom.setStatus(BusinessConstants.Status.ORDER_EXCEPTION);
            }
            if (!hasTQTWJ001) {
                try {
                    orderProcessingStatusService.addOrUpdate(piCustomOrder.getOrderId() + "-" + piCustomOrder.getOrderLineId(), OderProcessingStatusConstants.CusOrderStatusName.CUS_ORDER_NAME_1230,
                            OderProcessingStatusConstants.CusOrderStatus.CUS_ORDER_1230, "", "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                resultMap.put(ORDER_EXCEPTION_FLAG, Boolean.TRUE);
                custom.setStatus(BusinessConstants.Status.ORDER_EXCEPTION);
            }

            reqPet = save(custom);
            if (Boolean.FALSE.equals(resultMap.get(ORDER_EXCEPTION_FLAG))) {
                try {
                    orderProcessingStatusService.addOrUpdate(piCustomOrder.getOrderId() + "-" + piCustomOrder.getOrderLineId(), OderProcessingStatusConstants.CusOrderStatusName.CUS_ORDER_NAME_1220,
                            OderProcessingStatusConstants.CusOrderStatus.CUS_ORDER_1220, "", "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (reqPet) {
                try {
                    stylePartCraftService.saveBatch(customStylePartCraftList);
                    reqPet = Boolean.TRUE;
                } catch (Exception e) {
                    log.error(e.getMessage());
                    reqPet = Boolean.FALSE;
                }
                if (null != motionList && motionList.size() > 0) {
                    int length = motionList.size();
                    List<CustomStylePartCraftMotion> tempList = new ArrayList<>();
                    for (int i = 0; i < length; i++) {
                        tempList.add(motionList.get(i));
                        if (((i + 1) % 100 == 0) || (i == length - 1)) {
                            try {
                                //stylePartCraftMotionService.saveBatch(tempList);
                                reqPet = Boolean.TRUE;
                            } catch (Exception e) {
                                reqPet = Boolean.FALSE;
                            }
                            tempList = new ArrayList<>(0);
                        }
                    }
                }
                try {
                    stylePartCraftPositionService.saveBatch(craftPositionList);
                    stylePartService.saveBatch(customStylePartList);
                    styleRuleService.saveBatch(styleRuleList);
                    reqPet = Boolean.TRUE;
                } catch (Exception e) {
                    log.error(e.getMessage());
                    reqPet = Boolean.FALSE;
                }
            }

        } catch (Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            ex.printStackTrace();
            reqPet = false;
        }
        return reqPet;
    }

    public List<CustomStylePartCraft> calculateCost(String factoryCode, PICustomOrderPartMaterial mainFabric,
                                                    List<CustomStylePartCraft> customStylePartCraftList,
                                                    List<CustomStylePartCraftMotion> motionList,
                                                    List<CustomStyleSewPosition> craftPositionList) {
        if (ObjectUtils.isNotEmptyList(craftPositionList)) {
            for (CustomStylePartCraft craft : customStylePartCraftList) {

                double totalArtificialTMU = 0.00;
                double totalMachineTMU = 0.00;
                for (CustomStylePartCraftMotion motion : motionList) {
                    if (ObjectUtils.isNotEmpt(motion)) {
                        if (motion.getPartCraftRandomCode() != null && craft.getRandomCode() != null &&
                                motion.getPartCraftRandomCode().equals(craft.getRandomCode())) {
                            totalArtificialTMU += motion.getManualTime();
                            totalMachineTMU += motion.getMachineTime();
                        }
                    }
                }
                CraftGradeEquipment equipment = gradeEquipmentService.getCraftGradeEquipment(factoryCode, craft.getCraftGradeCode());
                Boolean rept = false;
                for (CustomStyleSewPosition hems : craftPositionList) {
                    if (StringUtils.isNotBlank(craft.getPartPositionCode()) &&
                            hems.getSewPositionCode().equalsIgnoreCase(craft.getPartPositionCode())) {
                        //计算参数系数公式 参数系数=(浮动时间*CAD缝边位长度+固定时间*参数长度)/[(浮动时间+固定时间)*参数长度]
                        double coefficient = 0.00;
                        Integer floatTime = craft.getFloatingTime();
                        if (null == floatTime) {
                            floatTime = 0;
                        }
                        Integer fixedTime = craft.getFixedTime();
                        if (null == fixedTime) {
                            fixedTime = 0;
                        }
                        Integer paramLength = craft.getParamLength();
                        if (null == paramLength) {
                            paramLength = 0;
                        }
                        BigDecimal sewPositionValue = hems.getSewPositionValue();
                        if (null == sewPositionValue) {
                            sewPositionValue = BigDecimal.ZERO;
                        }
                        coefficient = (floatTime * sewPositionValue.doubleValue() + fixedTime * paramLength);
                        double tempCofficent = (floatTime + fixedTime) * paramLength;
                        if (tempCofficent > 0.00) {
                            coefficient /= tempCofficent;
                        }
                        Machine machine = machineService.getMachindCodeData(craft.getMachineCode(), craft.getWorkTypeCode());
                        totalArtificialTMU *= coefficient;
                        totalMachineTMU *= coefficient;
                        //参数化标准时间
                        //标准时间计算公式 (机器TMU(1+机器浮余+人工浮余)/1667+（人工TMU*（1+人工浮余））/2000) *(1+宽放系数)+捆扎时间/2000
                        double standardTime = (totalMachineTMU * (1 + machine.getMachineFloatover() + machine.getManualFloatover()) / 1667 +
                                (totalArtificialTMU * (1 + machine.getManualFloatover())) / 2000) *
                                (1 + craft.getAllowanceCode().doubleValue()) + craft.getStrappingCode().doubleValue() / 2000;
                        //参数化标准单价
                        double standardPrice = standardTime * equipment.getMinuteWage().doubleValue();

                        if (craft.getIsFabricStyleFix() && StringUtils.isNotBlank(mainFabric.getPatternSymmetry())) {
                            BigDecimal sewingRatio = patternSymmetryService.getPatternSymmetrysCode(mainFabric.getPatternSymmetry());
                            if (sewingRatio == null) {
                                sewingRatio = BigDecimal.ZERO.setScale(3);
                            }
                            double countTime = standardTime * (1 + sewingRatio.doubleValue() + craft.getFabricTimeCoefficient().doubleValue() + 0.00);
                            double countPrice = standardPrice * (1 + sewingRatio.doubleValue() + craft.getFabricTimeCoefficient().doubleValue() + 0.00);
                            craft.setStandardTime(new BigDecimal(countTime).setScale(3, RoundingMode.HALF_UP));
                            craft.setStandardPrice(new BigDecimal(countPrice).setScale(3, RoundingMode.HALF_UP));
                        } else {
                            double countTime = standardTime * (1 + craft.getFabricTimeCoefficient().doubleValue() + 0.00);
                            double countPrice = standardPrice * (1 + craft.getFabricTimeCoefficient().doubleValue() + 0.00);
                            craft.setStandardTime(new BigDecimal(countTime).setScale(3, RoundingMode.HALF_UP));
                            craft.setStandardPrice(new BigDecimal(countPrice).setScale(3, RoundingMode.HALF_UP));
                        }
                        rept = true;
                        break;
                    }
                }
                if (!rept) {
                    if (craft.getIsFabricStyleFix() && StringUtils.isNotBlank(mainFabric.getPatternSymmetry())) {
                        BigDecimal sewingRatio = patternSymmetryService.getPatternSymmetrysCode(mainFabric.getPatternSymmetry());
                        if (sewingRatio == null) {
                            sewingRatio = BigDecimal.ZERO.setScale(3);
                        }
                        double countTime = craft.getStandardTime().doubleValue() * (1 + sewingRatio.doubleValue() + craft.getFabricTimeCoefficient().doubleValue() + 0.00);
                        double standardPrice = countTime * equipment.getMinuteWage().doubleValue();
                        double countPrice = standardPrice * (1 + sewingRatio.doubleValue() + craft.getFabricTimeCoefficient().doubleValue() + 0.00);
                        craft.setStandardTime(new BigDecimal(countTime).setScale(3, RoundingMode.HALF_UP));
                        craft.setStandardPrice(new BigDecimal(countPrice).setScale(3, RoundingMode.HALF_UP));
                    } else {
                        double countTime = craft.getStandardTime().doubleValue() * (1 + craft.getFabricTimeCoefficient().doubleValue() + 0.00);
                        double standardPrice = countTime * equipment.getMinuteWage().doubleValue();
                        double countPrice = standardPrice * (1 + craft.getFabricTimeCoefficient().doubleValue() + 0.00);
                        craft.setStandardTime(new BigDecimal(countTime).setScale(3, RoundingMode.HALF_UP));
                        craft.setStandardPrice(new BigDecimal(countPrice).setScale(3, RoundingMode.HALF_UP));
                    }
                }
                log.info("当前工序:" + craft.getCraftCode() + "::::标准时间:" + craft.getStandardTime());
                log.info("当前工序:" + craft.getCraftCode() + "::::标准单价:" + craft.getStandardPrice());
            }
        }
        return customStylePartCraftList;
    }

    private List<CustomStyleRule> buileStyleRuleList(List<CustomStyleRule> styleRuleList) {
        List<CustomStyleRule> newRuleList = new ArrayList<>();
        Map<Integer, List<CustomStyleRule>> groupByRuleList = styleRuleList.stream().collect(Collectors.groupingBy(CustomStyleRule::getNember));
        TreeMap<Integer, List<CustomStyleRule>> newGroupByRuleList = new TreeMap<>(groupByRuleList);
        int numBer = 0;
        for (Integer key : newGroupByRuleList.keySet()) {
            List<CustomStyleRule> ruleList = newGroupByRuleList.get(key);
            Map<Integer, List<CustomStyleRule>> groupByPartCraftRule = ruleList.stream().collect(Collectors.groupingBy(CustomStyleRule::getProcessType));
            List<CustomStyleRule> cutCraftRules = MapUtils.getList(groupByPartCraftRule, RuleProcessType.PROCESS_TYPE_CUT_BACK);
            if (ObjectUtils.isNotEmptyList(cutCraftRules)) {
                for (CustomStyleRule cutCraftRule : cutCraftRules) {
                    numBer++;
                    cutCraftRule.setProcessNo(numBer);
                    newRuleList.add(cutCraftRule);
                }
            }
            List<CustomStyleRule> addCraftRules = MapUtils.getList(groupByPartCraftRule, RuleProcessType.PROCESS_TYPE_ADD);
            if (ObjectUtils.isNotEmptyList(addCraftRules)) {
                for (CustomStyleRule addRule : addCraftRules) {
                    numBer++;
                    addRule.setProcessNo(numBer);
                    newRuleList.add(addRule);
                }
            }
            List<CustomStyleRule> replaceCraftRules = MapUtils.getList(groupByPartCraftRule, RuleProcessType.PROCESS_TYPE_REPLACE);
            if (ObjectUtils.isNotEmptyList(replaceCraftRules)) {
                for (CustomStyleRule replaceRule : replaceCraftRules) {
                    numBer++;
                    replaceRule.setProcessNo(numBer);
                    newRuleList.add(replaceRule);
                }
            }
        }
        if (ObjectUtils.isEmptyList(newRuleList)) {
            newRuleList = styleRuleList;
        }
        return newRuleList;
    }

    /**
     * 根据设计部件编码和工序的设计部件编码对比添加上级部件random重新组装工序数据
     *
     * @param custom
     * @param customStylePartList
     * @param customStylePartCraftList
     * @return
     */
    private List<CustomStylePartCraft> buildStyleCraftData(CustomStyleCraftCourse custom,
                                                           List<CustomStylePart> customStylePartList,
                                                           List<CustomStylePartCraft> customStylePartCraftList) {
        for (CustomStylePart part : customStylePartList) {
            for (CustomStylePartCraft craft : customStylePartCraftList) {
                if (part.getDesignPartCode().equalsIgnoreCase(craft.getDesignPartCode())) {
                    craft.setDesignPartCode(part.getDesignPartCode());
                    craft.setDesignPartName(part.getDesignPartName());
                    craft.setStylePartRandomCode(part.getRandomCode());
                }
            }
        }
        CustomStylePart part = new CustomStylePart();
        part.setDesignPartName("其它部件");
        part.setDesignPartCode("otherParts");
        part.setRandomCode(SnowflakeIdUtil.generateId());
        part.setCustomMainRandomCode(custom.getRandomCode());
        part.setStatus(custom.getStatus());
        part.setCreateUser("plm");
        for (CustomStylePartCraft craft : customStylePartCraftList) {
            if (craft.getStylePartRandomCode() == null) {
                craft.setStylePartRandomCode(part.getRandomCode());
                craft.setDesignPartCode(part.getDesignPartCode());
                craft.setDesignPartName(part.getDesignPartName());
            }
        }
        customStylePartList.add(part);
        return customStylePartCraftList;
    }

    private boolean useList(List<ThinkStyleCraft> arr, String targetValue) {
        for (ThinkStyleCraft craft : arr) {
            if (targetValue.equalsIgnoreCase(craft.getCraftCode())) {
                return true;
            }
        }
        return false;
    }

    private boolean useList(String[] arr, String targetValue) {
        return Arrays.asList(arr).contains(targetValue);
    }

    private String[] saveDynamicArrays(List<ThinkStyleCraft> craftList) {
        String[] codeArray = new String[craftList.size()];
        for (int i = 0; i < craftList.size(); i++) {
            ThinkStyleCraft craft = craftList.get(i);
            codeArray[i] = craft.getCraftCode();
        }
        return codeArray;
    }


    public List<PartCraftRule> getPartCraftRuleList(List<ThinkStyleProcessRule> processRules) {
        List<PartCraftRule> rules = new ArrayList<>();
        for (ThinkStyleProcessRule rule : processRules) {
            PartCraftRule craftRule = new PartCraftRule();
            craftRule.setSourceCraftCode(StringUtils.isNotBlank(rule.getSourceCraftCode()) ? rule.getSourceCraftCode() : null);
            craftRule.setSourceCraftName(rule.getSourceCraftName());
            craftRule.setProcessType(Integer.valueOf(rule.getProcessType()));
            craftRule.setActionCraftCode(StringUtils.isNotBlank(rule.getActionCraftCode()) ? rule.getActionCraftCode() : null);
            craftRule.setActionCraftName(rule.getActionCraftName());
            rules.add(craftRule);
        }
        return rules;
    }


    /**
     * 根据部件数量进行组合
     *
     * @param array
     * @param except
     * @return
     */
    public String[] permutation(String[] array, int except) {
        List<String> res = new ArrayList<>();
        if (except == 1) {
            for (int i = 0; i < array.length; i++) {
                String codes = array[i];
                res.add(codes);
            }
        } else {
            for (int i = 0; i < array.length; i++) {
                for (int j = i + 1; j < array.length - 1; j++) {
                    if (except == 3) {
                        for (int k = j + 1; k < array.length; k++) {
                            String codes = array[i] + "_" + array[j] + "_" + array[k];
                            res.add(codes);
                        }
                    } else {
                        String codes = array[i] + "_" + array[j];
                        res.add(codes);
                    }
                }
            }
        }

        String[] resultArray = new String[res.size()];
        for (int i = 0; i < res.size(); i++) {
            resultArray[i] = res.get(i);
        }
        return resultArray;
    }


    @Override
    public List<CustomStyleCraftCourse> getOrderCustmStyleBaseList(String orderNo, String orderLineId) {
        return customStyleCraftCourseDao.getOrderCustmStyleBaseList(orderNo, orderLineId);
    }

    @Override
    public CustomStyleCraftCourse getCustomStyleCraftCourse(String orderNo, String orderLineNo) {
        QueryWrapper<CustomStyleCraftCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(CustomStyleCraftCourse::getOrderNo, orderNo);
        queryWrapper.lambda().eq(CustomStyleCraftCourse::getOrderLineId, orderLineNo);
        CustomStyleCraftCourse cuso = null;
        try {
            cuso = customStyleCraftCourseDao.selectOne(queryWrapper);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cuso;
    }


    @Override
    public Integer getCustomOrderLine(String orderNo, String orderLineId) {
        return customStyleCraftCourseDao.getCustomOrderLine(orderNo, orderLineId);
    }

    @Override
    public CustomStyleCraftCourse getOrderCustomStyleByVersion(String orderNo, String orderLineId, String releaseVersion) {
        CustomStyleCraftCourse course = null;
        try {
            course = customStyleCraftCourseDao.getOrderCustomStyleByVersion(orderNo, orderLineId, releaseVersion);
        } catch (Exception x) {
            x.printStackTrace();
        }
        return course;
    }

    @Override
    public CustomStyleCraftCourse getOrderCustomStyleByRandomCode(Long randomCode) {
        Map<String, CraftCategory> map = craftCategoryDao.getForMap();
        CustomStyleCraftCourse course = customStyleCraftCourseDao.getOrderCustomStyleByRandomCode(randomCode);
        if (null != map && null != course && map.containsKey(course.getCraftCategoryCode())) {
            course.setCraftCategoryName(map.get(course.getCraftCategoryCode()).getCraftCategoryName());
        }
        return course;
    }

    @Override
    public Boolean changeCustomStyleOrderInfo(CustomStyleCraftCourse course, Integer status) {
        List<CustomStyleRule> customStyleRuleList = course.getStyleRuleList();
        List<CustomStyleSewPosition> customStyleSewPositionList = course.getSewPositionList();
        List<CustomStylePart> customStylePartList = course.getCustomPartList();
        List<CustomStylePartCraft> verifyPartCraftList = new ArrayList<>();
        List<CustomStylePartCraftMotion> verifyPartCraftMotionList = new ArrayList<>();

        customStyleSewPositionList.forEach(customStyleSewPosition -> {
            customStyleSewPosition.setId(null);
            customStyleSewPosition.setStatus(course.getStatus());
            customStyleSewPosition.setRandomCode(SnowflakeIdUtil.generateId());
            customStyleSewPosition.setCreateUser(course.getCreateUser());
            customStyleSewPosition.setCustomMainRandomCode(course.getRandomCode());
        });
        if (null != customStyleRuleList && customStyleRuleList.size() > 0) {
            customStyleRuleList.forEach(r -> {
                r.setStatus(course.getStatus());
                r.setRandomCode(SnowflakeIdUtil.generateId());
                r.setCreateUser(course.getCreateUser());
                r.setCustomMainRandomCode(course.getRandomCode());
            });
        }

        for (CustomStylePart part : customStylePartList) {
            part.setId(null);
            part.setRandomCode(SnowflakeIdUtil.generateId());
            part.setStatus(course.getStatus());
            part.setUpdateUser(course.getUpdateUser());
            if (status.equals(BusinessConstants.Status.PUBLISHED_STATUS) || status.equals(BusinessConstants.Status.NOT_ACTIVE_STATUS)) {
                part.setAuditUser(course.getUpdateUser());
                part.setAuditTime(new Date());
            }

            part.setCustomMainRandomCode(course.getRandomCode());
            if (ObjectUtils.isEmptyList(part.getPartCraftList())) continue;
            for (CustomStylePartCraft craft : part.getPartCraftList()) {
                craft.setId(null);
                craft.setStatus(course.getStatus());
                craft.setUpdateUser(course.getUpdateUser());
                if (status.equals(BusinessConstants.Status.PUBLISHED_STATUS) || status.equals(BusinessConstants.Status.NOT_ACTIVE_STATUS)) {
                    craft.setAuditUser(course.getUpdateUser());
                    craft.setAuditTime(new Date());
                }
                craft.setStylePartRandomCode(part.getRandomCode());
                craft.setRandomCode(SnowflakeIdUtil.generateId());
                if (StringUtils.isBlank(craft.getCraftFlowNum())) {
                    craft.setCraftFlowNum("");
                }
                if (ObjectUtils.isEmptyList(craft.getStylePartCraftMotionList())) continue;
                for (CustomStylePartCraftMotion motion : craft.getStylePartCraftMotionList()) {
                    motion.setId(null);
                    motion.setRandomCode(SnowflakeIdUtil.generateId());
                    motion.setStatus(course.getStatus());
                    motion.setUpdateUser(course.getUpdateUser());
                    motion.setPartCraftRandomCode(craft.getRandomCode());
                }
                verifyPartCraftMotionList.addAll(craft.getStylePartCraftMotionList());
            }
            verifyPartCraftList.addAll(part.getPartCraftList());

        }
        boolean savePet = false;
        if (ObjectUtils.isNotEmptyList(customStylePartList) &&
                ObjectUtils.isNotEmptyList(customStyleSewPositionList) && ObjectUtils.isNotEmptyList(verifyPartCraftList) &&
                ObjectUtils.isNotEmptyList(verifyPartCraftMotionList)) {
            if (course.getId() == null) {
                savePet = save(course);
            } else {
                int i = customStyleCraftCourseDao.updateCustomMaster(course);
                if (i > 0) {
                    savePet = true;
                }
            }
            try {
                stylePartService.saveOrUpdateBatch(customStylePartList);
                if (ObjectUtils.isNotEmptyList(customStyleRuleList)) {
                    styleRuleService.saveOrUpdateBatch(customStyleRuleList);
                }
                stylePartCraftPositionService.saveOrUpdateBatch(customStyleSewPositionList);
                stylePartCraftService.saveOrUpdateBatch(verifyPartCraftList);
                //stylePartCraftMotionService.saveOrUpdateBatch(verifyPartCraftMotionList);
                savePet = Boolean.TRUE;
            } catch (Exception e) {
                savePet = Boolean.FALSE;
            }
        }

        if (savePet) {
            if (course.getStatus().equals(BusinessConstants.Status.PUBLISHED_STATUS)) {
                if (course.getReleaseTime() == null) {
                    course.setReleaseTime(new Date());
                }
                CountDownLatch countDownLatch = new CountDownLatch(1);
                try {
                    threadPoolTaskExecutor.submit(() -> {
                        releaseCustomStyle(course);
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
                try {
                    countDownLatch.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

        return savePet;
    }

    @Override
    public Boolean releaseCustomStyle(CustomStyleCraftCourse craftCourse) {
        List<CustomStyleCraftCourse> courseList = getOrderCustmStyleBaseList(craftCourse.getOrderNo(), craftCourse.getOrderLineId());
        List<CustomStyleSewPosition> changeSewPositionList = new ArrayList<>();
        List<CustomStyleRule> changeRuleList = new ArrayList<>();
        List<CustomStylePart> changePartList = new ArrayList<>();
        List<CustomStylePartCraft> verifyPartCraftList = new ArrayList<>();
        List<CustomStylePartCraftMotion> verifyPartCraftMotionList = new ArrayList<>();

        if (ObjectUtils.isNotEmptyList(courseList)) {
            for (CustomStyleCraftCourse course : courseList) {
                if (course.getReleaseVersion().equalsIgnoreCase(craftCourse.getReleaseVersion())) {
                    course.setEffectiveTime(new Date());
                    course.setStatus(BusinessConstants.Status.PUBLISHED_STATUS);
                    course.setReleaseTime(craftCourse.getReleaseTime());
                    course.setReleaseUser(craftCourse.getUpdateUser());
                } else {
                    course.setExpirationTime(new Date());
                    course.setStatus(BusinessConstants.Status.INVALID_STATUS);
                }
                List<CustomStyleRule> customStyleRuleList = styleRuleService.getStyleMainRandomRuleList(course.getRandomCode());
                if (ObjectUtils.isNotEmptyList(customStyleRuleList)) {
                    for (CustomStyleRule rule : customStyleRuleList) {
                        rule.setStatus(course.getStatus());
                        changeRuleList.addAll(customStyleRuleList);
                    }
                }

                List<CustomStyleSewPosition> customStyleSewPositionList = stylePartCraftPositionService.getPartRandomCodeSewPositionList(course.getRandomCode());
                if (ObjectUtils.isNotEmptyList(customStyleSewPositionList)) {
                    for (CustomStyleSewPosition p : customStyleSewPositionList) {
                        p.setStatus(course.getStatus());
                    }
                    changeSewPositionList.addAll(customStyleSewPositionList);
                }

                List<CustomStylePart> customStylePartList = stylePartService.getMainRandomCodePartList(course.getRandomCode());
                if (ObjectUtils.isNotEmptyList(customStylePartList)) {
                    for (CustomStylePart part : customStylePartList) {
                        part.setStatus(course.getStatus());
                        part.setUpdateUser(course.getUpdateUser());
                        List<CustomStylePartCraft> craftList = stylePartCraftService.getPartRandomCodeCraftList(part.getRandomCode());
                        if (ObjectUtils.isNotEmptyList(craftList)) {
                            for (CustomStylePartCraft craft : craftList) {
                                craft.setStatus(course.getStatus());
                                List<CustomStylePartCraftMotion> motionList = stylePartCraftMotionService.getCraftRandomCodeMotionList(craft.getRandomCode());
                                if (ObjectUtils.isNotEmptyList(motionList)) {
                                    for (CustomStylePartCraftMotion motion : motionList) {
                                        motion.setStatus(course.getStatus());
                                        motion.setUpdateUser(course.getUpdateUser());
                                    }
                                    verifyPartCraftMotionList.addAll(motionList);
                                }
                            }
                            verifyPartCraftList.addAll(craftList);
                        }
                    }
                    changePartList.addAll(customStylePartList);
                }
            }
        }

        boolean savePet = false;
        savePet = saveOrUpdateBatch(courseList);
        if (savePet) {
            if (ObjectUtils.isNotEmptyList(changePartList)) {
                savePet = stylePartService.saveOrUpdateBatch(changePartList);
            }
            if (ObjectUtils.isNotEmptyList(changeRuleList)) {
                savePet = styleRuleService.saveOrUpdateBatch(changeRuleList);
            }
            if (ObjectUtils.isNotEmptyList(changeSewPositionList)) {
                savePet = stylePartCraftPositionService.saveOrUpdateBatch(changeSewPositionList);
            }
            if (ObjectUtils.isNotEmptyList(verifyPartCraftList)) {
                savePet = stylePartCraftService.saveOrUpdateBatch(verifyPartCraftList);
            }
            if (ObjectUtils.isNotEmptyList(verifyPartCraftMotionList)) {
                savePet = stylePartCraftMotionService.saveOrUpdateBatch(verifyPartCraftMotionList);
            }
        }
        return savePet;
    }

    @Override
    public List<CustomStyleCraftCourse> queryCustomOrderForTheCurrentDateList() {
        List<CustomStyleCraftCourse> list = new ArrayList<>();
        try {
            list = customStyleCraftCourseDao.queryCustomOrderForTheCurrentDateList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public int deleteByRandomList(List<Long> randomCodeList) {
        return customStyleCraftCourseDao.deleteByRandomList(randomCodeList);
    }

    public Boolean removeAllCustomStyleDetailData(Long randomCode) {
        Boolean rept = false;
        int i = 0;
        List<CustomStylePart> partList = stylePartService.getMainRandomCodePartList(randomCode);
        if (ObjectUtils.isNotEmptyList(partList)) {
            List<Long> partRandomCodes = new ArrayList<>();
            partList.forEach(p -> partRandomCodes.add(p.getRandomCode()));
            List<CustomStylePartCraft> craftList = stylePartCraftService.getPartRandomCodeCraftList(partRandomCodes);
            if (ObjectUtils.isNotEmptyList(craftList)) {
                List<Long> craftRandomCodes = new ArrayList<>();
                craftList.forEach(c -> craftRandomCodes.add(c.getRandomCode()));
                i = stylePartCraftMotionService.deleteCustomStyleMotionList(craftRandomCodes);

            }
            i = stylePartCraftService.deleteCustomStyleCraftList(partRandomCodes);
            i = stylePartService.deleteCustomStylePart(randomCode);
        }
        i = styleRuleService.deleteCustomStyleRule(randomCode);
        i = stylePartCraftPositionService.deleteCustomStylePosition(randomCode);
        if (i > 0) {
            rept = true;
        }
        return rept;

    }

    @Override
    public void createCustomStyleByBigStyle(String orderId, String orderLineId) {

        PICustomOrder customOrder = piCustomOrderService.getOrderId(orderId, orderLineId);
        if (StringUtils.isBlank(customOrder.getOrderType()) || !customOrder.getOrderType().equals("20")) {
            return;
        }
        PICustomOrderPartMaterial mainFabric = piCustomOrderPartMaterialService.getMainMaterialData(customOrder.getOrderId(),
                customOrder.getOrderLineId(), customOrder.getMainMaterialCode());

        String styleSKCCode = customOrder.getStyleSKCCode();
        BigStyleAnalyseMaster bigStyle = bigStyleAnalyseMasterDao.getBigStyleAnalyseByStyleSKCCode(styleSKCCode);

        QueryWrapper<ThinkStyleWarehouse> thinkStyleQry = new QueryWrapper<>();
        thinkStyleQry.lambda().eq(ThinkStyleWarehouse::getStyleCode, customOrder.getStyleCode())
                .ne(ThinkStyleWarehouse::getStatus, 1090);
        ThinkStyleWarehouse thinkStyle = thinkStyleWarehouseService.getOne(thinkStyleQry, false);


        QueryWrapper<CustomStyleCraftCourse> customStyleQry = new QueryWrapper<>();
        customStyleQry.lambda().eq(CustomStyleCraftCourse::getOrderNo, customOrder.getOrderId())
                .eq(CustomStyleCraftCourse::getOrderLineId, customOrder.getOrderLineId());
        CustomStyleCraftCourse customStyle = getOne(customStyleQry, false);
        boolean isCreate = false;
        if (customStyle == null) {
            customStyle = new CustomStyleCraftCourse();
            customStyle.setRandomCode(SnowflakeIdUtil.generateId());
            customStyle.setCreateTime(new Date());
            customStyle.setCreateUser("PLM");
            customStyle.setReleaseVersion("1.0");
            customStyle.setRemark("靠码定制自动生成");
            isCreate = true;
        }

        if (bigStyle == null || bigStyle.getStatus() != BusinessConstants.Status.PUBLISHED_STATUS) {
            customStyle.setStatus(BusinessConstants.Status.ORDER_EXCEPTION);
        } else {
            customStyle.setStatus(BusinessConstants.Status.DRAFT_STATUS);
        }
        customStyle.setStyleCode(customOrder.getCustStyleCode());
        customStyle.setOrderNo(customOrder.getOrderId());
        customStyle.setOrderLineId(customOrder.getOrderLineId());
        customStyle.setStyleDescript(null);
        customStyle.setCustomerName(customOrder.getCustomerName());
        customStyle.setMainMaterialCode(customOrder.getMainMaterialCode());
        customStyle.setFabricDirection(customOrder.getFabricDirection());
        if (mainFabric != null) {
            customStyle.setFabricsScore(mainFabric.getMaterialGrade());
        }
        customStyle.setUpdateUser("PLM");
        customStyle.setUpdateTime(new Date());
        if (bigStyle != null) {
            customStyle.setFabricsScore(bigStyle.getFabricFraction());
            customStyle.setStanderTime(bigStyle.getStandardTime());
            customStyle.setTotalPrice(bigStyle.getStandardPrice());
            customStyle.setFactoryCode(bigStyle.getFactoryCode());
            if (StringUtils.isNotBlank(customStyle.getFactoryCode())) {
                List<FactoryVO> factorys = gradeEquipmentService.getAllFactory();
                for (FactoryVO itm : factorys) {
                    if (itm.equals(customStyle.getFactoryCode())) {
                        customStyle.setFactoryName(itm.getFactoryName());
                        break;
                    }
                }
            }
            customStyle.setProductionCategory(bigStyle.getProductionCategory());
            customStyle.setMainFrameCode(bigStyle.getMainFrameCode());
            customStyle.setMainFrameName(bigStyle.getMainFrameName());
            customStyle.setCraftCategoryCode(bigStyle.getCraftCategoryCode());
            customStyle.setCraftCategoryName(bigStyle.getCraftCategoryName());
            customStyle.setClothesCategoryCode(bigStyle.getClothesCategoryCode());
            customStyle.setClothesCategoryName(bigStyle.getClothesCategoryName());
        }
        if (thinkStyle != null) {
            customStyle.setThinkStyleName(thinkStyle.getStyleName());
            customStyle.setStylePicture(thinkStyle.getPictureUrl());
            customStyle.setBrand(dictionaryService.getBrandName(thinkStyle.getBrand()));
        }

        if (isCreate) {
            save(customStyle);
        } else {
            update(customStyle, customStyleQry);
            removeAllCustomStyleDetailData(customStyle.getRandomCode());
        }

        if (bigStyle == null) {
            return;
        }


        List<CustomStylePart> customParts = new ArrayList<>();
        List<CustomStylePartCraft> customCrafts = new ArrayList<>();
        List<CustomStylePartCraftMotion> customActions = new ArrayList<>();
        bigStylePartCraft2CustomStyle(bigStyle, customStyle, customParts, customCrafts, customActions);
        if (ObjectUtils.isNotEmptyList(customParts)) {
            stylePartService.saveBatch(customParts);
        }
        if (ObjectUtils.isNotEmptyList(customCrafts)) {
            stylePartCraftService.saveBatch(customCrafts);
        }
        if (ObjectUtils.isNotEmptyList(customActions)) {
            stylePartCraftMotionService.saveBatch(customActions);
        }


    }

    private void bigStylePartCraft2CustomStyle(BigStyleAnalyseMaster bigStyle,
                                               CustomStyleCraftCourse customStyle,
                                               List<CustomStylePart> customParts,
                                               List<CustomStylePartCraft> customCrafts,
                                               List<CustomStylePartCraftMotion> customActions) {
        //获取大货款部件工序
        List<BigStyleAnalysePartCraft> bigStylePartCrafts = bigStyleAnalysePartCraftDao.getPartAndDetailByStyleRandomCode(bigStyle.getRandomCode());
        //获取大货款动作
        List<SewingCraftAction> bigActions = styleSewingCraftActionDao.getDataBySewingCraftRandomCodeAndCraftCode(bigStyle.getRandomCode(), null);


        for (BigStyleAnalysePartCraft bigPart : bigStylePartCrafts) {
            CustomStylePart customPart = new CustomStylePart();
            customPart.setRandomCode(SnowflakeIdUtil.generateId());
            customPart.setDesignPartCode(bigPart.getPartCraftMainCode());
            customPart.setDesignPartName(bigPart.getPartCraftMainName());
            customPart.setIsVirtual(false);
            customPart.setPositionCode(null);
            customPart.setPositionName(null);
            customPart.setFabricStyle(null);
            customPart.setPartDescript(null);
            customPart.setCustomMainRandomCode(customStyle.getRandomCode());
            customPart.setRemark(bigPart.getRemark());
            customPart.setPatternSymmetry(bigPart.getPatternSymmetry());
            customPart.setPatternSymmetryName(null);
            customPart.setCreateUser("PLM");
            customPart.setCreateTime(new Date());
            customPart.setUpdateUser("PLM");
            customPart.setUpdateTime(new Date());
            customPart.setStatus(customStyle.getStatus());
            customParts.add(customPart);


            if (ObjectUtils.isNotEmptyList(bigPart.getPartCraftDetailList())) {
                List<BigStyleAnalysePartCraftDetail> bigCrafts = bigPart.getPartCraftDetailList();
                int n = 1;
                for (BigStyleAnalysePartCraftDetail bigCraft : bigCrafts) {
                    CustomStylePartCraft customCraft = new CustomStylePartCraft();
                    customCraft.setStylePartRandomCode(customPart.getRandomCode());
                    customCraft.setRandomCode(SnowflakeIdUtil.generateId());
                    customCraft.setCraftFlowNum(bigCraft.getCraftNo());
                    customCraft.setOrderNum(n++);
                    customCraft.setCraftCode(bigCraft.getCraftCode());
                    customCraft.setCraftName(bigCraft.getCraftName());
                    customCraft.setCraftDescript(bigCraft.getCraftRemark());
                    customCraft.setCraftCategoryCode(bigCraft.getCraftCategoryCode());
                    customCraft.setCraftCategoryName(bigCraft.getCraftCategoryName());
                    customCraft.setCraftPartName(bigCraft.getCraftPartName());
                    customCraft.setCraftPartCode(bigCraft.getCraftCode());
                    customCraft.setMakeTypeCode(null);
                    customCraft.setCraftGradeCode(bigCraft.getCraftGradeCode());
                    customCraft.setMachineCode(bigCraft.getMachineCode());
                    customCraft.setMachineName(bigCraft.getMachineName());
                    customCraft.setAllowanceRandomCode(null);
                    customCraft.setAllowanceCode(null);
                    customCraft.setStrappingCode(null);
                    customCraft.setIsFabricStyleFix(null);
                    customCraft.setFabricGrade(null);
                    customCraft.setStandardTime(bigCraft.getStandardTime());
                    customCraft.setStandardPrice(bigCraft.getStandardPrice());
                    customCraft.setFabricScorePlanCode(null);
                    customCraft.setSectionCode(null);
                    customCraft.setWorkTypeCode(null);
                    customCraft.setFabricTimeCoefficient(null);
                    customCraft.setTimeSupplement(null);
                    customCraft.setFabricScore(null);
                    customCraft.setOrderGrade(null);
                    customCraft.setMakeDescription(null);
                    customCraft.setQualitySpec(null);
                    customCraft.setFixedTime(null);
                    customCraft.setFloatingTime(null);
                    customCraft.setSewingLength(null);
                    customCraft.setParamLength(null);
                    customCraft.setSourceCraftName(null);
                    customCraft.setRemark(null);
                    customCraft.setDesignPartCode(null);
                    customCraft.setDesignPartName(null);
                    customCraft.setPartPositionCode(null);
                    customCraft.setPartPositionName(null);
                    customCraft.setStitchLength(null);
                    customCraft.setRpm(null);
                    customCraft.setCreateTime(new Date());
                    customCraft.setCreateUser("PLM");
                    customCraft.setStatus(customStyle.getStatus());
                    customCrafts.add(customCraft);


                    List<SewingCraftAction> myBigActions = bigActions.stream()
                            .filter(x -> x.getPartCraftMainCode().equals(bigPart.getPartCraftMainCode())
                                    && x.getCraftCode().equals(bigCraft.getCraftCode()))
                            .collect(Collectors.toList());
                    for (SewingCraftAction bigAction : myBigActions) {
                        CustomStylePartCraftMotion customAction = new CustomStylePartCraftMotion();
                        customAction.setRandomCode(SnowflakeIdUtil.generateId());
                        customAction.setOrderNum(bigAction.getOrderNum());
                        customAction.setMotionCode(bigAction.getMotionCode());
                        customAction.setMotionName(bigAction.getMotionName());
                        customAction.setFrequency(bigAction.getFrequency());
                        customAction.setDescription(bigAction.getDescription());
                        customAction.setSpeed(bigAction.getSpeed());
                        customAction.setMachineTime(bigAction.getMachineTime());
                        customAction.setManualTime(bigAction.getManualTime());
                        customAction.setPartCraftRandomCode(customCraft.getRandomCode());
                        customAction.setAuditDate(null);
                        customAction.setCreateTime(new Date());
                        customAction.setCreateUser("PLM");
                        customAction.setUpdateTime(new Date());
                        customAction.setUpdateUser("PLM");
                        customAction.setStatus(customStyle.getStatus());
                        customActions.add(customAction);
                    }
                }
            }
        }
    }

    public List<CraftVO> getDataForExcelReportByCustomRandomCode(Long randomCode) {
        return customStyleCraftCourseDao.getDataForExcelReportByCustomRandomCode(randomCode);
    }

    @Override
    public List<CraftVO> getDataForExcelReportOrderByCustomWorkOrder(Long randomCode) {
        return customStyleCraftCourseDao.getDataForExcelReportOrderByCustomWorkOrder(randomCode);
    }

    @Override
    public boolean isCustomOrderExist(String orderNo, String orderLineId, String releaseVersion) {
        return customStyleCraftCourseDao.isCustomOrderExist(orderNo, orderLineId, releaseVersion);
    }


}