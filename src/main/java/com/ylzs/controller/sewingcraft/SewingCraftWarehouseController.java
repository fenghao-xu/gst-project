package com.ylzs.controller.sewingcraft;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ylzs.common.cache.StaticDataCache;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.constant.MessageConstant;
import com.ylzs.common.util.*;
import com.ylzs.common.util.pageHelp.PageUtils;
import com.ylzs.common.utils.object.ObjectUtils;
import com.ylzs.dao.bigstylecraft.BigStyleAnalysePartCraftDetailDao;
import com.ylzs.dao.bigticketno.BigOrderPartCraftDetailDao;
import com.ylzs.dao.sewingcraft.SewingCraftWarehouseWorkplaceDao;
import com.ylzs.elasticsearch.craftstd.dao.CraftStdRepository;
import com.ylzs.elasticsearch.workplacecraft.dao.WorkplaceCraftESRepository;
import com.ylzs.elasticsearch.workplacecraft.esbo.WorkplaceCraftEsBO;
import com.ylzs.entity.bigstylecraft.BigStyleAnalysePartCraftDetail;
import com.ylzs.entity.craftmainframe.ProductionPart;
import com.ylzs.entity.craftstd.*;
import com.ylzs.entity.sewingcraft.*;
import com.ylzs.entity.staticdata.CraftGradeEquipment;
import com.ylzs.entity.system.User;
import com.ylzs.entity.system.WebConfig;
import com.ylzs.entity.timeparam.*;
import com.ylzs.service.bigstylecraft.*;
import com.ylzs.service.bigticketno.*;
import com.ylzs.service.craftmainframe.ICraftMainFrameService;
import com.ylzs.service.craftmainframe.IProductionPartService;
import com.ylzs.service.craftstd.ICraftCategoryService;
import com.ylzs.service.craftstd.IMachineService;
import com.ylzs.service.craftstd.IMakeTypeService;
import com.ylzs.service.craftstd.impl.CraftFileService;
import com.ylzs.service.craftstd.impl.CraftStdService;
import com.ylzs.service.craftstd.impl.CraftStdToolService;
import com.ylzs.service.pi.ISendPiService;
import com.ylzs.service.sewingcraft.*;
import com.ylzs.service.staticdata.PartPositionService;
import com.ylzs.service.staticdata.WorkTypeService;
import com.ylzs.service.system.IUserService;
import com.ylzs.service.system.IWebConfigService;
import com.ylzs.service.timeparam.*;
import com.ylzs.service.timeparam.impl.SameLevelCraftService;
import com.ylzs.vo.FabricScorePlanVO;
import com.ylzs.vo.SewingCraftVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

//import netscape.javascript.JSObject;

/**
 * @author xuwei
 * @create 2020-02-26 11:13
 * ??????????????????
 */
@Api(tags = "??????????????????")
@RestController
@RequestMapping("/sewingCraft")
public class SewingCraftWarehouseController {
    private final static String DEFAULT_SITE = "8081";

    private static final Logger LOGGER = LoggerFactory.getLogger(SewingCraftWarehouseController.class);

    @Resource
    private ICraftCategoryService craftCategoryService;

    @Resource
    private IMakeTypeService makeTypeService;

    @Resource
    private IWebConfigService webConfigService;

    @Resource
    private ISendPiService sendPi;

    @Resource
    private CraftGradeEquipmentService gradeEquipmentService;

    @Resource
    private IMachineService machineService;

    @Resource
    private WideCoefficientService wideCoefficientService;

    @Resource
    private StrappingTimeConfigService strappingTimeConfigService;

    @Resource
    private WorkplaceCraftESRepository workplaceCraftESRepository;

    @Resource
    private CraftStdRepository craftStdRepository;

    @Resource
    private PartPositionService partPositionService;

    @Resource
    private FabricScorePlanService fabricScorePlanService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Resource
    private SewingCraftWarehouseService sewingCraftWarehouseService;

    @Resource
    private CraftStdService craftStdService;

    @Resource
    private WorkTypeService workTypeService;

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
    private ICraftMainFrameService mainFrameService;

    @Resource
    private StyleSewingCraftWarehouseService styleSewingCraftWarehouseService;

    @Resource
    private StyleSewingCraftActionService styleSewingCraftActionService;

    @Resource
    private StyleSewingCraftPartPositionService styleSewingCraftPartPositionService;

    @Resource
    private StyleSewingCraftWarehouseWorkplaceService styleSewingCraftWarehouseWorkplaceService;

    @Resource
    private StyleSewingCraftStdService styleSewingCraftStdService;

    @Resource
    private BigOrderSewingCraftWarehouseService bigOrderSewingCraftWarehouseService;

    @Resource
    private BigOrderSewingCraftActionService bigOrderSewingCraftActionService;

    @Resource
    private BigOrderSewingCraftPartPositionService bigOrderSewingCraftPartPositionService;

    @Resource
    private BigOrderSewingCraftWarehouseWorkplaceService bigOrderSewingCraftWarehouseWorkplaceService;

    @Resource
    private BigOrderSewingCraftStdService bigOrderSewingCraftStdService;

    @Resource
    private IOrderGradeService orderGradeService;

    @Resource
    private FabricScoreService fabricScoreService;

    @Resource
    private FabricGradeService fabricGradeService;

    @Resource
    ThreadPoolTaskExecutor taskExecutor;

    @Resource
    private CraftStdToolService craftStdToolService;

    @Resource
    private IUserService userService;

    @Resource
    private SewingCraftWarehouseWorkplaceDao sewingCraftWarehouseWorkplaceDao;

    @Resource
    private SameLevelCraftService sameLevelCraftService;

    @Resource
    private IProductionPartService productionPartService;

    @Resource
    private BigStyleOperationLogService bigStyleOperationLogService;

    @Resource
    private BigStyleAnalysePartCraftDetailDao bigStyleAnalysePartCraftDetailDao;

    @Resource
    private BigOrderPartCraftDetailDao bigOrderPartCraftDetailDao;


