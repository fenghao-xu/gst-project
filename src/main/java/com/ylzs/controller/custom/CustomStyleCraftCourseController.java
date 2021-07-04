package com.ylzs.controller.custom;

import com.alibaba.fastjson.JSONObject;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.constant.MessageConstant;
import com.ylzs.common.util.*;
import com.ylzs.common.util.pageHelp.PageUtils;
import com.ylzs.common.utils.MapUtils;
import com.ylzs.common.utils.object.ObjectUtils;
import com.ylzs.controller.auth.UserContext;
import com.ylzs.entity.craftmainframe.CraftMainFrame;
import com.ylzs.entity.craftmainframe.CraftMainFrameRoute;
import com.ylzs.entity.craftmainframe.FlowNumConfig;
import com.ylzs.entity.craftstd.CraftFile;
import com.ylzs.entity.craftstd.CraftStd;
import com.ylzs.entity.craftstd.CraftStdTool;
import com.ylzs.entity.custom.*;
import com.ylzs.entity.plm.FabricMainData;
import com.ylzs.entity.plm.PICustomOrder;
import com.ylzs.entity.plm.PICustomOrderPartMaterial;
import com.ylzs.entity.sewingcraft.SewingCraftAction;
import com.ylzs.entity.sewingcraft.SewingCraftPartPosition;
import com.ylzs.entity.sewingcraft.SewingCraftStd;
import com.ylzs.entity.sewingcraft.SewingCraftWarehouseWorkplace;
import com.ylzs.entity.timeparam.FabricScore;
import com.ylzs.entity.timeparam.OrderGrade;
import com.ylzs.service.bigstylecraft.StyleSewingCraftWarehouseService;
import com.ylzs.service.craftmainframe.ICraftMainFrameRouteService;
import com.ylzs.service.craftmainframe.ICraftMainFrameService;
import com.ylzs.service.craftmainframe.IProductionPartService;
import com.ylzs.service.craftstd.IDictionaryService;
import com.ylzs.service.craftstd.impl.CraftFileService;
import com.ylzs.service.craftstd.impl.CraftStdService;
import com.ylzs.service.craftstd.impl.CraftStdToolService;
import com.ylzs.service.custom.*;
import com.ylzs.service.interfaceOutput.ISectionSMVService;
import com.ylzs.service.plm.IFabricMainDataService;
import com.ylzs.service.plm.IPICustomOrderPartMaterialService;
import com.ylzs.service.plm.IPICustomOrderService;
import com.ylzs.service.sewingcraft.*;
import com.ylzs.service.staticdata.WorkTypeService;
import com.ylzs.service.timeparam.CraftGradeEquipmentService;
import com.ylzs.service.timeparam.FabricGradeService;
import com.ylzs.service.timeparam.FabricScoreService;
import com.ylzs.service.timeparam.IOrderGradeService;
import com.ylzs.vo.bigstylecraft.StyleCraftVO;
import com.ylzs.vo.bigstylereport.CraftVO;
import com.ylzs.vo.customStyle.CustomStyleMasterVo;
import com.ylzs.vo.sewing.VSewingCraftVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.ylzs.common.util.Assert.isFalse;


/**
 * 定制款工艺路线主数据表
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-03-18 11:05:41
 */
@Api(tags = "定制款工艺主数据层")
@RestController
@RequestMapping(value = "/customStyleCraftCourse")
public class CustomStyleCraftCourseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomStyleCraftCourseController.class);
    @Autowired
    private ICustomStyleCraftCourseService customStyleCraftCourseService;
    @Autowired
    private ICustomStylePartService customStylePartService;
    @Autowired
    private ICustomStylePartCraftService customStylePartCraftService;
    @Autowired
    private ICustomStylePartCraftMotionService customStylePartCraftMotionService;
    @Autowired
    private ICustomStyleRuleService customStyleRuleService;
    @Autowired
    private ICustomStyleSewPositionService customStyleSewPositionService;
    @Resource
    private ThreadPoolTaskExecutor threadPoolExecutor;
    @Autowired
    private IPICustomOrderPartMaterialService partMaterialService;
    @Autowired
    private CraftGradeEquipmentService gradeEquipmentService;
    @Autowired
    private SewingCraftWarehouseService sewingCraftWarehouseService;

    @Resource
    private StyleSewingCraftWarehouseService styleSewingCraftWarehouseService;

    @Autowired
    private IOrderGradeService orderGradeService;
    @Autowired
    private FabricScoreService fabricScoreService;
    @Autowired
    private FabricGradeService fabricGradeService;
    @Autowired
    private ICraftMainFrameService craftMainFrameService;
    @Autowired
    private IPICustomOrderService piCustomOrderService;

    @Resource
    private CraftStdService craftStdService;

    @Resource
    private WorkTypeService workTypeService;
    @Resource
    private CraftStdToolService craftStdToolService;

    @Resource
    private SewingCraftActionService sewingCraftActionService;

    @Resource
    private SewingCraftPartPositionService sewingCraftPartPositionService;

    @Resource
    private SewingCraftWarehouseWorkplaceService sewingCraftWarehouseWorkplaceService;

    @Resource
    private CraftFileService craftFileService;

    @Resource
    private SewingCraftStdService sewingCraftStdService;

    @Resource
    ThreadPoolTaskExecutor taskExecutor;

    @Resource
    ISectionSMVService sectionSMVService;

    @Resource
    IProductionPartService productionPartService;

    @Resource
    ICraftMainFrameRouteService craftMainFrameRouteService;

    @Resource
    IFabricMainDataService fabricMainDataService;

    @Resource
    IDictionaryService dictionaryService;

    @RequestMapping(value = "/getCustomStyleListAll", method = RequestMethod.POST)
    @ApiOperation(value = "/getCustomStyleListAll", httpMethod = "POST", notes = "查询定制订单工艺数据列表")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", dataType = "MessageParam", name = "data", value = "信息参数", required = true)})
    public Result<List<CustomStyleCraftCourse>> getCustomStyleListAll(HttpServletRequest request) throws Exception {

        String data = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        Map<String, String> requestParam = JSONObject.parseObject(data, Map.class);
        Map<String, String> parmMap = JSONObject.parseObject(MapUtils.getString(requestParam, "parments"), Map.class);

        Integer page = MapUtils.getInteger(requestParam, "page");
        Integer rows = MapUtils.getInteger(requestParam, "rows");
        HashMap params = parameterAssembly(parmMap);
        //PageHelper.startPage(page, rows);
        List<CustomStyleCraftCourse> styleCraftCourseList = customStyleCraftCourseService.searchStyleCraftCourseList(params);
        styleCraftCourseList.forEach(vo -> vo.setCustomOrderNo(vo.getOrderNo() + "-" + vo.getOrderLineId()));
        Long total = Long.valueOf(styleCraftCourseList.size());
        styleCraftCourseList = PageUtils.startPage(styleCraftCourseList, page, rows);
        return Result.ok(styleCraftCourseList, total);

    }

    @RequestMapping(value = "/searchCustomStyleCraftMasterAll", method = RequestMethod.POST)
    @ApiOperation(value = "/searchCustomStyleCraftMasterAll", httpMethod = "POST", notes = "查询定制订单工艺数据列表")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", dataType = "MessageParam", name = "data", value = "信息参数", required = true)})
    public Result<List<CustomStyleCraftCourse>> searchCustomStyleCraftMasterAll(HttpServletRequest request) throws Exception {

        String data = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        HashMap params = new HashMap();
        Integer page = 1;
        Integer rows = 15;
        if (StringUtils.isNotBlank(data)) {
            Map<String, String> requestParam = JSONObject.parseObject(data, Map.class);
            if (StringUtils.isNotBlank(MapUtils.getString(requestParam, "parments"))) {
                Map<String, String> parmMap = JSONObject.parseObject(MapUtils.getString(requestParam, "parments"), Map.class);
                params = parameterAssembly(parmMap);
            }
            page = MapUtils.getInteger(requestParam, "page") != null ? MapUtils.getInteger(requestParam, "page") : 1;
            rows = MapUtils.getInteger(requestParam, "rows") != null ? MapUtils.getInteger(requestParam, "rows") : 15;
        }


        List<CustomStyleCraftCourse> styleCraftCourseList = customStyleCraftCourseService.searchStyleCraftCourseList(params);
        styleCraftCourseList.forEach(vo -> vo.setCustomOrderNo(vo.getOrderNo() + "-" + vo.getOrderLineId()));
        Long total = Long.valueOf(styleCraftCourseList.size());
        styleCraftCourseList = PageUtils.startPage(styleCraftCourseList, page, rows);
        return Result.ok(styleCraftCourseList, total);
    }

    @RequestMapping(value = "/getOrderCustmStyleBaseList", method = RequestMethod.GET)
    @ApiOperation(value = "getOrderCustmStyleBaseList", httpMethod = "GET", notes = "根据订单号获取定制订单所有版本数据")
    public Result<List<CustomStyleCraftCourse>> getOrderCustmStyleBaseList(HttpServletRequest request,
                                                                           @RequestParam(name = "orderNo", required = true) String orderNo,
                                                                           @RequestParam(name = "orderLineId", required = true) String orderLineId) {
        List<CustomStyleCraftCourse> list = customStyleCraftCourseService.getOrderCustmStyleBaseList(orderNo, orderLineId);
        if (ObjectUtils.isNotEmptyList(list)) {
            list.forEach(x -> x.setCustomOrderNo(x.getOrderNo() + "-" + x.getOrderLineId()));
        }
        return Result.ok(list);
    }

    @RequestMapping(value = "/getCustomStyleDetailAll", method = RequestMethod.GET)
    @ApiOperation(value = "getCustomStyleDetailAll", httpMethod = "GET", notes = "根据订单号获取定制订单详情数据")
    public Result getCustomStyleDetailAll(HttpServletRequest request,
                                          @RequestParam(name = "mainRandomCode", required = true) Long mainRandomCode) {
        JSONObject jsonObject = new JSONObject();
        CustomStyleCraftCourse course = customStyleCraftCourseService.getOrderCustomStyleByRandomCode(mainRandomCode);
        if (ObjectUtils.isNotEmpt(course)) {
            // List<CraftMainFrame> mainFrameList = craftMainFrameService.selectMainFrameListByClothesCategoryCode(course.getClothesCategoryCode());
            List<CraftMainFrame> mainFrameList = craftMainFrameService.getByCondition(null, null, null);
            if (ObjectUtils.isNotEmptyList(mainFrameList)) {
                jsonObject.put("craftMainFrameList", mainFrameList);
            }
            course.setCustomOrderNo(course.getOrderNo() + "-" + course.getOrderLineId());
            jsonObject.put("masterData", course);
        }
        long t1 = System.currentTimeMillis();
        try {
            List<CustomStylePart> partList = customStylePartService.getMainRandomCodePartList(mainRandomCode);
            if (ObjectUtils.isNotEmptyList(partList)) {
                List<Long> partRandomCode = new ArrayList<>();
                partList.parallelStream().forEach(part -> partRandomCode.add(part.getRandomCode()));
                List<CustomStylePartCraft> partCraftList = customStylePartCraftService.getPartRandomCodeCraftList(partRandomCode);
                if (ObjectUtils.isNotEmptyList(partList) && ObjectUtils.isNotEmptyList(partCraftList)) {
                  //  List<Long> partCraftRandomCodeList = new ArrayList<>();
                   // partCraftList.forEach(part -> partCraftRandomCodeList.add(part.getRandomCode()));
                    // List<CustomStylePartCraftMotion> motionList = customStylePartCraftMotionService.getCraftRandomCodeMotionList(partCraftRandomCodeList);
                    //if (ObjectUtils.isNotEmptyList(motionList)) {
                    //Map<Long, List<CustomStylePartCraftMotion>> groupCraftMotionMap = motionList.stream().collect(Collectors.groupingBy(CustomStylePartCraftMotion::getPartCraftRandomCode));
                    //for (CustomStylePartCraft craftVo : partCraftList) {
                    partCraftList.stream().parallel().forEach(craftVo -> {
                        searchCraftData(craftVo);
                        changeCategoryAndPartDataToList(craftVo);
                        List<SewingCraftAction> actions = sewingCraftActionService.getDataBySewingCraftCode(craftVo.getCraftCode());
                        List<CustomStylePartCraftMotion> customStylePartCraftMotions = changeAction(craftVo.getRandomCode(), actions);
                        if (null != customStylePartCraftMotions && customStylePartCraftMotions.size() > 0) {
                            craftVo.setStylePartCraftMotionList(customStylePartCraftMotions);
                        }
                        //if (groupCraftMotionMap.containsKey(craftVo.getRandomCode())) {
                        //craftVo.setStylePartCraftMotionList(groupCraftMotionMap.get(craftVo.getRandomCode()));
                        // }
                            /*for (Long key : groupCraftMotionMap.keySet()) {
                                if (craftVo.getRandomCode().equals(key)) {
                                    craftVo.setStylePartCraftMotionList(groupCraftMotionMap.get(key));
                                }
                            }*/
                    });

                    //  }
                    Map<Long, List<CustomStylePartCraft>> groupCraftMap = partCraftList.stream().collect(Collectors.groupingBy(CustomStylePartCraft::getStylePartRandomCode));
                    partList.stream().forEach(part -> {
                        if (groupCraftMap.containsKey(part.getRandomCode())) {
                            part.setPartCraftList(groupCraftMap.get(part.getRandomCode()));
                        }
                       /* groupCraftMap.forEach((key, value) -> {
                            if (key.equals(part.getRandomCode())) part.setPartCraftList(value);
                        });*/
                    });
                }
            }
            jsonObject.put("customPartList", partList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long t2 = System.currentTimeMillis();
        LOGGER.error("" + (t2 - t1));
        t1 = System.currentTimeMillis();
        try {
            jsonObject.put("styleRuleList", customStyleRuleService.getStyleMainRandomRuleList(mainRandomCode));
            jsonObject.put("sewPositionList", customStyleSewPositionService.getPartRandomCodeSewPositionList(mainRandomCode));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
        t2 = System.currentTimeMillis();
        LOGGER.error("" + (t2 - t1));

        return Result.ok(jsonObject);
    }

    private List<CustomStylePartCraftMotion> changeAction(Long partCraftRandomCode, List<SewingCraftAction> actions) {
        List<CustomStylePartCraftMotion> result = new ArrayList<>();
        if (null != actions && actions.size() > 0) {
            for (SewingCraftAction action : actions) {
                CustomStylePartCraftMotion mo = new CustomStylePartCraftMotion();
                mo.setPartCraftRandomCode(partCraftRandomCode);
                mo.setOrderNum(action.getOrderNum());
                mo.setMotionCode(action.getMotionCode());
                mo.setMotionName(action.getMotionName());
                mo.setFrequency(action.getFrequency());
                mo.setDescription(action.getDescription());
                mo.setSpeed(action.getSpeed());
                mo.setMachineTime(action.getMachineTime());
                mo.setManualTime(action.getManualTime());
                result.add(mo);
            }
        }
        return result;
    }

    private HashMap parameterAssembly(Map<String, String> requestParam) {
        HashMap params = new HashMap();
        if (requestParam != null && requestParam.size() > 0) {
            String keywords = MapUtils.getString(requestParam, "keywords");
            if (StringUtils.isNotBlank(keywords)) {
                params.put("keywords", keywords);
            }
            //工艺品类
            String craftCode = MapUtils.getString(requestParam, "craftCode");
            if (StringUtils.isNotBlank(craftCode)) {
                params.put("craftCode", craftCode);
            }
            //服装品类
            String clothingCode = MapUtils.getString(requestParam, "clothingCode");
            if (StringUtils.isNotBlank(clothingCode)) {
                params.put("clothingCode", clothingCode);
            }
            //工艺主框架
            String mainFrameCode = MapUtils.getString(requestParam, "mainFrameCode");
            if (StringUtils.isNotBlank(mainFrameCode)) {
                params.put("mainFrameCode", mainFrameCode);
            }
            //设计部件
            String queryDesignData = MapUtils.getString(requestParam, "queryDesignData");
            if (StringUtils.isNotBlank(queryDesignData)) {
                params.put("queryDesignData", queryDesignData);
            }
            //工厂编码
            String factoryCode = MapUtils.getString(requestParam, "factoryCode");
            if (StringUtils.isNotBlank(factoryCode)) {
                params.put("factoryCode", factoryCode);
            }
            //生产组别
            String productionCategory = MapUtils.getString(requestParam, "productionCategory");
            if (StringUtils.isNotBlank(productionCategory)) {
                params.put("productionCategory", productionCategory);
            }
            //创建时间
            String createTime = MapUtils.getString(requestParam, "createTime");
            if (StringUtils.isNotBlank(createTime)) {
                params.put("createTime", createTime);
            }
            //发布用户
            String releaseUser = MapUtils.getString(requestParam, "releaseUser");
            if (StringUtils.isNotBlank(releaseUser)) {
                params.put("releaseUser", releaseUser);
            }
            //发布时间
            String releaseTimeStart = MapUtils.getString(requestParam, "releaseTimeStart");
            if (StringUtils.isNotBlank(releaseTimeStart)) {
                params.put("releaseTimeStart", releaseTimeStart);
            }
            //发布时间
            String releaseTimEnd = MapUtils.getString(requestParam, "releaseTimEnd");
            if (StringUtils.isNotBlank(releaseTimEnd)) {
                params.put("releaseTimEnd", releaseTimEnd);
            }
            Integer status = MapUtils.getInteger(requestParam, "status");
            if (status != null) {
                params.put("status", status);
            }
        }
        return params;
    }


    @RequestMapping(value = "/saveDraftCustomOrderInformation", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "saveDraftCustomOrderInformation", httpMethod = "POST", notes = "保存定制订单草稿信息")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", dataType = "MessageParam", name = "data", value = "信息参数", required = true)})
    @Transactional(rollbackFor = Exception.class)
//    @Authentication(auth = Authentication.AuthType.EDIT)
    public Result saveDraftCustomOrderInformation(HttpServletRequest request,
                                                  @RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser) {
        Long randomCode = SnowflakeIdUtil.generateId();

        String data="";
        try {
            data = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtils.isBlank(data)) {
            return Result.build(BusinessConstants.CommonConstant.ZERO, MessageConstant.STATUS_TEXT_DATA_NULL);
        }
        CustomStyleCraftCourse course = JSONObject.parseObject(data, CustomStyleCraftCourse.class);
        if (course.getStatus().equals(BusinessConstants.Status.IN_VALID) ||
                course.getStatus().equals(BusinessConstants.Status.INVALID_STATUS)) {
            return Result.build(BusinessConstants.CommonConstant.ZERO, "状态码错误");
        }

        if (ObjectUtils.isEmptyList(course.getCustomPartList()) ||
                ObjectUtils.isEmptyList(course.getSewPositionList())) {
            return Result.build(BusinessConstants.CommonConstant.ZERO, MessageConstant.STATUS_TEXT_DATA_NULL);
        }
        boolean isNew = false;
        if (course.getId() == null && course.getRandomCode() == null) {
            isFalse(customStyleCraftCourseService.isCustomOrderExist(course.getOrderNo(),
                    course.getOrderLineId(), course.getReleaseVersion()), "该版本号的订单工艺已存在，不允许保存");
            course.setRandomCode(randomCode);
            course.setCreateUser(currentUser.getUserName());
            isNew = true;
        }

        try {
            if (!isNew && course.getId() != null && course.getRandomCode() != null) {
                course.setUpdateUser(currentUser.getUserName());
                if (!customStyleCraftCourseService.removeAllCustomStyleDetailData(course.getRandomCode())) {
                    throw new Exception();
                }
            }

            Boolean bol = customStyleCraftCourseService.changeCustomStyleOrderInfo(course, course.getStatus());
            if (!bol) {
                throw new Exception();
            } else {
                if (BusinessConstants.Status.DRAFT_STATUS.equals(course.getStatus())) {
                    taskExecutor.execute(() -> {
                        sectionSMVService.sendCustomStyleActualSectionSMV(course.getOrderNo(),
                                course.getOrderLineId(), true);
                    });
                }

            }
            randomCode = course.getRandomCode();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return Result.build(BusinessConstants.CommonConstant.ZERO, MessageConstant.STATUS_TEXT_INTERNAL_ERROR);
        }
        return Result.ok(customStyleCraftCourseService.getOrderCustomStyleByRandomCode(randomCode));
    }

    @RequestMapping(value = "/getCraftCalculationTimeOfPrice", method = RequestMethod.POST)
    @ApiOperation(value = "getCraftCalculationTimeOfPrice", httpMethod = "POST", notes = "根据订单和工序列表计算工序标准时间标准单价")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", dataType = "MessageParam", name = "data", value = "信息参数", required = true)})
    public Result getCraftCalculationTimeOfPrice(HttpServletRequest request) {
        JSONObject json = new JSONObject();
        try {
            String data = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
            if (StringUtils.isBlank(data)) {
                return Result.build(BusinessConstants.CommonConstant.ZERO, MessageConstant.STATUS_TEXT_DATA_NULL);
            }
            CustomStyleMasterVo requestParam = JSONObject.parseObject(data, CustomStyleMasterVo.class);
            if (ObjectUtils.isEmpty(requestParam) || StringUtils.isBlank(requestParam.getOrderLineId()) ||
                    StringUtils.isBlank(requestParam.getOrderNo()) ||
                    StringUtils.isBlank(requestParam.getReleaseVersion())) {

                return Result.build(BusinessConstants.CommonConstant.ZERO, MessageConstant.STATUS_TEXT_DATA_NULL);
            }
            if ("bigstyle".equalsIgnoreCase(requestParam.getSearchType()) && (requestParam.getCodeList() == null || requestParam.getCodeList().size() == 0)) {
                return Result.build(BusinessConstants.CommonConstant.ZERO, MessageConstant.STATUS_TEXT_DATA_NULL);
            }
            if (!"bigstyle".equalsIgnoreCase(requestParam.getSearchType()) && ObjectUtils.isEmptyList(requestParam.getCraftCodeList())) {
                return Result.build(BusinessConstants.CommonConstant.ZERO, MessageConstant.STATUS_TEXT_DATA_NULL);
            }
            CustomStyleCraftCourse craftCourse = customStyleCraftCourseService.getOrderCustomStyleByVersion(requestParam.getOrderNo(),
                    requestParam.getOrderLineId(), requestParam.getReleaseVersion());
            if (ObjectUtils.isEmpty(craftCourse)) {
                return Result.build(BusinessConstants.CommonConstant.ZERO, "根据订单号找不到定制订单信息");
            }
            PICustomOrderPartMaterial mainFabric = partMaterialService.getMainMaterialData(requestParam.getOrderNo(),
                    requestParam.getOrderLineId(), craftCourse.getMainMaterialCode());
            PICustomOrder piCustomOrder = piCustomOrderService.getOrderId(requestParam.getOrderNo(), requestParam.getOrderLineId());
            if (ObjectUtils.isEmpty(mainFabric) || ObjectUtils.isEmpty(piCustomOrder)) {
                return Result.build(BusinessConstants.CommonConstant.ZERO, "根据订单号找不到定制订单主面料信息");
            }
            if (StringUtils.isNotBlank(piCustomOrder.getPatternSymmetry())) {
                mainFabric.setPatternSymmetry(piCustomOrder.getPatternSymmetry());
            }

            final List<VSewingCraftVo> sewingCraftWarehouseList = new ArrayList<>();
            if ("bigstyle".equalsIgnoreCase(requestParam.getSearchType())) {
                String bigStyleAnalyseCode = requestParam.getBigStyleAnalyseCode();
                List<StyleCraftVO> styleCraftVOS = requestParam.getCodeList();
                Map<String, String> styleMap = new HashMap<>();
                List<String> bigStyleAnalyseCodes = new ArrayList<>(0);
                if (null != styleCraftVOS && styleCraftVOS.size() > 0) {
                    for (StyleCraftVO vo : styleCraftVOS) {
                        String key = vo.getBigStyleAnalyseCode() + vo.getCode();
                        styleMap.put(key, key);
                        bigStyleAnalyseCodes.add(vo.getBigStyleAnalyseCode());
                    }
                    List<VSewingCraftVo> temp = styleSewingCraftWarehouseService.getCraftCodeDataAll(styleMap, mainFabric.getMaterialGrade(), craftCourse.getMainFrameCode(), bigStyleAnalyseCodes);
                    sewingCraftWarehouseList.addAll(temp);
                }

            } else {
                String[] codeArray = new String[requestParam.getCraftCodeList().size()];
                for (int i = 0; i < requestParam.getCraftCodeList().size(); i++) {
                    codeArray[i] = requestParam.getCraftCodeList().get(i);
                }
                List<VSewingCraftVo> temp = sewingCraftWarehouseService.getCraftCodeDataAll(codeArray, mainFabric.getMaterialGrade(), craftCourse.getMainFrameCode());
                sewingCraftWarehouseList.addAll(temp);
            }
            List<CustomStylePartCraftMotion> motionList = new ArrayList<>();
            List<CustomStylePartCraft> customStylePartCraftList = new ArrayList<>();
            if (ObjectUtils.isNotEmptyList(sewingCraftWarehouseList)) {
                sewingCraftWarehouseList.parallelStream().forEach(craftvo -> {
                    craftvo.setRandomCode(SnowflakeIdUtil.generateId());
                    craftvo.setStatus(craftCourse.getStatus());
                    craftvo.setStandardTime(craftvo.getStandardTime() != null ? craftvo.getStandardTime() : BigDecimal.ZERO.setScale(3));
                    craftvo.setStandardPrice(craftvo.getStandardPrice() != null ? craftvo.getStandardPrice() : BigDecimal.ZERO.setScale(3));

                    if (ObjectUtils.isNotEmptyList(craftvo.getMotionsList())) {
                        List<CustomStylePartCraftMotion> list = ListUtils.copyList(craftvo.getMotionsList(), CustomStylePartCraftMotion.class);
                        list.parallelStream().forEach(motion -> {
                            motion.setId(null);
                            motion.setStatus(craftCourse.getStatus());
                            motion.setCreateUser(craftCourse.getCreateUser());
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

                });
            }
            customStylePartCraftList = ListUtils.copyList(sewingCraftWarehouseList, CustomStylePartCraft.class);
            List<OrderGrade> orderGradeList = orderGradeService.getAllOrderGrade();
            for (CustomStylePartCraft craft : customStylePartCraftList) {
                craft.setFabricScore(craftCourse.getFabricsScore());
                changeCategoryAndPartDataToList(craft);
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
                                if (craftCourse.getFabricsScore() >= fabricScore.getMinValue() && craftCourse.getFabricsScore() <= fabricScore.getMaxValue()) {
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
            List<CustomStyleSewPosition> styleSewPositionList = customStyleSewPositionService.getPartRandomCodeSewPositionList(craftCourse.getRandomCode());
            customStylePartCraftList = customStyleCraftCourseService.calculateCost(craftCourse.getFactoryCode(), mainFabric, customStylePartCraftList, motionList, styleSewPositionList);
            List<CustomStylePartCraft> partCraftVoList = ListUtils.copyList(customStylePartCraftList, CustomStylePartCraft.class);
            List<CustomStylePartCraftMotion> motionVos = ListUtils.copyList(motionList, CustomStylePartCraftMotion.class);
            Map<Long, List<CustomStylePartCraftMotion>> groupByCraftMotionList = motionVos.stream().collect(Collectors.groupingBy(CustomStylePartCraftMotion::getPartCraftRandomCode));
            if (ObjectUtils.isNotEmpt(groupByCraftMotionList)) {
                for (CustomStylePartCraft craftVo : partCraftVoList) {
                    for (Long randomCode : groupByCraftMotionList.keySet()) {
                        if (craftVo.getRandomCode().equals(randomCode)) {
                            craftVo.setStylePartCraftMotionList(groupByCraftMotionList.get(randomCode));
                        }
                    }
                    //查询工艺建标，缝边位置，工位工序的数据
                    searchCraftData(craftVo);
                }
            }
            json.put("partCraftList", partCraftVoList);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Result.ok(json);
    }

    /**
     * 查询工艺建标，缝边位置，工位工序的数据
     */
    private void searchCraftData(final CustomStylePartCraft craftVo) {
        if (null == craftVo) {
            return;
        }
        //工序编码
        String craftCode = craftVo.getCraftCode();
        Result result = sewingCraftWarehouseService.getSewingCraftRandomCode(craftCode);
        Long code = (Long) result.getData();
        if (null == code) {
            return;
        }

        try {
            List<SewingCraftStd> sewingCraftStdsTmp = sewingCraftStdService.getDataBySewingCraftRandomCode(code);
            List<CraftStd> craftStdList = new ArrayList<>();
            if (null != sewingCraftStdsTmp && sewingCraftStdsTmp.size() > 0) {
                for (SewingCraftStd std : sewingCraftStdsTmp) {
                    List<CraftStd> data = getCraftStdByParam(std.getCraftStdCode(), null, null, null);
                    if (data != null && data.size() > 0) {
                        craftStdList.addAll(data);
                    }
                }
            }
            craftVo.setCraftStdList(craftStdList);
        } catch (Exception e) {
            e.getMessage();
        }
        try {
            List<SewingCraftPartPosition> sewPartPositionListTmp = sewingCraftPartPositionService.getDataBySewingCraftRandomCode(code);
            craftVo.setSewPartPositionList(sewPartPositionListTmp);
        } catch (Exception e) {
            e.getMessage();
        }
        try {
            final Map<String, Object> param = new HashMap<>();
            param.put("sewingCraftRandomCode", code);
            List<SewingCraftWarehouseWorkplace> workplaceCraftListTmp = sewingCraftWarehouseWorkplaceService.getDataByParam(param);
            craftVo.setWorkplaceCraftList(workplaceCraftListTmp);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private List<CraftStd> getCraftStdByParam(String param, String craftCategoryCode, String craftPartCode, String craftCategoryListStr) {
        Map<String, Object> map = new HashMap<>();
        List<String> craftCategoryList = null;
        if (StringUtils.isNotEmpty(craftCategoryListStr)) {
            String str[] = craftCategoryListStr.split(",");
            craftCategoryList = new ArrayList<>();
            craftCategoryList.addAll(Arrays.asList(str));
        }
        if (null != craftCategoryList && craftCategoryList.size() > 0) {
            map.put("craftCategoryList", craftCategoryList);
        }
        String[] statusArr = {Const.STD_STATUS_AUDIT,Const.STD_STATUS_OLD};
        map.put("param", param);
        map.put("status", statusArr);
        map.put("craftCategoryCode", craftCategoryCode);
        map.put("craftPartCode", craftPartCode);
        List<CraftStd> data = craftStdService.getCraftStdByLikeParam(map);
        if (null != data && data.size() > 0) {
            //data.stream().parallel().forEach(std -> {
            data.stream().forEach(std -> {
                //线稿图
                List<String> pictureUrlList = new ArrayList<>(0);
                //视频
                List<String> vedioUrlList = new ArrayList<>(0);
                List<CraftFile> files = craftFileService.getByKeyId(std.getId());
                //后续优化--暂时用这个来
                if (files != null && files.size() > 0) {
                    for (CraftFile file : files) {
                        //线稿图
                        if (Const.RES_TYPE_HAND_IMG == file.getResourceType()) {
                            pictureUrlList.add(file.getFileUrl());
                        } else if (Const.RES_TYPE_STD_VIDEO == file.getResourceType()) {
                            vedioUrlList.add(file.getFileUrl());
                        }
                    }
                }
                //辅助工具
                List<CraftStdTool> tools = craftStdToolService.getCraftStdToolByCraftStdId(std.getId());
                String toolName = "";
                String toolCode = "";
                if (null != tools && tools.size() > 0) {
                    int len = tools.size();
                    for (int i = 0; i < len; i++) {
                        CraftStdTool tool = tools.get(i);
                        toolName = toolName + tool.getToolName();
                        toolCode = toolCode + tool.getToolCode();
                        if (i < len - 1) {
                            toolName = toolName + ",";
                            toolCode = toolCode + ",";
                        }
                    }
                }
                std.setHelpTool(toolCode);
                std.setHelpToolCode(toolName);
                std.setVedioUrlList(vedioUrlList);
                std.setPictureUrlList(pictureUrlList);
            });
        }
        return data;
    }

    @RequestMapping(value = "/updateStatus", method = RequestMethod.GET)
    @ApiOperation(value = "updateStatus", httpMethod = "GET", notes = "修改定制订单状态")
    public Result updateStatus(@RequestParam(name = "mainRandomCode", required = true) Long mainRandomCode,
                               @RequestParam(name = "status", required = true) Integer status,
                               @RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser) {
        if (status.equals(BusinessConstants.Status.DRAFT_STATUS) ||
                status.equals(BusinessConstants.Status.PUBLISHED_STATUS) ||
                status.equals(BusinessConstants.Status.AUDITED_STATUS)) {
            return Result.build(BusinessConstants.CommonConstant.ZERO, "状态码错误");
        }
        CustomStyleCraftCourse craftCourse = customStyleCraftCourseService.getOrderCustomStyleByRandomCode(mainRandomCode);
        if (ObjectUtils.isEmpty(craftCourse)) {
            return Result.build(BusinessConstants.CommonConstant.ZERO, "当前mainRandomCode找不到定制订单数据");
        }
        craftCourse.setStatus(status);
        craftCourse.setExpirationTime(new Date());
        craftCourse.setUpdateUser(currentUser.getUserName());
        List<CustomStylePart> stylePartList = customStylePartService.getMainRandomCodePartList(mainRandomCode);
        List<CustomStylePartCraftMotion> changePartCraftMotionList = new ArrayList<>();
        List<CustomStylePartCraft> changePartCraftList = new ArrayList<>();
        List<CustomStyleRule> changeRuleList = customStyleRuleService.getStyleMainRandomRuleList(mainRandomCode);
        List<CustomStyleSewPosition> changeSewPositionList = customStyleSewPositionService.getPartRandomCodeSewPositionList(mainRandomCode);
        for (CustomStylePart part : stylePartList) {
            part.setStatus(status);
            part.setUpdateUser(currentUser.getUserName());
            List<CustomStylePartCraft> partCraftList = customStylePartCraftService.getPartRandomCodeCraftList(part.getRandomCode());
            if (ObjectUtils.isNotEmptyList(partCraftList)) {
                changePartCraftList.addAll(partCraftList);
                for (CustomStylePartCraft craft : partCraftList) {
                    craft.setStatus(status);
                    List<CustomStylePartCraftMotion> motionList = customStylePartCraftMotionService.getCraftRandomCodeMotionList(craft.getRandomCode());
                    if (ObjectUtils.isNotEmptyList(motionList)) {
                        changePartCraftMotionList.addAll(motionList);
                    }
                }
            }
        }
        if (ObjectUtils.isNotEmptyList(changePartCraftMotionList)) {
            changePartCraftMotionList.forEach(m -> m.setStatus(status));
        }
        if (ObjectUtils.isNotEmptyList(changeRuleList)) {
            changeRuleList.forEach(r -> r.setStatus(status));
        }
        if (ObjectUtils.isNotEmptyList(changeSewPositionList)) {
            changeSewPositionList.forEach(p -> p.setStatus(status));
        }
        boolean rept = false;
        if (status.equals(BusinessConstants.Status.INVALID_STATUS)) {
            rept = customStyleCraftCourseService.updateById(craftCourse);
            if (rept) {
                customStylePartService.updateBatchById(stylePartList);
                customStylePartCraftService.updateBatchById(changePartCraftList);
                customStylePartCraftMotionService.updateBatchById(changePartCraftMotionList);
                customStyleRuleService.updateBatchById(changeRuleList);
                customStyleSewPositionService.updateBatchById(changeSewPositionList);
            }
        } else if (status.equals(BusinessConstants.Status.IN_VALID)) {
            rept = customStyleCraftCourseService.removeById(craftCourse);
            if (rept) {

                if (ObjectUtils.isNotEmptyList(changePartCraftList)) {
                    List<Long> craftRandomCodeList = new ArrayList<>();
                    changePartCraftList.forEach(c -> craftRandomCodeList.add(c.getRandomCode()));
                    customStylePartCraftService.deleteCustomStyleCraftList(craftRandomCodeList);
                }
                if (ObjectUtils.isNotEmptyList(changePartCraftMotionList)) {
                    List<Long> craftMotionRandomCodeList = new ArrayList<>();
                    changePartCraftMotionList.forEach(m -> craftMotionRandomCodeList.add(m.getRandomCode()));
                    customStylePartCraftMotionService.deleteCustomStyleMotionList(craftMotionRandomCodeList);
                }
                customStylePartService.deleteCustomStylePart(craftCourse.getRandomCode());
                customStyleRuleService.deleteCustomStyleRule(craftCourse.getRandomCode());
                customStyleSewPositionService.deleteCustomStylePosition(craftCourse.getRandomCode());
            }
        }

        return Result.ok();
    }


    private void changeCategoryAndPartDataToList(CustomStylePartCraft customStylePartCraft) {
        if (null == customStylePartCraft) {
            return;
        }
        String craftCategoryCode = customStylePartCraft.getCraftCategoryCode();
        String craftCategoryName = customStylePartCraft.getCraftCategoryName();

        List<JSONObject> craftCategoryList = new ArrayList<>();
        if(StringUtils.isNotBlank(craftCategoryCode) && StringUtils.isNotBlank(craftCategoryName)) {
            String[] code_cat = craftCategoryCode.split(",");
            String[] name_cat = craftCategoryName.split(",");
            for (int i = 0; i < code_cat.length; i++) {
                JSONObject cat = new JSONObject();
                cat.put("craftCategoryCode", code_cat[i]);
                cat.put("craftCategoryName", name_cat[i]);
                craftCategoryList.add(cat);
            }
        }

        List<JSONObject> partList = new ArrayList<>();
        String craftPartName = customStylePartCraft.getCraftPartName();
        String craftPartCode = customStylePartCraft.getCraftPartCode();
        if(StringUtils.isNotBlank(craftPartCode) && StringUtils.isNotBlank(craftPartName)) {
            String[] code_part = craftPartCode.split(",");
            String[] name_part = craftPartName.split(",");
            for (int i = 0; i < code_part.length; i++) {
                JSONObject part = new JSONObject();
                part.put("craftPartCode", code_part[i]);
                part.put("craftPartName", name_part[i]);
                partList.add(part);
            }
        }
        customStylePartCraft.setCraftCategoryList(craftCategoryList);
        customStylePartCraft.setCraftPartList(partList);
    }

    @RequestMapping(value = "/getCustomStyleDataTest", method = RequestMethod.GET)
    @ApiOperation(value = "getCustomStyleDataTest", httpMethod = "GET", notes = "测试获取单条定制订单所有数据")
    public Result getCustomStyleDataTest(@RequestParam(name = "mainRandomCode", required = true) Long mainRandomCode) {
        CustomStyleCraftCourse craftCourse = customStyleCraftCourseService.getOrderCustomStyleByRandomCode(mainRandomCode);
        List<CustomStylePart> stylePartList = customStylePartService.getMainRandomCodePartList(mainRandomCode);
        for (CustomStylePart part : stylePartList) {
            List<CustomStylePartCraft> partCraftList = customStylePartCraftService.getPartRandomCodeCraftList(part.getRandomCode());
            if (ObjectUtils.isNotEmptyList(partCraftList)) {
                part.setPartCraftList(partCraftList);
                for (CustomStylePartCraft craft : partCraftList) {
                    List<CustomStylePartCraftMotion> motionList = customStylePartCraftMotionService.getCraftRandomCodeMotionList(craft.getRandomCode());
                    if (ObjectUtils.isNotEmptyList(motionList)) {
                        craft.setStylePartCraftMotionList(motionList);
                    }
                }
            }
        }
        craftCourse.setCustomPartList(stylePartList);
        craftCourse.setStyleRuleList(customStyleRuleService.getStyleMainRandomRuleList(mainRandomCode));
        craftCourse.setSewPositionList(customStyleSewPositionService.getPartRandomCodeSewPositionList(mainRandomCode));
        return Result.ok(craftCourse);
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ApiOperation(value = "test", notes = "测试服务")
    //@Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<JSONObject> test() {
        customStyleCraftCourseService.createCustomStyleByBigStyle("MBS20072900001", "1");

        JSONObject result = new JSONObject();
        return Result.ok(result);

    }


    @ApiOperation(value = "/exportCustomStyleCraftPath", notes = "导出单个款式工艺路径清单", httpMethod = "GET", response = Result.class)
    @RequestMapping(value = "/exportCustomStyleCraftPath", method = RequestMethod.GET)
    public Result exportCustomStyleCraftPath(@RequestParam(name = "randomCode", required = true) String randomCode,
                                          @RequestParam(name = "type", required = true) Integer type,
                                          HttpServletResponse response) throws Exception {
        if (StringUtils.isEmpty(randomCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "参数为空");
        }

        Long ran = Long.parseLong(randomCode);
        CustomStyleCraftCourse data = customStyleCraftCourseService.getOrderCustomStyleByRandomCode(ran);
        if (null == data) {
            return Result.ok("无数据导出");
        }
        List<CraftVO> craftVOS = new ArrayList<>();
        if (BusinessConstants.ExportTemplate.HAVE_PRICE.equals(type) || BusinessConstants.ExportTemplate.NOT_HAVE_PRICE.equals(type)) {
            craftVOS = customStyleCraftCourseService.getDataForExcelReportByCustomRandomCode(ran);

            for (int i = 1; i < craftVOS.size(); i++) {
                craftVOS.get(i - 1).setNextCraftCode(craftVOS.get(i).getCraftCode());
            }

            Map<String, Integer> productionPartMaxFlowMap = new HashMap<>();
            Map<String, String> productionPartMaxCraftMap = new HashMap<>();
            Map<String, Integer> productionPartMinFlowMap = new HashMap<>();
            Map<String, String> productionPartMinCraftMap = new HashMap<>();
            Map<String, CraftVO> craftVoMap = new HashMap<>();
            List<FlowNumConfig> flowNumConfigs = productionPartService.getFlowNumConfigAll();

            for (CraftVO craft : craftVOS) {
                FlowNumConfig flowNumConfig = flowNumConfigs.stream().filter(x -> craft.getCraftFlowNum() != null
                        && craft.getCraftFlowNum().toString().startsWith(x.getFlowNum())).findFirst().orElse(null);
                if (flowNumConfig == null) {
                    continue;
                }
                String productionPartCode = flowNumConfig.getProductionPartCode();

                craft.setProductionPartCode(productionPartCode);
                int lastMax = productionPartMaxFlowMap.getOrDefault(productionPartCode, -1);
                int lastMin = productionPartMinFlowMap.getOrDefault(productionPartCode, -1);


                if (craft.getCraftFlowNum().intValue() > lastMax || lastMax == -1) {
                    productionPartMaxFlowMap.put(productionPartCode, craft.getCraftFlowNum());
                    productionPartMaxCraftMap.put(productionPartCode, craft.getCraftCode());
                }
                if (craft.getCraftFlowNum().intValue() < lastMin || lastMin == -1) {
                    productionPartMinFlowMap.put(productionPartCode, craft.getCraftFlowNum());
                    productionPartMinCraftMap.put(productionPartCode, craft.getCraftCode());
                }
                craftVoMap.put(productionPartCode + craft.getCraftCode(), craft);
            }

            List<CraftMainFrameRoute> routes = craftMainFrameRouteService.getMainFrameRouteByCode(data.getMainFrameCode());
            for (CraftMainFrameRoute route : routes) {
                String currCraftCode = productionPartMaxCraftMap.getOrDefault(route.getProductionPartCode(), null);
                String nextCraftCode = productionPartMinCraftMap.getOrDefault(route.getNextProductionPartCode(), null);
                if (currCraftCode == null || nextCraftCode == null) {
                    continue;
                }
                CraftVO craftVO = craftVoMap.getOrDefault(route.getProductionPartCode() + currCraftCode, null);
                if (craftVO == null) {
                    continue;
                }
                craftVO.setNextCraftCode(nextCraftCode);
            }

            Map<String, Integer> nexCraftMap = new HashMap<>();
            for (CraftVO craftVO : craftVOS) {
                if (craftVO.getNextCraftCode() == null) {
                    continue;
                }
                Integer n = nexCraftMap.getOrDefault(craftVO.getNextCraftCode(), 0);
                n += 1;
                nexCraftMap.put(craftVO.getNextCraftCode(), n);
            }

            for (CraftVO craftVO : craftVOS) {
                Integer n = 0;
                if (craftVO.getNextCraftCode() != null) {
                    n = nexCraftMap.getOrDefault(craftVO.getNextCraftCode(), 0);
                }
                if (n >= 2) {
                    craftVO.setNextCraftCodeCount(n);
                }
            }
        } else if (BusinessConstants.ExportTemplate.EPS_HAVE_PRICE.equals(type)
                || BusinessConstants.ExportTemplate.EPS_NOT_HAVE_PRICE.equals(type)
                || BusinessConstants.ExportTemplate.FINANCE.equals(type)) {
            craftVOS = customStyleCraftCourseService.getDataForExcelReportOrderByCustomWorkOrder(ran);
        }


        //List<CraftVO> craftVOS = styleSewingCraftWarehouseService.getDataForExcelReport(ran);
        List<FabricMainData> list = fabricMainDataService.getFabricMainDataByCode(data.getMainMaterialCode());
        //经向弹性等级
        String warpElasticGrade = "";
        //纬向弹性等级
        String weftElasticGrade = "";
        if (null != list && list.size() > 0) {
            warpElasticGrade = list.get(0).getWarpElasticGrade();
            weftElasticGrade = list.get(0).getWeftElasticGrade();
        }

        try {
            if(BusinessConstants.ExportTemplate.FINANCE.equals(type)) {
                ExportCustomDataUtil.exportFinanceData(type, data, response, craftVOS, weftElasticGrade, warpElasticGrade);

            } else {
                ExportCustomDataUtil.exportData(type, data, response, craftVOS, weftElasticGrade, warpElasticGrade);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;


    }
}