    /**
     * ???????????? ?????????????????????
     */
    @ApiOperation(value = "getDropDownData", notes = "???????????? ?????????????????????")
    @RequestMapping(value = "/getDropDownData", method = RequestMethod.GET)
    public Result<JSONObject> getDropDownData() {
        JSONObject result = new JSONObject();
        //List<CraftCategory> craftCategory = craftCategoryService.getAllValidCraftCategory();
        // result.put("craftCategory",craftCategory);
        //??????????????????bug ???????????????
        List<WebConfig> webConfigList = webConfigService.getConfigList();
        //1???????????????????????????????????????
        List<CraftCategory> craftCategories = craftCategoryService.getCraftCategoryAndPart();
        if (null != craftCategories && craftCategories.size() > 0 && craftCategories.size() < 5) {
            LOGGER.info("craftCategories???????????????:" + craftCategories.size());
            try {
                int loop = 20;
                while (craftCategories.size() < 5) {
                    TimeUnit.MICROSECONDS.sleep(10);
                    craftCategories = craftCategoryService.getCraftCategoryAndPart();
                    LOGGER.info("craftloop " + loop + " ???,?????????" + craftCategories.size());
                    loop--;
                    if (loop < 0) {
                        break;
                    }
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                e.printStackTrace();

            }
        }
        result.put("craftCategories", craftCategories);


        //2???????????????
       /* List<MakeType> makeTypes = makeTypeService.getAllMakeType();
        result.put("makeTypes", makeTypes);*/
        List<WorkType> workTypes = workTypeService.getWorkTypeAndMakeType();
        result.put("workTypes", workTypes);
        //3?????????????????????
        List<CraftGradeEquipment> craftGrades = gradeEquipmentService.getCraftGradeByType(BusinessConstants.CraftGradeEquipmentType.CRAFT_GRADE_SEWING);
        result.put("craftGrades", craftGrades);

        //4???????????????
        List<Machine> machines = machineService.getAllMachine();
        result.put("machines", machines);

        //5???????????????
        List<WideCoefficient> wideCoefficients = wideCoefficientService.getAllWideCoefficient();
        result.put("wideCoefficients", wideCoefficients);

        //6???????????????
        List<StrappingTimeConfig> strappingTimeConfigs = strappingTimeConfigService.getAllStrappingTimeConfigs();
        result.put("strappingTimeConfigs", strappingTimeConfigs);

        //7?????????????????????
        List<FabricScorePlanVO> fabricScorePlanVOS = fabricScorePlanService.selectFabricScoresAndPlan();
        result.put("fabricScorePlanVOS", fabricScorePlanVOS);

        //8????????????
        List<SameLevelCraft> sameLevelCrafts = sameLevelCraftService.getAllData();
        result.put("sameLevelCrafts", sameLevelCrafts);
        return Result.ok(MessageConstant.SUCCESS, result);

    }

    @RequestMapping(value = "/getSewingCraftByRandomCode", method = RequestMethod.GET)
    @ApiOperation(value = "getSewingCraftByRandomCode", notes = "??????randomCode??????")
    public Result<JSONObject> getSewingCraftByRandomCode(@RequestParam String randomCode) {
        LOGGER.info("?????????randomCode???" + randomCode);
        if (StringUtils.isEmpty(randomCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "????????????");
        }
        JSONObject result = new JSONObject();
        try {
            Long code = Long.parseLong(randomCode);
            SewingCraftWarehouse sewingCraftWarehouse = sewingCraftWarehouseService.getDataByRandom(code);
            result = getDetailDataToJSONObject(sewingCraftWarehouse, BusinessConstants.TableName.SEWING_CRAFT, null, null, null);
        } catch (Exception e) {
            LOGGER.error("???????????????????????????:" + e.getMessage());
        }
        return Result.ok(MessageConstant.SUCCESS, result);
    }

    @RequestMapping(value = "/getMinuteWage", method = RequestMethod.GET)
    @ApiOperation(value = "getMinuteWage", notes = "?????????????????????????????????????????????")
    public Result<BigDecimal> getMinuteWage(@RequestParam String factoryCode, @RequestParam String craftGradeCode) {
        Map<String, BigDecimal> minueMap = getMinueMap();
        BigDecimal data = minueMap.get(factoryCode + "_" + craftGradeCode);
        if (null == data) {
            data = BigDecimal.ZERO;
        }
        return Result.ok(MessageConstant.SUCCESS, data.setScale(3, BigDecimal.ROUND_HALF_UP));
    }

    /**
     * key?????????_????????????
     */
    private Map<String, BigDecimal> getMinueMap() {
        List<CraftGradeEquipment> list = gradeEquipmentService.getAllCraftGrade();
        Map<String, BigDecimal> minueMap = new HashMap<>(0);
        if (null != list && list.size() > 0) {
            for (CraftGradeEquipment vo : list) {
                minueMap.put(vo.getFactoryCode() + "_" + vo.getCraftGradeCode(), vo.getMinuteWage());
            }
        }
        return minueMap;
    }

    /**
     * ????????????????????????????????????,????????????????????????????????????
     * ??????????????????????????????????????????????????????????????????????????????????????????????????????
     */
    @RequestMapping(value = "/getSewingCraftByCraftCodeList", method = RequestMethod.POST)
    @ApiOperation(value = "getSewingCraftByCraftCodeList", notes = "????????????????????????????????????")
    public Result<List<JSONObject>> getSewingCraftByCraftCodeList(HttpServletRequest request) throws Exception {
        //?????????idList ??????????????????????????????????????????????????????????????????
        String data = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        if (StringUtils.isEmpty(data)) {
            return Result.build(MessageConstant.PARAM_NULL, "????????????");
        }
        LOGGER.info("????????????????????????????????????" + data);
        JSONObject dataObj = JSONObject.parseObject(data);
        JSONArray idArray = dataObj.getJSONArray("idList");
        String flag = dataObj.getString("flag");
        String searchType = dataObj.getString("searchType");

        String factoryCode = dataObj.getString("factoryCode");
        String mainFrameCode = dataObj.getString("mainFrameCode");
        Integer fabricFraction = dataObj.getInteger("fabricFraction");
        List<SewingCraftWarehouse> sewingCraftWarehouseList = new ArrayList<>(0);
        //Map<String, JSONObject> mapData = new HashMap<>();
        //??????????????????????????????
        final List<JSONObject> result = Collections.synchronizedList(new ArrayList());
        Map<String, BigDecimal> minueMap = getMinueMap();
        //??????????????????bug ???????????????
        List<WebConfig> webConfigList = webConfigService.getConfigList();
        //???idList???????????????????????????????????????????????????????????????????????????
        if (null != idArray && idArray.size() > 0) {
            List<Long> idList = new ArrayList<>();
            for (int i = 0; i < idArray.size(); i++) {
                idList.add(Long.parseLong(idArray.getJSONObject(i).getString("id")));
            }
            LOGGER.info("idList???" + idList);

            // List<SewingCraftWarehouse> sews = styleSewingCraftWarehouseService.getDataByPartDetailIds(idList);
            //?????????????????????????????????????????????????????????
            String tableName = "order".equalsIgnoreCase(flag) ? BusinessConstants.TableName.BIG_ORDER : BusinessConstants.TableName.BIG_STYLE_ANALYSE;
            List<SewingCraftWarehouse> sews = new ArrayList<>();
            if ("order".equalsIgnoreCase(flag)) {
                LOGGER.info("??????order???");
                sews = bigOrderSewingCraftWarehouseService.getDataByPartDetailIds(idList);
            } else {
                LOGGER.info("??????style???");
                sews = styleSewingCraftWarehouseService.getDataByPartDetailIds(idList);

            }
            //???????????????????????????
            LOGGER.info("idList???length???:" + idList.size() + " sews???length???:" + sews.size());
            if (null != sews && sews.size() > 0 && sews.size() != idList.size()) {
                LOGGER.info("??????????????????");
                try {
                    int loop = 10;
                    while (sews.size() != idList.size()) {
                        TimeUnit.MICROSECONDS.sleep(10);
                        if ("order".equalsIgnoreCase(flag)) {
                            LOGGER.info("??????order???");
                            sews = bigOrderSewingCraftWarehouseService.getDataByPartDetailIds(idList);
                        } else {
                            LOGGER.info("??????style???");
                            sews = styleSewingCraftWarehouseService.getDataByPartDetailIds(idList);
                        }
                        LOGGER.info("loop " + loop + " ???,?????????" + sews.size());
                        loop--;
                        if (loop < 0) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                    e.printStackTrace();

                }
            }

            if (null != sews && sews.size() > 0) {
                LOGGER.info("SIZE???" + sews.size());
                LOGGER.info("sews???" + JSONObject.toJSONString(sews));
                sews.stream().parallel().forEach(sew -> {
                    sew.setId(sew.getPartDetailId());
                    JSONObject obj = getDetailDataToJSONObject(sew, tableName, mainFrameCode, null, null);
                    //mapData.put("" + sew.getRandomCode() + sew.getPartDetailId(), obj);
                    addData(result, obj);
                });
                if (StringUtils.isNotEmpty(factoryCode) && !"order".equalsIgnoreCase(flag)) {
                    for (JSONObject obj : result) {
                        //?????????????????????????????????????????? ????????????=????????????*???????????????????????????????????????????????????????????????????????????
                        BigDecimal standardTime = obj.getBigDecimal("standardTime");
                        if (standardTime == null) {
                            standardTime = BigDecimal.ZERO;
                        }
                        BigDecimal minuWage = minueMap.get(factoryCode + "_" + obj.getString("craftGradeCode"));
                        if (minuWage == null) {
                            minuWage = BigDecimal.ZERO;
                        }
                        BigDecimal standardPrice = standardTime.multiply(minuWage).setScale(3, BigDecimal.ROUND_HALF_UP);
                        obj.put("standardPrice", standardPrice);
                    }
                }
            }

        } else { //???codeList???????????????????????????????????????????????????????????????????????????
            String codeList = dataObj.getString("codeList");
            LOGGER.info("codeList???" + codeList);
            if (StringUtils.isEmpty(codeList)) {
                return Result.build(MessageConstant.PARAM_NULL, "codeList????????????");
            }
            JSONArray codeArray = JSONArray.parseArray(codeList);
            List<String> codes = new ArrayList<>(0);
            List<String> bigStyleAnalyseCodes = new ArrayList<>(0);
            List<String> productionTicketNos = new ArrayList<>(0);
            Map<String, String> styleMap = new HashMap<>();
            final Map<String, String> map = new HashMap<>();
            for (int i = 0; i < codeArray.size(); i++) {
                JSONObject obj = codeArray.getJSONObject(i);
                codes.add(obj.getString("code"));
                bigStyleAnalyseCodes.add(obj.getString("bigStyleAnalyseCode"));
                map.put(obj.getString("code"), obj.getString("patternSymmetry"));
                String key = obj.getString("bigStyleAnalyseCode") + obj.getString("code");
                //????????????????????????????????????
                if (StringUtils.isNotEmpty(obj.getString("productionTicketNo"))) {
                    productionTicketNos.add(obj.getString("productionTicketNo"));
                    key = obj.getString("productionTicketNo") + obj.getString("code");
                }
                styleMap.put(key, key);
            }
            //?????????????????????????????????????????????????????????,bigstyle ?????????????????????
            if ("bigstyle".equalsIgnoreCase(searchType)) {
                List<SewingCraftWarehouse> tempList = styleSewingCraftWarehouseService.getDataByBigStyleAnalyseCodeList(bigStyleAnalyseCodes);
                if (null != tempList && tempList.size() > 0) {
                    for (SewingCraftWarehouse vo : tempList) {
                        String key = vo.getBigStyleAnalyseCode() + vo.getCraftCode();
                        if (styleMap.containsKey(key)) {
                            sewingCraftWarehouseList.add(vo);
                        }
                    }
                }
                // sewingCraftWarehouseList = styleSewingCraftWarehouseService.getDataByCraftCodeList(codes, bigStyleAnalyseCode);
            } else if ("orderstyle".equalsIgnoreCase(searchType)) {//?????????????????????
                List<SewingCraftWarehouse> tempList = bigOrderSewingCraftWarehouseService.getDataByBigStyleAnalyseCodeList(productionTicketNos);
                if (null != tempList && tempList.size() > 0) {
                    for (SewingCraftWarehouse vo : tempList) {
                        String key = vo.getProductionTicketNo() + vo.getCraftCode();
                        if (styleMap.containsKey(key)) {
                            sewingCraftWarehouseList.add(vo);
                        }
                    }
                }
            } else {
                sewingCraftWarehouseList = sewingCraftWarehouseService.getDataByCraftCodeList(codes);
            }

            if (null != sewingCraftWarehouseList && sewingCraftWarehouseList.size() > 0) {
                final List<FabricGrade> fabricGradeList = fabricGradeService.getAllFabricGrade();
                final List<OrderGrade> orderGradeList = orderGradeService.getAllOrderGrade();
                for (SewingCraftWarehouse sew : sewingCraftWarehouseList) {
                    sew.setFabricFraction(fabricFraction);
                    //??????????????????
                    //????????????????????????????????????????????????????????????????????????????????????A??? ???X?????? ??? B?????????X????????????????????????????????????????????????B?????????X??????????????????????????????0???
                    if ("bigstyle".equalsIgnoreCase(searchType) || "orderstyle".equalsIgnoreCase(searchType)) {
                        sew.setTimeSupplement(null);
                    }
                    if (sew.getMotionsList() != null) {
                        BigDecimal sewLength = calSewLength(sew.getMotionsList());
                        sew.setSewingLength(sewLength.intValue());
                    }
                    String tableName = "bigstyle".equalsIgnoreCase(searchType) ? BusinessConstants.TableName.BIG_STYLE_ANALYSE : BusinessConstants.TableName.SEWING_CRAFT;
                    if ("orderstyle".equalsIgnoreCase(searchType)) {
                        tableName = BusinessConstants.TableName.BIG_ORDER;
                    }
                    JSONObject obj = getDetailDataToJSONObject(sew, tableName, mainFrameCode, fabricGradeList, orderGradeList);
                    obj.put("patternSymmetry", map.get(sew.getCraftCode()));
                    //????????????????????????????????????????????????????????????
                    obj.put("baseStandardTime", obj.getString("standardTime"));
                    obj.put("baseStandardPrice", obj.getString("standardPrice"));
                    //??????????????????????????????
                    //if (StringUtils.isEmpty(searchType)) {
                    if (StringUtils.isNotEmpty(factoryCode)) {
                        obj.put("factoryCode", factoryCode);
                    }
                    sewingCraftWarehouseService.calStandTimeAndPrice(obj);
                    // }
                    result.add(obj);
                }

            }
        }

        for (JSONObject obj : result) {
            //?????????????????????????????????????????? ????????????=????????????*???????????????????????????????????????????????????????????????????????????
            if (StringUtils.isNotEmpty(factoryCode)) {
                BigDecimal standardTime = obj.getBigDecimal("baseStandardTime");
                if (standardTime == null) {
                    standardTime = BigDecimal.ZERO;
                }
                BigDecimal minuWage = minueMap.get(factoryCode + "_" + obj.getString("craftGradeCode"));
                if (minuWage == null) {
                    minuWage = BigDecimal.ZERO;
                }
                BigDecimal standardPrice = standardTime.multiply(minuWage).setScale(3, BigDecimal.ROUND_HALF_UP);
                obj.put("baseStandardPrice", standardPrice);
            }
        }
        return Result.ok(MessageConstant.SUCCESS, result);
    }

    private synchronized void addData(List<JSONObject> result, JSONObject obj) {
        result.add(obj);
    }

    private BigDecimal calSewLength(List<SewingCraftAction> list) {
        if (null == list || list.size() == 0) {
            return BigDecimal.ZERO;
        }
        //??????????????????
        //????????????=?????????????????????M???????????????????????????????????????*????????????,???M??????????????????0
        BigDecimal sewLength = BigDecimal.ZERO;
        for (SewingCraftAction action : list) {
            BigDecimal frequency = BigDecimal.ONE;
            BigDecimal frequencyObj = action.getFrequency();
            if (null != frequencyObj) {
                frequency = frequencyObj;
            }
            String code = action.getMotionCode();
            if (!code.startsWith("M") || code.indexOf("=") != -1) {
                sewLength = sewLength.add(BigDecimal.ZERO);
            } else {
                sewLength = sewLength.add(frequency.multiply(new BigDecimal(code.substring(1, 3))).setScale(3, BigDecimal.ROUND_HALF_UP));
            }
        }
        return sewLength;
    }

    /**
     * ????????????????????????????????????????????????
     */
    @RequestMapping(value = "/calSomeData", method = RequestMethod.GET)
    @ApiOperation(value = "calSomeData", notes = "????????????????????????????????????????????????")
    public Result<JSONObject> calSomeData(@RequestParam(value = "planCode", required = true) String planCode,
                                          @RequestParam(value = "fabricFraction", required = true) Integer fabricFraction) {
        List<FabricGrade> fabricGradeList = fabricGradeService.getAllFabricGrade();
        List<OrderGrade> orderGradeList = orderGradeService.getAllOrderGrade();
        JSONObject data = calData(planCode, fabricFraction, fabricGradeList, orderGradeList);
        return Result.ok(MessageConstant.SUCCESS, data);
    }

    private JSONObject calData(String planCode, Integer
            fabricFraction, List<FabricGrade> fabricGradeList, List<OrderGrade> orderGradeList) {
        //??????????????????
        //????????????--???????????????0
        String fabricGradeCode = "0";
        List<FabricScore> fabricScores = fabricScoreService.getFabricScoreByPlanCode(planCode);
        if (null != fabricScores && fabricScores.size() > 0) {
            for (FabricScore score : fabricScores) {
                if (score.getMinValue() <= fabricFraction && score.getMaxValue() >= fabricFraction) {
                    fabricGradeCode = score.getFabricGradeCode();
                }
            }
        }
        //??????????????????
        BigDecimal coefficient = BigDecimal.ZERO;
        if (null != fabricGradeList && fabricGradeList.size() > 0) {
            for (FabricGrade fabricGrade : fabricGradeList) {
                if (fabricGrade.getFabricGradeCode().equals(fabricGradeCode)) {
                    coefficient = fabricGrade.getCoefficient();
                }
            }
        }
        //????????????
        String orderGrade = "0";
        if (null != orderGradeList && orderGradeList.size() > 0) {
            for (OrderGrade order : orderGradeList) {
                if (order.getMinValue() <= fabricFraction && order.getMaxValue() >= fabricFraction) {
                    orderGrade = order.getOrderCode();
                }
            }
        }
        JSONObject result = new JSONObject();
        try {
            result.put("orderGrade", Integer.parseInt(orderGrade));
            result.put("fabricGradeCode", Integer.parseInt(fabricGradeCode));
            result.put("fabricRatio", coefficient);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * ??????????????????????????????JSONObject
     */
    private JSONObject getDetailDataToJSONObject(SewingCraftWarehouse sewingCraftWarehouse, String
            tableName, String mainFrameCode, List<FabricGrade> fabricGradeList, List<OrderGrade> orderGradeList) {
        //??????????????????????????????????????????????????????????????? ???????????????????????????????????????????????????list
        changeCategoryAndPartDataToList(sewingCraftWarehouse);
        try {
            if (null != orderGradeList && null != fabricGradeList) {
                JSONObject obj = calData(sewingCraftWarehouse.getFabricScorePlanCode(), sewingCraftWarehouse.getFabricFraction(), fabricGradeList, orderGradeList);
                sewingCraftWarehouse.setFabricRatio(obj.getBigDecimal("fabricRatio"));
                sewingCraftWarehouse.setFabricGradeCode(obj.getInteger("fabricGradeCode"));
                sewingCraftWarehouse.setOrderGrade(obj.getInteger("orderGrade"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        final JSONObject result = JSONObject.parseObject(JSONObject.toJSONString(sewingCraftWarehouse));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (sewingCraftWarehouse.getReleaseTime() != null) {
            result.put("releaseTime", sdf.format(sewingCraftWarehouse.getReleaseTime()));
        }
        if (sewingCraftWarehouse.getCreateTime() != null) {
            result.put("createTime", sdf.format(sewingCraftWarehouse.getCreateTime()));
        }
        if (sewingCraftWarehouse.getUpdateTime() != null) {
            result.put("updateTime", sdf.format(sewingCraftWarehouse.getUpdateTime()));
        }
        Map<String, User> map = userService.getUserMap();
        if (map.containsKey(sewingCraftWarehouse.getReleaseUser())) {
            result.put("releaseUserName", map.get(sewingCraftWarehouse.getReleaseUser()).getUserName());
        }
        if (map.containsKey(sewingCraftWarehouse.getCreateUser())) {
            result.put("createUserName", map.get(sewingCraftWarehouse.getCreateUser()).getUserName());
        } else {
            result.put("createUserName", "");
        }
        if (map.containsKey(sewingCraftWarehouse.getUpdateUser())) {
            result.put("updateUserName", map.get(sewingCraftWarehouse.getUpdateUser()).getUserName());
        } else {
            result.put("updateUserName", "");
        }
        if (map.containsKey(sewingCraftWarehouse.getReleaseUser())) {
            result.put("releaseUserName", map.get(sewingCraftWarehouse.getReleaseUser()).getUserName());
        } else {
            result.put("releaseUserName", "");
        }
        result.put("craftCategoryList", sewingCraftWarehouse.getCraftCategoryList());
        result.put("craftPartList", sewingCraftWarehouse.getCraftPartList());
        result.put("timeSupplement", sewingCraftWarehouse.getTimeSupplement());
        result.put("fabricFraction", sewingCraftWarehouse.getFabricFraction());
        result.put("fabricGradeCode", sewingCraftWarehouse.getFabricGradeCode());
        result.put("orderGrade", sewingCraftWarehouse.getOrderGrade());
        result.put("fabricRatio", sewingCraftWarehouse.getFabricRatio());
        Long code = sewingCraftWarehouse.getRandomCode();

        final Map<String, Object> param = new HashMap<>();
        param.put("sewingCraftRandomCode", code);
        param.put("mainFrameCode", mainFrameCode);


        // CountDownLatch countDownLatch = new CountDownLatch(4);
        //???????????????????????????
        //taskExecutor.execute(() -> {
        try {
            List<SewingCraftAction> motionsListTmp = new ArrayList<>();
            if (BusinessConstants.TableName.SEWING_CRAFT.equals(tableName)) {
                motionsListTmp = sewingCraftActionService.getDataBySewingCraftRandomCode(code);
            } else if (BusinessConstants.TableName.BIG_STYLE_ANALYSE.equals(tableName)) {
                motionsListTmp = styleSewingCraftActionService.getDataBySewingCraftRandomCodeAndCraftCode(code, sewingCraftWarehouse.getCraftCode());
            } else if (BusinessConstants.TableName.BIG_ORDER.equals(tableName)) {
                motionsListTmp = bigOrderSewingCraftActionService.getDataBySewingCraftRandomCodeAndCraftCode(code, sewingCraftWarehouse.getCraftCode());
            }
            result.put("motionsList", motionsListTmp);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //countDownLatch.countDown();
        }
        // });
        //???????????????????????????
        // taskExecutor.execute(() -> {
        try {
            List<SewingCraftPartPosition> sewPartPositionListTmp = new ArrayList<>();
            if (BusinessConstants.TableName.SEWING_CRAFT.equals(tableName)) {
                sewPartPositionListTmp = sewingCraftPartPositionService.getDataBySewingCraftRandomCode(code);
            } else if (BusinessConstants.TableName.BIG_STYLE_ANALYSE.equals(tableName)) {
                sewPartPositionListTmp = styleSewingCraftPartPositionService.getDataBySewingCraftRandomCodeAndCraftCode(code, sewingCraftWarehouse.getCraftCode());
            } else if (BusinessConstants.TableName.BIG_ORDER.equals(tableName)) {
                sewPartPositionListTmp = bigOrderSewingCraftPartPositionService.getDataBySewingCraftRandomCodeAndCraftCode(code, sewingCraftWarehouse.getCraftCode());
            }
            result.put("sewPartPositionList", sewPartPositionListTmp);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // countDownLatch.countDown();
        }
        // });
        //??????????????????
        // taskExecutor.execute(() -> {
        try {
            List<SewingCraftWarehouseWorkplace> workplaceCraftListTmp = new ArrayList<>();
            if (BusinessConstants.TableName.SEWING_CRAFT.equals(tableName)) {
                workplaceCraftListTmp = sewingCraftWarehouseWorkplaceService.getDataByParam(param);
                //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                if (null != workplaceCraftListTmp && workplaceCraftListTmp.size() > 0) {
                    for (SewingCraftWarehouseWorkplace work : workplaceCraftListTmp) {
                        if (StringUtils.isNotEmpty(work.getMainFrameCode()) && work.getMainFrameCode().equals(mainFrameCode)) {
                            result.put("craftNo", work.getCraftFlowNum());
                        }
                    }
                }
            } else if (BusinessConstants.TableName.BIG_STYLE_ANALYSE.equals(tableName)) {
                workplaceCraftListTmp = styleSewingCraftWarehouseWorkplaceService.getDataByParamAndCraftCode(code, sewingCraftWarehouse.getCraftCode());
            } else if (BusinessConstants.TableName.BIG_ORDER.equals(tableName)) {
                workplaceCraftListTmp = bigOrderSewingCraftWarehouseWorkplaceService.getDataByParamAndCraftCode(code, sewingCraftWarehouse.getCraftCode());
            }
            result.put("workplaceCraftList", workplaceCraftListTmp);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // countDownLatch.countDown();
        }
        // });
        //???????????????????????????????????????????????????
        // taskExecutor.execute(() -> {
        try {
            List<SewingCraftStd> sewingCraftStdsTmp = new ArrayList<>();
            if (BusinessConstants.TableName.SEWING_CRAFT.equals(tableName)) {
                sewingCraftStdsTmp = sewingCraftStdService.getDataBySewingCraftRandomCode(code);
            } else if (BusinessConstants.TableName.BIG_STYLE_ANALYSE.equals(tableName)) {
                sewingCraftStdsTmp = styleSewingCraftStdService.getDataBySewingCraftRandomCodeAndCraftCode(code, sewingCraftWarehouse.getCraftCode());
            } else if (BusinessConstants.TableName.BIG_ORDER.equals(tableName)) {
                sewingCraftStdsTmp = bigOrderSewingCraftStdService.getDataBySewingCraftRandomCodeAndCraftCode(code, sewingCraftWarehouse.getCraftCode());
            }
            List<CraftStd> craftStdList = new ArrayList<>();
            if (null != sewingCraftStdsTmp && sewingCraftStdsTmp.size() > 0) {
                for (SewingCraftStd std : sewingCraftStdsTmp) {
                    if (StringUtils.isNotEmpty(std.getCraftStdCode())) {
                        List<CraftStd> data = getCraftStdByParam(std.getCraftStdCode(), null, null, null, null, null, null);
                        if (data != null && data.size() > 0) {
                            craftStdList.addAll(data);
                        }
                    }

                }
            }
            //????????????????????????????????????????????????GST??????????????????????????????????????????????????????????????????????????????????????????
//????????????????????????????????????????????????
//            if (null == craftStdList || craftStdList.size() == 0) {
//                if (StringUtils.isNotEmpty(sewingCraftWarehouse.getMakeDescription())) {
//                    CraftStd craftStd = new CraftStd();
//                    craftStd.setMakeDesc(sewingCraftWarehouse.getMakeDescription());
//                    craftStd.setRequireQuality(sewingCraftWarehouse.getQualitySpec());
//                    craftStdList.add(craftStd);
//                }
//            }
            result.put("craftStdList", craftStdList);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //countDownLatch.countDown();
        }
        // });

        /*try {
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return result;
    }

    /**
     * ??????????????????????????????????????????????????????????????? ???????????????????????????????????????????????????list
     */
    private void changeCategoryAndPartDataToList(SewingCraftWarehouse sewingCraftWarehouse) {
        if (null == sewingCraftWarehouse) {
            return;
        }
        String craftCategoryCode = sewingCraftWarehouse.getCraftCategoryCode();
        String craftCategoryName = sewingCraftWarehouse.getCraftCategoryName();

        List<JSONObject> craftCategoryList = new ArrayList<>();
        if (null != craftCategoryCode && null != craftCategoryName) {
            String[] code_cat = craftCategoryCode.split(",");
            String[] name_cat = craftCategoryName.split(",");
            if (code_cat.length == name_cat.length) {
                for (int i = 0; i < code_cat.length; i++) {
                    JSONObject cat = new JSONObject();
                    cat.put("craftCategoryCode", code_cat[i]);
                    cat.put("craftCategoryName", name_cat[i]);
                    craftCategoryList.add(cat);
                }
            }
        }
        List<JSONObject> partList = new ArrayList<>();
        String craftPartName = sewingCraftWarehouse.getCraftPartName();
        String craftPartCode = sewingCraftWarehouse.getCraftPartCode();
        if (null != craftPartName && null != craftPartCode) {
            String[] code_part = craftPartCode.split(",");
            String[] name_part = craftPartName.split(",");
            if (code_part.length == name_part.length) {
                for (int i = 0; i < code_part.length; i++) {
                    JSONObject part = new JSONObject();
                    part.put("craftPartCode", code_part[i]);
                    part.put("craftPartName", name_part[i]);
                    partList.add(part);
                }
            }
        }
        sewingCraftWarehouse.setCraftCategoryList(craftCategoryList);
        sewingCraftWarehouse.setCraftPartList(partList);
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "getAll", notes = "????????????????????????")
    public Result<List<SewingCraftWarehouse>> getAll(@RequestParam(defaultValue = "1") Integer page,
                                                     @RequestParam(defaultValue = "30") Integer rows,
                                                     @RequestParam(name = "keywords", required = false) String keywords,
                                                     @RequestParam(name = "craftCode", required = false) String craftCode,
                                                     @RequestParam(name = "craftName", required = false) String craftName,
                                                     @RequestParam(name = "description", required = false) String description,
                                                     @RequestParam(name = "craftCategoryCode", required = false) String craftCategoryCode,
                                                     @RequestParam(name = "craftPartCode", required = false) String craftPartCode,
                                                     @RequestParam(name = "mainFrameCode", required = false) String mainFrameCode,
                                                     @RequestParam(name = "createUser", required = false) String createUser,
                                                     @RequestParam(name = "releaseUser", required = false) String releaseUser,
                                                     @RequestParam(name = "createTimeBeginDate", required = false) String createTimeBeginDate,
                                                     @RequestParam(name = "createTimeEndDate", required = false) String createTimeEndDate,
                                                     @RequestParam(name = "releaseTimeBeginDate", required = false) String releaseTimeBeginDate,
                                                     @RequestParam(name = "releaseTimeEndDate", required = false) String releaseTimeEndDate,
                                                     @RequestParam(name = "status", required = false) Integer status) {
        List<User> users = userService.getAllUser();
        List<String> createUserList = new ArrayList<>();
        List<String> releaseUserList = new ArrayList<>();
        for (User user : users) {
            String name = user.getUserName();
            if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(createUser) && name.indexOf(createUser) != -1) {
                createUserList.add(user.getUserCode());
            }
            if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(releaseUser) && name.indexOf(releaseUser) != -1) {
                releaseUserList.add(user.getUserCode());
            }
        }
        PageHelper.startPage(page, rows);
        if (StringUtils.isNotEmpty(keywords)) {
            keywords = StringUtils.replaceBlank(keywords);
        }
        if (StringUtils.isNotEmpty(craftCode)) {
            craftCode = StringUtils.replaceBlank(craftCode);
        }
        if (StringUtils.isNotEmpty(craftName)) {
            craftName = StringUtils.replaceBlank(craftName);
        }
        if (StringUtils.isNotEmpty(description)) {
            description = StringUtils.replaceBlank(description);
        }

        List<SewingCraftWarehouse> data = sewingCraftWarehouseService.getDataByPager(keywords, craftCode, craftName, description, craftCategoryCode, craftPartCode, status, mainFrameCode, createUserList, releaseUserList, createTimeBeginDate, createTimeEndDate, releaseTimeBeginDate, releaseTimeEndDate);
        PageInfo<SewingCraftWarehouse> pageInfo = new PageInfo<>(data);
        return Result.ok(data, pageInfo.getTotal());
    }

    @RequestMapping(value = "/searchSewingCraftWarehouse", method = RequestMethod.GET)
    @ApiOperation(value = "searchSewingCraftWarehouse", notes = "?????????????????????")
    public Result<List<SewingCraftWarehouse>> searchSewingCraftWarehouse(@RequestParam(defaultValue = "1") Integer
                                                                                 page,
                                                                         @RequestParam(defaultValue = "30") Integer rows,
                                                                         @RequestParam(name = "craftPartCode", required = false) String craftPartCode,
                                                                         @RequestParam(name = "mainFrameCode", required = false) String mainFrameCode) {
        PageHelper.startPage(page, rows);
        LOGGER.info("???????????????????????????: page" + page + " rows:" + rows + " craftPartCode:" + craftPartCode + " mainFrameCode:" + mainFrameCode);
        Map<String, Object> map = new HashMap<>();
        map.put("mainFrameCode", mainFrameCode);
        map.put("craftPartCode", craftPartCode);
        List<SewingCraftWarehouse> data = sewingCraftWarehouseService.getDataByMainFramePartCode(map);
        PageInfo<SewingCraftWarehouse> pageInfo = new PageInfo<>(data);
        return Result.ok(data, pageInfo.getTotal());
    }

    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @ApiOperation(value = "updateStatus", notes = "??????????????????")
    public Result<JSONObject> updateStatus(@RequestParam(name = "randomCode", required = true) String randomCode,
                                           @RequestParam(name = "status", required = true) Integer status,
                                           @RequestParam(name = "userCode", required = false) String userCode) throws Exception {
        final Long randomCodeLong = Long.parseLong(randomCode);
        SewingCraftWarehouse craft = sewingCraftWarehouseService.getDataByRandom(randomCodeLong);
        if (null != craft) {
            if (BusinessConstants.Status.IN_VALID.equals(status)) {
                String used = sewingCraftWarehouseService.getCraftInUsed(craft.getCraftCode());
                if (StringUtils.isNotBlank(used)) {
                    return Result.build(MessageConstant.PROGRAM_ERROR, "?????????????????????????????????");
                }
            }

            if (sewingCraftWarehouseService.updateStatus(status, randomCodeLong, userCode)) {
                JSONObject obj = new JSONObject();
                obj.put("randomCode", randomCode);
                obj.put("status", status);
                if (BusinessConstants.Status.PUBLISHED_STATUS.equals(status)) {
                    taskExecutor.execute(() -> {
                        SewingCraftWarehouse house = sewingCraftWarehouseService.getDataByRandom(randomCodeLong);
                        //???????????????????????????????????????
                        sewingCraftWarehouseService.updateRelateCraftInfo(house.getCraftCode());
                        List<SewingCraftWarehouse> list = new ArrayList<>(0);
                        if (null != house) {
                            list.add(house);
                        }
                        sendSewListToPi(list);
                    });
                }
                return Result.ok(MessageConstant.SUCCESS, obj);
            }
        }
        return Result.ok(MessageConstant.SERVER_FIELD_ERROR, new JSONObject());
    }

    @ApiOperation(value = "getCraftInUsed", notes = "???????????????????????????", httpMethod = "GET", response = Result.class)
    @RequestMapping(value = "/getCraftInUsed", method = RequestMethod.GET)
    public Result getCraftInUsed(
            @RequestParam(name = "craftCode", required = true) String craftCode) throws Exception {
        String used = sewingCraftWarehouseService.getCraftInUsed(craftCode);
        if (null == used) {
            used = "";
        }
        return Result.ok(MessageConstant.SUCCESS, used);
    }

    @RequestMapping(value = "/addOrUpdate", method = RequestMethod.POST)
    @ApiOperation(value = "addOrUpdate", notes = "????????????????????????????????????")
    //@Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<JSONObject> update(HttpServletRequest request) throws Exception {
        String data = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        if (StringUtils.isEmpty(data)) {
            return Result.build(MessageConstant.PARAM_NULL, "????????????");
        }
        LogUtil.insertBigStyleOperationToLOG(data, "sewing_craft_warehouse");
        LOGGER.info("addOrUpdate??????????????????????????????" + data);
        JSONObject dataObj = JSONObject.parseObject(data);
        //????????????
        Result<JSONObject> paramCheckResult = checkParam(dataObj);
        if (MessageConstant.PARAM_NULL.equals(paramCheckResult.getCode())) {
            LOGGER.error("???????????????" + JSONObject.toJSONString(paramCheckResult));
            return paramCheckResult;
        }
        //??????????????????????????????randomCode???craftCode?????????????????????????????????
        //randomCode
        String randomCode = dataObj.getString("randomCode");
        //craftCode
        String craftCode = dataObj.getString("craftCode");
        String operation = BusinessConstants.Send2Pi.actionType_create;
        if (StringUtils.isNotEmpty(randomCode) && StringUtils.isNotEmpty(craftCode)) {
            LOGGER.info("------??????????????????????????????update--------");
            operation = BusinessConstants.Send2Pi.actionType_update;
        }

        JSONObject result = sewingCraftWarehouseService.addOrUpdataSewingCraft(null, dataObj, operation, BusinessConstants.TableName.SEWING_CRAFT, 1);
        if (StringUtils.isNotEmpty(result.getString("craftCode")) && result.getLong("randomCode") != null) {
            return Result.ok(MessageConstant.SUCCESS, result);
        } else {
            return Result.ok(MessageConstant.SERVER_FIELD_ERROR, new JSONObject());
        }
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     */
    private void updatePartCraft(String craftCode) {
        HashMap param = new HashMap();
        param.put("params", craftCode);
        List<SewingCraftVo> list = sewingCraftWarehouseWorkplaceDao.searchSewingCraftData(param);
        if (null != list && list.size() > 0) {
            for (SewingCraftVo vo : list) {

            }
        }
    }

    @RequestMapping(value = "/generateSewingCraftCode", method = RequestMethod.POST)
    @ApiOperation(value = "generateSewingCraftCode", notes = "??????????????????")
    public Result<JSONObject> generateSewingCraftCode(HttpServletRequest request) throws Exception {
        String data = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        if (StringUtils.isEmpty(data)) {
            return Result.build(MessageConstant.PARAM_NULL, "????????????");
        }
        JSONObject dataObj = JSONObject.parseObject(data);
        String code = sewingCraftWarehouseService.justGenerateSewingCraftCode(dataObj);
        JSONObject result = new JSONObject();
        result.put("craftCode", code);
        result.put("randomCode", SnowflakeIdUtil.generateId());
        return Result.ok(MessageConstant.SUCCESS, result);
    }

    @RequestMapping(value = "/saveBigStyleSingleCraft", method = RequestMethod.POST)
    @ApiOperation(value = "saveBigStyleSingleCraft", notes = "???????????????????????????????????????")
    public Result<JSONObject> saveBigStyleSingleCraft(HttpServletRequest request) throws Exception {
        String data = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        if (StringUtils.isEmpty(data)) {
            return Result.build(MessageConstant.PARAM_NULL, "????????????");
        }
        JSONObject dataObj = JSONObject.parseObject(data);
        //????????????
        Result<JSONObject> paramCheckResult = checkParam(dataObj);
        if (MessageConstant.PARAM_NULL.equals(paramCheckResult.getCode())) {
            LOGGER.error("???????????????" + JSONObject.toJSONString(paramCheckResult));
            return paramCheckResult;
        }
        String partCraftMainCode = dataObj.getString("partCraftMainCode");
        if (StringUtils.isEmpty(partCraftMainCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "??????????????????");
        }
        Long bigStyleRandomCode = dataObj.getLong("bigStyleRandomCode");
        if (null == bigStyleRandomCode) {
            return Result.build(MessageConstant.PARAM_NULL, "?????????????????????randomCode??????");
        }

        JSONObject result = sewingCraftWarehouseService.saveBigStyleSingleCraft(partCraftMainCode, dataObj, bigStyleRandomCode, BusinessConstants.TableName.BIG_STYLE_ANALYSE);
        if (StringUtils.isNotEmpty(result.getString("craftCode")) && result.getLong("randomCode") != null) {
            try {
                JSONArray detailList = dataObj.getJSONArray("partCraftDetailList");
                Map<String, User> userMap = userService.getUserMap();
                String userCode = dataObj.getString("userCode");
                String userName = userMap.containsKey(userCode) ? userMap.get(userCode).getUserName() : "";
                if (null != detailList && detailList.size() > 0) {
                    for (int i = 0; i < detailList.size(); i++) {
                        JSONObject obj = detailList.getJSONObject(i);

                        BigStyleAnalysePartCraftDetail bigStyleAnalysePartCraftDetail = JSONObject.parseObject(obj.toJSONString(), BigStyleAnalysePartCraftDetail.class);
                        List<Long> ids = bigStyleAnalysePartCraftDetailDao.getIdsByStyleRandomCodeAndPartCraftMainCodeAndCraftCode(bigStyleRandomCode, partCraftMainCode, result.getString("craftCode"));
                        if (null != bigStyleAnalysePartCraftDetail) {
                            bigStyleAnalysePartCraftDetail.setPartCraftMainCode(partCraftMainCode);
                            bigStyleAnalysePartCraftDetail.setCraftCode(result.getString("craftCode"));
                            bigStyleAnalysePartCraftDetail.setCraftName(dataObj.getString("craftName"));
                            bigStyleAnalysePartCraftDetail.setCraftRemark(dataObj.getString("description"));
                            bigStyleAnalysePartCraftDetail.setMachineCode(dataObj.getString("machineCode"));
                            bigStyleAnalysePartCraftDetail.setStandardTime(dataObj.getBigDecimal("standardTime"));
                            bigStyleAnalysePartCraftDetail.setStandardPrice(dataObj.getBigDecimal("standardPrice"));
                            bigStyleAnalysePartCraftDetail.setStyleRandomCode(bigStyleRandomCode);
                            bigStyleAnalysePartCraftDetail.setUpdateUser(userName);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            try {
                                if (StringUtils.isNotEmpty(dataObj.getString("updateTime"))) {
                                    bigStyleAnalysePartCraftDetail.setTimeUpdate(sdf.parse(dataObj.getString("updateTime")));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        //??????????????????
                        if (null == ids || ids.size() == 0) {
                            List<BigStyleAnalysePartCraftDetail> partCraftDetails = new ArrayList<>();
                            partCraftDetails.add(bigStyleAnalysePartCraftDetail);
                            bigStyleAnalysePartCraftDetailDao.insertPartCraftDetailForeach(partCraftDetails);
                        } else {//????????????????????????
                            bigStyleAnalysePartCraftDetail.setId(ids.get(0));
                            bigStyleAnalysePartCraftDetailDao.updatePartCraftDetail(bigStyleAnalysePartCraftDetail);
                        }

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return Result.ok(MessageConstant.SUCCESS, result);
        } else {
            return Result.ok(MessageConstant.SERVER_FIELD_ERROR, new JSONObject());
        }
    }


    @RequestMapping(value = "/saveOrderSingleCraft", method = RequestMethod.POST)
    @ApiOperation(value = "saveOrderSingleCraft", notes = "???????????????????????????????????????")
    public Result<JSONObject> saveOrderSingleCraft(HttpServletRequest request) throws Exception {
        String data = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        if (StringUtils.isEmpty(data)) {
            return Result.build(MessageConstant.PARAM_NULL, "????????????");
        }
        JSONObject dataObj = JSONObject.parseObject(data);
        //????????????
        Result<JSONObject> paramCheckResult = checkParam(dataObj);
        if (MessageConstant.PARAM_NULL.equals(paramCheckResult.getCode())) {
            LOGGER.error("???????????????" + JSONObject.toJSONString(paramCheckResult));
            return paramCheckResult;
        }
        String partCraftMainCode = dataObj.getString("partCraftMainCode");
        if (StringUtils.isEmpty(partCraftMainCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "??????????????????");
        }
        Long orderRandomCode = dataObj.getLong("orderRandomCode");
        if (null == orderRandomCode) {
            return Result.build(MessageConstant.PARAM_NULL, "?????????????????????randomCode??????");
        }
        JSONObject result = sewingCraftWarehouseService.saveBigStyleSingleCraft(partCraftMainCode, dataObj, orderRandomCode, BusinessConstants.TableName.BIG_ORDER);
        if (StringUtils.isNotEmpty(result.getString("craftCode")) && result.getLong("randomCode") != null) {
            //List<BigStyleAnalysePartCraftDetail> partCraftDetails
            try {
                JSONArray detailList = dataObj.getJSONArray("partCraftDetailList");
                Map<String, User> userMap = userService.getUserMap();
                String userCode = dataObj.getString("userCode");
                String userName = userMap.containsKey(userCode) ? userMap.get(userCode).getUserName() : "";
                if (null != detailList && detailList.size() > 0) {
                    for (int i = 0; i < detailList.size(); i++) {
                        JSONObject obj = detailList.getJSONObject(i);
                        BigStyleAnalysePartCraftDetail bigStyleAnalysePartCraftDetail = JSONObject.parseObject(obj.toJSONString(), BigStyleAnalysePartCraftDetail.class);
                        List<Long> ids = bigOrderPartCraftDetailDao.getIdsByStyleRandomCodeAndPartCraftMainCodeAndCraftCode(orderRandomCode, partCraftMainCode, result.getString("craftCode"));
                        if (null != bigStyleAnalysePartCraftDetail) {
                            bigStyleAnalysePartCraftDetail.setPartCraftMainCode(partCraftMainCode);
                            bigStyleAnalysePartCraftDetail.setCraftCode(result.getString("craftCode"));
                            bigStyleAnalysePartCraftDetail.setCraftName(dataObj.getString("craftName"));
                            bigStyleAnalysePartCraftDetail.setCraftRemark(dataObj.getString("description"));
                            bigStyleAnalysePartCraftDetail.setMachineCode(dataObj.getString("machineCode"));
                            bigStyleAnalysePartCraftDetail.setStandardTime(dataObj.getBigDecimal("standardTime"));
                            bigStyleAnalysePartCraftDetail.setStandardPrice(dataObj.getBigDecimal("standardPrice"));
                            bigStyleAnalysePartCraftDetail.setStyleRandomCode(orderRandomCode);
                            bigStyleAnalysePartCraftDetail.setUpdateUser(userName);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            try {
                                if (StringUtils.isNotEmpty(dataObj.getString("updateTime"))) {
                                    bigStyleAnalysePartCraftDetail.setTimeUpdate(sdf.parse(dataObj.getString("updateTime")));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        if (null == ids || ids.size() == 0) {
                            List<BigStyleAnalysePartCraftDetail> partCraftDetails = new ArrayList<>();
                            partCraftDetails.add(bigStyleAnalysePartCraftDetail);
                            bigOrderPartCraftDetailDao.insertPartCraftDetailForeach(partCraftDetails);
                        } else {
                            bigStyleAnalysePartCraftDetail.setId(ids.get(0));
                            bigOrderPartCraftDetailDao.updatePartCraftDetail(bigStyleAnalysePartCraftDetail);
                        }

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return Result.ok(MessageConstant.SUCCESS, result);
        } else {
            return Result.ok(MessageConstant.SERVER_FIELD_ERROR, new JSONObject());
        }
    }

    /**
     * ????????????
     * operationType ?????????????????????add???update????????????????????????update??????????????????randomCode???Id?????????
     */
    public Result<JSONObject> checkParam(JSONObject dataObj) {
        //????????????
        String userCode = dataObj.getString("userCode");
        if (StringUtils.isEmpty(userCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "??????????????????");
        }
        //????????????
        String status = dataObj.getString("status");
        if (StringUtils.isEmpty(status)) {
            return Result.build(MessageConstant.PARAM_NULL, "????????????");
        }
        JSONArray craftCategoryList = dataObj.getJSONArray("craftCategoryList");
        if (null == craftCategoryList || craftCategoryList.size() == 0) {
            return Result.build(MessageConstant.PARAM_NULL, "??????????????????");
        }
        JSONArray craftPartList = dataObj.getJSONArray("craftPartList");
        if (null == craftPartList || craftPartList.size() == 0) {
            return Result.build(MessageConstant.PARAM_NULL, "??????????????????");
        }
      /* JSONArray workplaceCraftList = dataObj.getJSONArray("workplaceCraftList");
        if (null == workplaceCraftList || workplaceCraftList.size() == 0) {
            return Result.build(MessageConstant.PARAM_NULL, "??????????????????");
        }
        JSONArray sewPartPositionList = dataObj.getJSONArray("sewPartPositionList");
        if (null == sewPartPositionList || sewPartPositionList.size() == 0) {
            return Result.build(MessageConstant.PARAM_NULL, "??????????????????");
        }
        JSONArray motionsList = dataObj.getJSONArray("motionsList");
        if (null == motionsList || motionsList.size() == 0) {
            return Result.build(MessageConstant.PARAM_NULL, "??????????????????");
        }*/
        JSONArray motionsList = dataObj.getJSONArray("motionsList");
        if (null != motionsList && motionsList.size() > 0) {
            for (int i = 0; i < motionsList.size(); i++) {
                JSONObject obj = motionsList.getJSONObject(i);
                String str = obj.getString("motionName");
                if (str.length() > 500) {
                    return Result.build(MessageConstant.PARAM_NULL, "??????????????????");
                }
            }

        }


        //????????????
        String makeTypeCode = dataObj.getString("makeTypeCode");
        if (StringUtils.isEmpty(makeTypeCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "??????????????????");
        }
        //????????????
        String craftName = dataObj.getString("craftName");
        if (StringUtils.isEmpty(craftName)) {
            return Result.build(MessageConstant.PARAM_NULL, "??????????????????");
        }
        if (StringUtils.isNotBlank(craftName)) {
            if (craftName.length() > 500) {
                return Result.build(MessageConstant.PARAM_NULL, "??????????????????");
            }
        }

        //????????????
        String description = dataObj.getString("description");
        if (StringUtils.isEmpty(description)) {
            return Result.build(MessageConstant.PARAM_NULL, "??????????????????");
        }
        if (StringUtils.isNotBlank(description)) {
            if (description.length() > 500) {
                return Result.build(MessageConstant.PARAM_NULL, "??????????????????");
            }
        }

        //????????????
        String craftGradeCode = dataObj.getString("craftGradeCode");
        if (StringUtils.isEmpty(craftGradeCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "??????????????????");
        }
        //????????????
        String machineCode = dataObj.getString("machineCode");
        if (StringUtils.isEmpty(machineCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "??????????????????");
        }
        //??????????????????
        String allowanceCode = dataObj.getString("allowanceCode");
        if (StringUtils.isEmpty(allowanceCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "????????????????????????");
        }
        //??????????????????
        /*String strappingCode = dataObj.getString("strappingCode");
        if (StringUtils.isEmpty(strappingCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "????????????????????????");
        }*/
        //????????????
        String isFabricStyleFix = dataObj.getString("isFabricStyleFix");
        if (StringUtils.isEmpty(isFabricStyleFix)) {
            return Result.build(MessageConstant.PARAM_NULL, "????????????????????????");
        }
        //????????????
        /*JSONArray craftStdList = dataObj.getJSONArray("craftStdList");
        if (null == craftStdList || craftStdList.size() == 0) {
            return Result.build(MessageConstant.PARAM_NULL, "??????????????????");
        }*/

        //??????????????????
        String fabricScorePlanCode = dataObj.getString("fabricScorePlanCode");
        if (StringUtils.isEmpty(fabricScorePlanCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "????????????????????????");
        }

        //????????????
        String fixedTime = dataObj.getString("fixedTime");
        if (StringUtils.isEmpty(fixedTime)) {
            dataObj.put("fixedTime", "0");
        }
        //????????????
        String floatingTime = dataObj.getString("floatingTime");
        if (StringUtils.isEmpty(floatingTime)) {
            dataObj.put("floatingTime", "0");
        }
        //????????????
        String sewingLength = dataObj.getString("sewingLength");
        if (StringUtils.isEmpty(sewingLength)) {
            dataObj.put("sewingLength", "0");
        }
        //????????????
        String paramLength = dataObj.getString("paramLength");
        if (StringUtils.isEmpty(paramLength)) {
            dataObj.put("paramLength", "0");
        }
        //????????????
        String standardTime = dataObj.getString("standardTime");
        if (StringUtils.isEmpty(standardTime)) {
            dataObj.put("standardTime", "0");//???????????????0
        }
        //????????????
        String standardPrice = dataObj.getString("standardPrice");
        if (StringUtils.isEmpty(standardPrice)) {
            dataObj.put("standardPrice", "0");//???????????????0
        }
        return Result.build(MessageConstant.SUCCESS, "????????????OK");
    }

    /**
     * ??????????????????
     * param---?????????????????????
     */
    @ApiOperation(value = "searchPartPosition", notes = "???????????????????????????")
    @RequestMapping(value = "searchPartPosition", method = RequestMethod.GET)
    public Result<List<PartPosition>> searchPartPosition(@RequestParam(defaultValue = "1") Integer page,
                                                         @RequestParam(defaultValue = "30") Integer rows,
                                                         @RequestParam(required = false) String param) {
        PageHelper.startPage(page, rows);
        LOGGER.info("--------????????????????????????:" + param);
        JSONObject obj = new JSONObject();
        List<PartPosition> data = new ArrayList<>(0);
        data = partPositionService.getSewingPartPositions(param);
        LOGGER.info("--------????????????????????????:" + JSONObject.toJSONString(data));
        PageInfo<PartPosition> pageInfo = new PageInfo<>(data);
        return Result.build(MessageConstant.SUCCESS,
                MessageConstant.messageMap.get(MessageConstant.SUCCESS),
                pageInfo.getTotal(), data);
    }

    /**
     * ????????????ES??????
     */
    @ApiOperation(value = "searchWorkplaceCraft", notes = "????????????ES??????")
    @RequestMapping(value = "searchWorkplaceCraft", method = RequestMethod.GET)
    public Result<List<WorkplaceCraftEsBO>> searchWorkplaceCraft(@RequestParam(defaultValue = "1") Integer page,
                                                                 @RequestParam(defaultValue = "30") Integer rows,
                                                                 @RequestParam(required = false) String param) {
        LOGGER.info("--------????????????????????????:" + param);
        //PageHelper.startPage(page, rows);
        JSONObject obj = new JSONObject();
        List<WorkplaceCraftEsBO> data = new ArrayList<>(0);
        List<WorkplaceCraftEsBO> result = new ArrayList<>(0);
        if (StringUtils.isEmpty(param)) {
            //??????????????????????????????
            Iterable<WorkplaceCraftEsBO> esData = workplaceCraftESRepository.search(QueryBuilders.matchAllQuery());
            if (null != esData) {
                for (WorkplaceCraftEsBO bo : esData) {
                    data.add(bo);
                }
            }
            result = forPage(data, page, rows);
            return Result.build(MessageConstant.SUCCESS,
                    MessageConstant.messageMap.get(MessageConstant.SUCCESS),
                    new Long(data.size()), result);
        }
        try {
            //?????????????????????????????????????????????????????????????????????????????????????????????
            String par = param.toLowerCase();
            //?????????????????????????????????????????????????????????????????????????????????????????????????????????
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                    .should(QueryBuilders.wildcardQuery("workplaceCraftCode", "*" + par + "*"))//????????????
                    .should(QueryBuilders.matchQuery("workplaceCraftName", par))//??????????????????
                    .should(QueryBuilders.matchQuery("craftFlowNum", par));//?????????
            Iterable<WorkplaceCraftEsBO> esData = workplaceCraftESRepository.search(boolQueryBuilder);
            if (null != esData) {
                for (WorkplaceCraftEsBO bo : esData) {
                    data.add(bo);
                }
            }
            LOGGER.info("--------????????????????????????:" + JSONObject.toJSONString(data));

        } catch (Exception e) {
            LOGGER.info("--------??????????????????????????????:" + e.getMessage());
            return Result.build(MessageConstant.PROGRAM_ERROR,
                    MessageConstant.messageMap.get(MessageConstant.PROGRAM_ERROR),
                    0L, null);
        }
        result = forPage(data, page, rows);
        return Result.build(MessageConstant.SUCCESS,
                MessageConstant.messageMap.get(MessageConstant.SUCCESS),
                new Long(data.size()), result);

    }

    private List<WorkplaceCraftEsBO> forPage(List<WorkplaceCraftEsBO> data, Integer page, Integer rows) {
        //????????????
        int currIdx = (page > 1 ? (page - 1) * page : 0);
        List<WorkplaceCraftEsBO> result = new ArrayList<>(0);
        for (int i = 0; i < rows && i < data.size() - currIdx; i++) {
            WorkplaceCraftEsBO vo = data.get(currIdx + i);
            result.add(vo);
        }
        return result;
    }


    /**
     * ????????????ES??????
     */
    @ApiOperation(value = "searchCraftStd", notes = "????????????ES??????")
    @RequestMapping(value = "searchCraftStd", method = RequestMethod.GET)
    public Result<List<CraftStd>> searchCraftStd(@RequestParam(defaultValue = "1") Integer page,
                                                 @RequestParam(defaultValue = "30") Integer rows,
                                                 @RequestParam(required = false) String param,
                                                 @RequestParam(required = false) String styleCode,
                                                 @RequestParam(required = false) String craftStdCode,
                                                 @RequestParam(required = false) String craftStdName,
                                                 @RequestParam(required = false) String craftCategoryCode,
                                                 @RequestParam(name = "craftCategoryList", required = false) String craftCategoryList,
                                                 @RequestParam(required = false) String craftPartCode) {
        LOGGER.info("--------????????????????????????:" + param);
        PageHelper.startPage(page, rows);
        JSONObject obj = new JSONObject();
        List<CraftStd> data = getCraftStdByParam(param, craftCategoryCode, craftPartCode, craftCategoryList, styleCode, craftStdCode, craftStdName);
       /* List<CraftStdEsBO> data = new ArrayList<>(0);
        if (StringUtils.isEmpty(param)) {.e
            //??????????????????????????????
            Iterable<CraftStdEsBO> esData = craftStdRepository.search(QueryBuilders.matchAllQuery());
            if (null != esData) {
                for (CraftStdEsBO bo : esData) {
                    data.add(bo);
                }
            }
            return Result.build(MessageConstant.SUCCESS,
                    MessageConstant.messageMap.get(MessageConstant.SUCCESS),
                    new Long(data.size()), data);
        }
        try {
            //?????????????????????????????????????????????????????????????????????????????????????????????
            String par = param.toLowerCase();
            //?????????????????????????????????????????????????????????????????????????????????????????????????????????
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                    .should(QueryBuilders.wildcardQuery("craftStdCode", "*" + par + "*"))//??????????????????
                    .should(QueryBuilders.matchQuery("craftStdName", par));//??????????????????
            Iterable<CraftStdEsBO> esData = craftStdRepository.search(boolQueryBuilder);
            if (null != esData) {
                for (CraftStdEsBO bo : esData) {
                    data.add(bo);
                }
            }
            LOGGER.info("--------????????????????????????:" + JSONObject.toJSONString(data));

        } catch (Exception e) {
            LOGGER.info("--------??????????????????????????????:" + e.getMessage());
            return Result.build(MessageConstant.PROGRAM_ERROR,
                    MessageConstant.messageMap.get(MessageConstant.PROGRAM_ERROR),
                    0L, null);
        }*/
        PageInfo<CraftStd> pageInfo = new PageInfo<>(data);
        return Result.build(MessageConstant.SUCCESS,
                MessageConstant.messageMap.get(MessageConstant.SUCCESS),
                new Long(pageInfo.getTotal()), data);

    }

    private List<CraftStd> getCraftStdByParam(String param, String craftCategoryCode,
                                              String craftPartCode, String craftCategoryListStr,
                                              String styleCode, String craftStdCode, String craftStdName) {
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

        String[] statusArr = {Const.STD_STATUS_AUDIT, Const.STD_STATUS_OLD};
        map.put("param", param);
        if (StringUtils.isNotEmpty(styleCode)) {
            styleCode = StringUtils.replaceBlank(styleCode);
            map.put("styleCode", styleCode);
        }
        if (StringUtils.isNotEmpty(craftStdCode)) {
            craftStdCode = StringUtils.replaceBlank(craftStdCode);
            map.put("craftStdCode", craftStdCode);
        }
        if (StringUtils.isNotEmpty(craftStdName)) {
            craftStdName = StringUtils.replaceBlank(craftStdName);
            map.put("craftStdName", craftStdName);
        }
        map.put("status", statusArr);
        map.put("craftCategoryCode", craftCategoryCode);
        map.put("craftPartCode", craftPartCode);
        List<CraftStd> data = craftStdService.getCraftStdByLikeParam(map);
        if (null != data && data.size() > 0) {
            //data.stream().parallel().forEach(std -> {
            data.stream().forEach(std -> {
                //?????????
                List<String> pictureUrlList = new ArrayList<>(0);
                //??????
                List<String> vedioUrlList = new ArrayList<>(0);
                List<CraftFile> files = craftFileService.getByKeyId(std.getId());
                //????????????--??????????????????
                if (files != null && files.size() > 0) {
                    for (CraftFile file : files) {
                        //?????????
                        if (Const.RES_TYPE_HAND_IMG == file.getResourceType()) {
                            pictureUrlList.add(file.getFileUrl());
                        } else if (Const.RES_TYPE_STD_VIDEO == file.getResourceType()) {
                            vedioUrlList.add(file.getFileUrl());
                        }
                    }
                }
                //????????????
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

    @RequestMapping("/test")
    @ApiOperation(value = "test", notes = "??????")
    public String teset(@RequestParam("code") String code) {
        Long random = Long.parseLong(code);
        //sendDataToPi(random);
        return "ok";
    }

    @RequestMapping("/es_save")
    public Long esSave() {
        return SnowflakeIdUtil.generateId();
    }

    /**
     * ???????????????????????????????????????
     */
    @RequestMapping(value = "/calSewingPriceAndTime", method = RequestMethod.POST)
    @ApiOperation(value = "calSewingPriceAndTime", notes = "???????????????????????????????????????")
    public Result<JSONObject> calSewingPriceAndTime(HttpServletRequest request) throws Exception {
        String data = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        if (StringUtils.isEmpty(data)) {
            return Result.build(MessageConstant.PARAM_NULL, "????????????");
        }
        LOGGER.info("???????????????????????????????????????????????????" + data);
        JSONObject result = new JSONObject();
        try {
            JSONObject dataObj = JSONObject.parseObject(data);
            String uiRandomCode = dataObj.getString("uiRandomCode");
            sewingCraftWarehouseService.calStandTimeAndPrice(dataObj);
            result.put("standardTime", dataObj.getString("standardTime"));
            result.put("standardPrice", dataObj.getString("standardPrice"));

            result.put("baseStandardTime", dataObj.getString("baseStandardTime"));
            result.put("baseStandardPrice", dataObj.getString("baseStandardPrice"));

            result.put("sewLength", dataObj.getString("sewLength"));
            result.put("uiRandomCode", uiRandomCode);
        } catch (Exception e) {
            LOGGER.info("??????????????????????????????????????????" + e.getMessage());
        }
        return Result.ok(MessageConstant.SUCCESS, result);
    }

    /**
     * ???????????????????????????????????????
     */
    @RequestMapping(value = "/calAll", method = RequestMethod.POST)
    @ApiOperation(value = "calAll", notes = "??????????????????????????????")
    public Result<JSONObject> calAll(HttpServletRequest request) throws Exception {
        String data = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        if (StringUtils.isEmpty(data)) {
            return Result.build(MessageConstant.PARAM_NULL, "????????????");
        }
        LOGGER.info("??????????????????????????????--???????????????????????????????????????????????????" + data);
        JSONObject result = new JSONObject();
        try {

            JSONObject dataObj = JSONObject.parseObject(data);

            Integer fabricFraction = dataObj.getInteger("fabricFraction");
            String planCode = dataObj.getString("planCode");
            String uiRandomCode = dataObj.getString("uiRandomCode");
            if (StringUtils.isNotEmpty(planCode) && fabricFraction != null) {
                List<FabricGrade> fabricGradeList = fabricGradeService.getAllFabricGrade();
                List<OrderGrade> orderGradeList = orderGradeService.getAllOrderGrade();
                JSONObject obj = calData(planCode, fabricFraction, fabricGradeList, orderGradeList);
                result.put("orderGrade", obj.get("orderGrade"));
                result.put("fabricGradeCode", obj.get("fabricGradeCode"));
                result.put("fabricRatio", obj.get("fabricRatio"));
                dataObj.put("fabricRatio", obj.get("fabricRatio"));
            }
            sewingCraftWarehouseService.calStandTimeAndPrice(dataObj);
            result.put("standardTime", dataObj.getString("standardTime"));
            result.put("standardPrice", dataObj.getString("standardPrice"));
            result.put("sewLength", dataObj.getString("sewLength"));
            result.put("uiRandomCode", uiRandomCode);
        } catch (Exception e) {
            LOGGER.info("??????????????????????????????????????????" + e.getMessage());
        }
        return Result.ok(MessageConstant.SUCCESS, result);
    }

    /**
     * ??????????????????????????????????????????????????????
     */
    @RequestMapping(value = "/calStandTimeAndPrice", method = RequestMethod.POST)
    @ApiOperation(value = "calStandTimeAndPrice", notes = "????????????????????????????????????????????????")
    public Result<JSONObject> calStandTimeAndPrice(HttpServletRequest request) throws Exception {
        String data = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        if (StringUtils.isEmpty(data)) {
            return Result.build(MessageConstant.PARAM_NULL, "????????????");
        }
        LOGGER.info("????????????????????????" + data);
        JSONObject result = new JSONObject();
        try {
            JSONObject dataObj = JSONObject.parseObject(data);
            //??????
            BigDecimal rpm = dataObj.getBigDecimal("rpm");
            //??????
            BigDecimal stitchLength = dataObj.getBigDecimal("stitchLength");
            //??????

            JSONArray choosedList = dataObj.getJSONArray("choosedList");
            JSONArray notChoosedList = dataObj.getJSONArray("notChoosedList");
            List<String> motionCodeList = new ArrayList<>();
            JSONArray motionsList = new JSONArray();
            if (null == choosedList || choosedList.size() == 0) {
                //??????????????????????????????????????????????????????0
                result.put("fixedTime", BigDecimal.ZERO.toString());
            } else {
                motionsList.addAll(choosedList);
                result.put("fixedTime", calMachineAndManualTime(notChoosedList, rpm, stitchLength, motionCodeList).intValue());
            }
            if (null == notChoosedList || notChoosedList.size() == 0) {
                //??????????????????????????????????????????????????????0
                result.put("floatingTime", BigDecimal.ZERO.toString());
            } else {
                motionsList.addAll(notChoosedList);
                result.put("floatingTime", calMachineAndManualTime(choosedList, rpm, stitchLength, motionCodeList).intValue());
            }
            //??????????????????
            //????????????=?????????????????????M???????????????????????????????????????*????????????,???M??????????????????0
            BigDecimal sewLength = BigDecimal.ZERO;
            for (int i = 0; i < motionsList.size(); i++) {
                JSONObject obj = motionsList.getJSONObject(i);
                BigDecimal frequency = BigDecimal.ONE;
                BigDecimal frequencyObj = obj.getBigDecimal("frequency");
                if (null != frequencyObj) {
                    frequency = frequencyObj;
                }
                String code = obj.getString("motionCode");
                if (!code.startsWith("M") || code.indexOf("=") != -1) {
                    sewLength = sewLength.add(BigDecimal.ZERO);
                } else {
                    sewLength = sewLength.add(frequency.multiply(new BigDecimal(code.substring(1, 3))).setScale(3, BigDecimal.ROUND_HALF_UP));
                }
            }
            result.put("sewingLength", sewLength.intValue());
        } catch (Exception e) {
            LOGGER.info("--------????????????????????????????????????????????????:" + e.getMessage());
            return Result.build(MessageConstant.PROGRAM_ERROR,
                    MessageConstant.messageMap.get(MessageConstant.PROGRAM_ERROR),
                    0L, null);
        }
        return Result.ok(MessageConstant.SUCCESS, result);

    }

    /**
     * ???????????????????????????????????????
     */
    private BigDecimal calMachineAndManualTime(JSONArray list, BigDecimal rpm, BigDecimal
            stitchLength, List<String> motionCodeList) {
        if (null == list || list.size() == 0) {
            return new BigDecimal(0);
        }
        BigDecimal manual = new BigDecimal(0);
        BigDecimal machine = new BigDecimal(0);
        for (int i = 0; i < list.size(); i++) {
            JSONObject obj = list.getJSONObject(i);
            String motionCode = obj.getString("motionCode");
            //code??????=====????????????????????????
            if (motionCode.indexOf("=") != -1 || StringUtils.isEmpty(motionCode)) {
                continue;
            }
            motionCodeList.add(motionCode);
            BigDecimal manualTime = obj.getBigDecimal("manualTime");
            if (null == manualTime) {
                manualTime = BigDecimal.ZERO;
            }
            BigDecimal machineTime = obj.getBigDecimal("machineTime");
            if (null == machineTime) {
                machineTime = BigDecimal.ZERO;
            }
            manual = manual.add(manualTime);
            machine = machine.add(machineTime);
        }
        return machine.add(manual);
    }


    @RequestMapping(value = "/searchSewingCraftData", method = RequestMethod.GET)
    @ApiOperation(value = "/searchSewingCraftData", notes = "????????????????????????????????????", httpMethod = "GET", response = Result.class)
    public Result<List<SewingCraftVo>> searchSewingCraftData(HttpServletRequest request,
                                                             @RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                                             @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows,
                                                             @RequestParam(name = "categoryCode", required = true) String categoryCode,
                                                             @RequestParam(name = "queryData", required = false) String queryData) {

        HashMap param = new HashMap();
        if (StringUtils.isNotEmpty(categoryCode)) param.put("categoryCode", categoryCode);
        if (StringUtils.isNotEmpty(queryData)) param.put("queryData", queryData);

        List<SewingCraftVo> sewingCraftVos = sewingCraftWarehouseService.searchSewingCraftData(param);
        Long size = null;
        if (ObjectUtils.isNotEmptyList(sewingCraftVos)) {
            size = Long.valueOf(sewingCraftVos.size());
            sewingCraftVos = PageUtils.startPage(sewingCraftVos, page, rows);
        }
        return Result.ok(sewingCraftVos, size);
    }

    @ApiOperation(value = "exportSewingCraft", notes = "??????????????????????????????", httpMethod = "GET", response = Result.class)
    @RequestMapping(value = "/exportSewingCraft", method = RequestMethod.GET)
    public Result exportSewingCraft(@RequestParam(name = "keywords", required = false) String keywords,
                                    @RequestParam(name = "craftCode", required = false) String craftCode,
                                    @RequestParam(name = "craftName", required = false) String craftName,
                                    @RequestParam(name = "description", required = false) String description,
                                    @RequestParam(name = "craftCategoryCode", required = false) String craftCategoryCode,
                                    @RequestParam(name = "craftPartCode", required = false) String craftPartCode,
                                    @RequestParam(name = "mainFrameCode", required = false) String mainFrameCode,
                                    @RequestParam(name = "createUser", required = false) String createUser,
                                    @RequestParam(name = "releaseUser", required = false) String releaseUser,
                                    @RequestParam(name = "createTimeBeginDate", required = false) String createTimeBeginDate,
                                    @RequestParam(name = "createTimeEndDate", required = false) String createTimeEndDate,
                                    @RequestParam(name = "releaseTimeBeginDate", required = false) String releaseTimeBeginDate,
                                    @RequestParam(name = "releaseTimeEndDate", required = false) String releaseTimeEndDate,
                                    @RequestParam(name = "status", required = false) Integer status,
                                    HttpServletResponse response) throws Exception {
        LOGGER.info("????????????????????????????????????:  " + " keywords:" + keywords);
        List<User> users = userService.getAllUser();
        List<String> createUserList = new ArrayList<>();
        List<String> releaseUserList = new ArrayList<>();
        for (User user : users) {
            String name = user.getUserName();
            if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(createUser) && name.indexOf(createUser) != -1) {
                createUserList.add(user.getUserCode());
            }
            if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(releaseUser) && name.indexOf(releaseUser) != -1) {
                releaseUserList.add(user.getUserCode());
            }
        }
        List<SewingCraftWarehouse> data = sewingCraftWarehouseService.getDataByPager(keywords, craftCode, craftName, description, craftCategoryCode, craftPartCode, status, mainFrameCode, createUserList, releaseUserList, createTimeBeginDate, createTimeEndDate, releaseTimeBeginDate, releaseTimeEndDate);
        if (CollUtil.isEmpty(data)) {
            return Result.ok("???????????????");
        }
        parseStatus(data);
        XSSFWorkbook wb = null;
        ServletOutputStream fos = null;
        try {
            wb = new XSSFWorkbook();
            Sheet sheet = wb.createSheet();
            Row row = sheet.createRow(0);
            int cellIndex = 0;
            Cell cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("????????????");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("????????????");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("????????????");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("????????????");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("????????????");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("????????????");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("????????????");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("????????????");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("????????????");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("?????????");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("????????????");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("?????????");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("????????????");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("??????");
            int rowIndex = 1;
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (SewingCraftWarehouse sew : data) {
                row = sheet.createRow(rowIndex++);
                cellIndex = 0;
                cell = row.createCell(cellIndex++, CellType.STRING);
                cell.setCellValue(sew.getCraftCode());
                cell = row.createCell(cellIndex++, CellType.STRING);
                cell.setCellValue(sew.getCraftPartName());
                cell = row.createCell(cellIndex++, CellType.STRING);
                cell.setCellValue(sew.getCraftName());
                cell = row.createCell(cellIndex++, CellType.STRING);
                cell.setCellValue(sew.getCraftGradeCode());
                cell = row.createCell(cellIndex++, CellType.STRING);
                cell.setCellValue(sew.getMachineName());
                cell = row.createCell(cellIndex++, CellType.STRING);
                if (null != sew.getStandardTime()) {
                    cell.setCellValue(sew.getStandardTime().toPlainString());
                }
                cell = row.createCell(cellIndex++, CellType.STRING);
                if (null != sew.getStandardPrice()) {
                    cell.setCellValue(sew.getStandardPrice().toPlainString());
                }
                cell = row.createCell(cellIndex++, CellType.STRING);
                cell.setCellValue(sew.getMakeTypeCode());
                cell = row.createCell(cellIndex++, CellType.STRING);
                cell.setCellValue(sew.getAllowanceRandomCode());
                cell = row.createCell(cellIndex++, CellType.STRING);
                cell.setCellValue(sew.getCreateUser());
                cell = row.createCell(cellIndex++, CellType.STRING);
                Date createTime = sew.getCreateTime();
                String time = "";
                if (null != createTime) {
                    time = sdf1.format(createTime);
                }
                cell.setCellValue(time);
                cell = row.createCell(cellIndex++, CellType.STRING);
                cell.setCellValue(sew.getReleaseUserName());
                cell = row.createCell(cellIndex++, CellType.STRING);
                cell.setCellValue(sew.getReleaseTime());
                Date releaseTime = sew.getCreateTime();
                String release = "";
                if (null != createTime) {
                    release = sdf1.format(releaseTime);
                }
                cell.setCellValue(release);
                cell = row.createCell(cellIndex++, CellType.STRING);
                cell.setCellValue(sew.getStatusName());
            }
            String fileName = URLEncoder.encode("??????????????????.xlsx", "UTF-8");
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            fos = response.getOutputStream();
            wb.write(fos);
            wb.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != fos) {
                fos.close();
                fos = null;
            }
            if (null != wb) {
                wb.close();
                wb = null;
            }
        }
        if (null != data) {
            data.clear();
            data = null;
        }
        return null;
    }

    private void parseStatus(List<SewingCraftWarehouse> data) {
        if (CollUtil.isEmpty(data)) {
            return;
        }
        data.stream().parallel().forEach(x -> {
            x.setStatusName(getStatusName(x.getStatus()));
        });
    }

    public static String getStatusName(Integer status) {
        String result = "";
        if (BusinessConstants.Status.DRAFT_STATUS.equals(status)) {
            result = "??????";
        } else if (BusinessConstants.Status.AUDITED_STATUS.equals(status)) {
            result = "?????????";
        } else if (BusinessConstants.Status.PUBLISHED_STATUS.equals(status)) {
            result = "?????????";
        } else if (BusinessConstants.Status.IN_VALID.equals(status)) {
            result = "??????";
        } else if (BusinessConstants.Status.INVALID_STATUS.equals(status)) {
            result = "??????";
        } else if (BusinessConstants.Status.NOT_ACTIVE_STATUS.equals(status)) {
            result = "?????????";
        }
        return result;
    }

    @ApiOperation(value = "getSewingCraftRandomCode", notes = "????????????randomCode", httpMethod = "GET", response = Result.class)
    @RequestMapping(value = "/getSewingCraftRandomCode", method = RequestMethod.GET)
    public Result getSewingCraftRandomCode(
            @RequestParam(name = "craftCode", required = true) String craftCode) throws Exception {
        return sewingCraftWarehouseService.getSewingCraftRandomCode(craftCode);
    }


    @ApiOperation(value = "batchSendCraft", notes = "???????????????????????????????????????????????????ME", httpMethod = "GET", response = Result.class)
    @RequestMapping(value = "/batchSendCraft", method = RequestMethod.GET)
    public String batchSendCraft(
            @RequestParam(name = "craftCode", required = true) String craftCode) throws Exception {
        Map<String, Object> param = new HashMap<>(0);
        param.put("status", BusinessConstants.Status.PUBLISHED_STATUS);
        param.put("code", craftCode);
        List<SewingCraftWarehouse> data = sewingCraftWarehouseService.getDataByCraftCodeLike(param);
        return sendSewListToPi(data);
    }

    private String sendSewListToPi(List<SewingCraftWarehouse> sewList) {
        JSONObject obj = new JSONObject();
        obj.put("site", DEFAULT_SITE);
        obj.put("fromSysFlag", BusinessConstants.FromSysType.FROM_CAPP);
        JSONArray operation = new JSONArray();
        if (sewList != null && sewList.size() > 0) {
            final Map<String, SameLevelCraft> sameLevelCraftMap = sameLevelCraftService.getAllDataToMap();
            final List<CraftCategory> craftCategoryList = craftCategoryService.getAllValidCraftCategory();
            final Map<String, MakeType> makeTypeMap = makeTypeService.getMakeTypeMap();
            //sewList.stream().parallel().forEach(sew -> {
            sewList.stream().forEach(sew -> {
                getData(sew, operation, sameLevelCraftMap, craftCategoryList, makeTypeMap);
            });
//            for (SewingCraftWarehouse sew : sewList) {
//                getData(sew, operation, sameLevelCraftMap);
//            }
        }
        obj.put("operations", operation);
        LOGGER.info("???????????????PI????????????:" + JSONObject.toJSONString(obj));
        return sendPi.sendToPi(BusinessConstants.PiReceiveUrlConfig.SEWING_CRAFT_URL, obj);
    }

    private void getData(SewingCraftWarehouse sewingCraftWarehouse, JSONArray operation, Map<String, SameLevelCraft> sameLevelCraftMap, List<CraftCategory> craftCategoryList, Map<String, MakeType> makeTypeMap) {
        if (null == sewingCraftWarehouse) {
            return;
        }
        String craft_category_codes = sewingCraftWarehouse.getCraftCategoryCode();
        String craft_category_names = sewingCraftWarehouse.getCraftCategoryName();
        String craftPartsCodes = sewingCraftWarehouse.getCraftPartCode();
        String craftPartsNames = sewingCraftWarehouse.getCraftPartName();
        String craft_category_name[] = craft_category_names.split(",");
        String craft_category_code[] = craft_category_codes.split(",");
        String craftPartsCode[] = craftPartsCodes.split(",");
        String craftPartsName[] = craftPartsNames.split(",");
        List<SewingCraftWarehouseWorkplace> sewingCraftWarehouseWorkplaces = sewingCraftWarehouseWorkplaceService.getDataBySewingCraftRandomCode(sewingCraftWarehouse.getRandomCode());
        //List<CraftCategory> craftCategoryList = craftCategoryService.getAllValidCraftCategory();
        // Map<String, MakeType> makeTypeMap = makeTypeService.getMakeTypeMap();
        for (int i = 0; i < craft_category_code.length; i++) {
            // for (int i = 0; i < 1; i++) {
            JSONObject data = new JSONObject();
            try {
                data.put("gxdm", sewingCraftWarehouse.getCraftCode());//????????????
                String desc = sewingCraftWarehouse.getDescription();
                if (StringUtils.isNotEmpty(desc) && desc.length() > 20) {
                    desc = desc.substring(0, 20);
                }
                data.put("gxmc", desc);//????????????
                data.put("jqdm", sewingCraftWarehouse.getMachineCode());//????????????
                String machineName = sewingCraftWarehouse.getMachineName();
                if (StringUtils.isNotEmpty(machineName) && machineName.length() > 20) {
                    machineName = machineName.substring(0, 20);
                }
                data.put("jqmc", machineName);//????????????
                data.put("SMV", sewingCraftWarehouse.getStandardTime());//????????????
                data.put("dj", sewingCraftWarehouse.getStandardPrice());//??????
                data.put("zglb", sewingCraftWarehouse.getMakeTypeCode());//????????????
                if (StringUtils.isNotBlank(sewingCraftWarehouse.getMakeTypeCode())) {
                    MakeType makeType = makeTypeMap.getOrDefault(sewingCraftWarehouse.getMakeTypeCode(), null);
                    if (makeType != null) {
                        String makeTypeName = makeType.getMakeTypeName();
                        if (StringUtils.isNotEmpty(makeTypeName) && makeTypeName.length() > 20) {
                            makeTypeName = makeTypeName.substring(0, 20);
                        }
                        data.put("zgmc", makeTypeName);//????????????
                    }
                }


                if (StringUtils.isNotBlank(craft_category_codes)) {
                    String[] keyArr = craft_category_codes.split(",");
                    String codes = "";
                    for (String key : keyArr) {
                        for (CraftCategory cat : craftCategoryList) {
                            if (key.equals(cat.getCraftCategoryCode())) {
                                if (codes == "") {
                                    codes = cat.getClothesBigCategoryCode();
                                } else {
                                    codes = codes + "," + cat.getClothesBigCategoryCode();
                                }
                            }
                        }
                    }
                    data.put("craftCategoryCode", codes);
                } else {
                    data.put("craftCategoryCode", craft_category_codes);//??????????????????craft_category_code[i]
                }

                if (StringUtils.isNotEmpty(craft_category_names) && craft_category_names.length() > 20) {
                    data.put("craftCategoryName", craft_category_names.substring(0, 20));//??????????????????craft_category_name[i]
                } else {
                    data.put("craftCategoryName", craft_category_names);//??????????????????craft_category_name[i]
                }
                data.put("gybj", craftPartsCodes);//??????????????????craftPartsCode[i]

                if (StringUtils.isNotEmpty(craftPartsNames) && craftPartsNames.length() > 20) {
                    data.put("gybjmc", craftPartsNames.substring(0, 20));//??????????????????craftPartsName[i]
                } else {
                    data.put("gybjmc", craftPartsNames);//??????????????????craftPartsName[i]
                }
                String sameCraft = sewingCraftWarehouse.getSameLevelCraftNumericalCode();
                if (StringUtils.isNotEmpty(sameCraft)) {
                    data.put("sjgxdm", sameCraft);//??????????????????
                } else {
                    data.put("sjgxdm", "");//??????????????????
                }

                if (sameLevelCraftMap.containsKey(sameCraft)) {
                    data.put("sjgxmc", sameLevelCraftMap.get(sameCraft).getSameLevelCraftName());//??????????????????
                    data.put("hardLevel", sameLevelCraftMap.get(sameCraft).getHardLevel()); //????????????
                } else {
                    data.put("sjgxmc", "");
                }
                data.put("bjdm", "");//????????????
                data.put("bjmc", "");//??????????????????
                data.put("wxbz", "N");//????????????
                data.put("sgzdm", sewingCraftWarehouse.getCraftCode());//??????????????? ???????????????
                String aaa = sewingCraftWarehouse.getCraftName();
                if (StringUtils.isNotEmpty(aaa) && aaa.length() > 20) {
                    aaa = aaa.substring(0, 20);
                }
                data.put("sgzmc", aaa);//??????????????????????????????
                data.put("cutSign", "N");//????????????(Y/N) ????????????????????????????????????N???
                if (StaticDataCache.WORK_TYPE_DICTIONARY_MAP.containsKey(sewingCraftWarehouse.getWorkTypeCode())) {
                    String section = StaticDataCache.WORK_TYPE_DICTIONARY_MAP.get(sewingCraftWarehouse.getWorkTypeCode()).getRemark();
                    if (StringUtils.isNotEmpty(section)) {
                        String sec[] = section.split("_");
                        data.put("bmdm", sec[0]);//????????????
                        data.put("bmmc", sec[1]);//????????????
                    }
                }
                if (null != sewingCraftWarehouseWorkplaces && sewingCraftWarehouseWorkplaces.size() > 0) {
                    for (SewingCraftWarehouseWorkplace workplace : sewingCraftWarehouseWorkplaces) {
                        if (workplace.getCraftCategoryCode().equals(craft_category_code[i])) {
                            data.put("main_frame_code", workplace.getMainFrameCode());//?????????????????????
                            data.put("main_frame_name", workplace.getMainFrameName());//?????????????????????
                            if (StringUtils.isNotEmpty(workplace.getProductionPartCode())) {
                                String partCode = workplace.getProductionPartCode();
                                Map<String, ProductionPart> partMap = productionPartService.getMapByMainFrameCode(workplace.getMainFrameCode());
                                if (partMap.containsKey(workplace.getProductionPartCode())) {
                                    data.put("scbj", partMap.get(workplace.getProductionPartCode()).getCustomProductionAreaCode());//????????????
                                }

                            } else {
                                data.put("scbj", "");//????????????
                            }
                            if (StringUtils.isNotEmpty(workplace.getProductionPartName())) {
                                data.put("scbjmc", workplace.getProductionPartName());//??????????????????
                            } else {
                                data.put("scbjmc", "");//??????????????????
                            }
                            break;
                        }
                    }
                }
                List<SewingCraftStd> sewingCraftStdsTmp = sewingCraftStdService.getDataBySewingCraftRandomCode(sewingCraftWarehouse.getRandomCode());
                List<CraftStd> craftStdList = new ArrayList<>();
                if (null != sewingCraftStdsTmp && sewingCraftStdsTmp.size() > 0) {
                    for (SewingCraftStd std : sewingCraftStdsTmp) {
                        List<CraftStd> temp = getCraftStdByParam(std.getCraftStdCode(), null, null, null, null, null, null);
                        if (temp != null && temp.size() > 0) {
                            craftStdList.addAll(temp);
                        }
                    }
                }
                String toolCode = "";
                String toolName = "";
                String picUrl = "";
                String vedioUrl = "";
                String qualitySpec = "";
                String makeDescription = "";
                int stdFlag = 0;
                for (CraftStd std : craftStdList) {
                    toolCode = stdFlag == 0 ? std.getHelpTool() : toolCode + "," + std.getHelpTool();
                    toolName = stdFlag == 0 ? std.getHelpToolCode() : toolName + "," + std.getHelpToolCode();
                    //toolCode = toolCode + "," + std.getHelpTool();
                    // toolName = toolName + "," + std.getHelpToolCode();
                    if (StringUtils.isNotEmpty(std.getRequireQuality())) {
                        //qualitySpec = qualitySpec + "," + std.getRequireQuality();
                        qualitySpec = stdFlag == 0 ? std.getRequireQuality() : qualitySpec + "," + std.getRequireQuality();
                    }
                    if (StringUtils.isNotEmpty(std.getMakeDesc())) {
                        // makeDescription = makeDescription + "," + std.getMakeDesc();
                        makeDescription = stdFlag == 0 ? std.getMakeDesc() : makeDescription + "," + std.getMakeDesc();
                    }

                    List<String> pics = std.getPictureUrlList();
                    if (null != pics && pics.size() > 0) {
                        /*for (String pp : pics) {
                            //picUrl = picUrl + "," + pp;
                            if (StringUtils.isNotEmpty(pp)) {
                                picUrl = stdFlag == 0 ? pp : picUrl + "," + pp;
                            }
                        }*/
                        picUrl = pics.get(0);
                    }
                    List<String> vedeios = std.getVedioUrlList();
                    if (null != vedeios && vedeios.size() > 0) {
                        /*for (String ve : vedeios) {
                            vedioUrl = vedioUrl + "," + ve;
                            if (StringUtils.isNotEmpty(ve)) {
                                vedioUrl = stdFlag == 0 ? ve : vedioUrl + "," + ve;
                            }
                        }*/
                        vedioUrl = vedeios.get(0);
                    }
                    stdFlag++;
                }
                data.put("gjdm", toolCode);//????????????
                if (StringUtils.isNotEmpty(toolName) && toolName.length() > 20) {
                    toolName = toolName.substring(0, 20);
                }
                data.put("gjmc", toolName);//????????????
                if (StringUtils.isNotEmpty(qualitySpec) && qualitySpec.length() > 20) {
                    qualitySpec = qualitySpec.substring(0, 20);
                }
                if (StringUtils.isNotEmpty(makeDescription) && makeDescription.length() > 20) {
                    makeDescription = makeDescription.substring(0, 20);
                }
                data.put("pzsm", qualitySpec);//????????????
                data.put("zgsm", makeDescription);//????????????
                data.put("fjPath", picUrl);//??????Url
                data.put("videoURL", vedioUrl);//??????URL
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (StringUtils.isNotEmpty(data.getString("scbj"))) {
                //synchronized (this) {
                operation.add(data);
                // }
                break;
            }
        }
    }
}
