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
 * 车缝工序词库
 */
@Api(tags = "车缝工序词库")
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
     * 新建界面 下拉字段的数据
     */
    @ApiOperation(value = "getDropDownData", notes = "新建界面 下拉字段的数据")
    @RequestMapping(value = "/getDropDownData", method = RequestMethod.GET)
    public Result<JSONObject> getDropDownData() {
        JSONObject result = new JSONObject();
        //List<CraftCategory> craftCategory = craftCategoryService.getAllValidCraftCategory();
        // result.put("craftCategory",craftCategory);
        //防止分页插件bug 偶发性影响
        List<WebConfig> webConfigList = webConfigService.getConfigList();
        //1、工艺品类与部件的层级关系
        List<CraftCategory> craftCategories = craftCategoryService.getCraftCategoryAndPart();
        if (null != craftCategories && craftCategories.size() > 0 && craftCategories.size() < 5) {
            LOGGER.info("craftCategories长度太短了:" + craftCategories.size());
            try {
                int loop = 20;
                while (craftCategories.size() < 5) {
                    TimeUnit.MICROSECONDS.sleep(10);
                    craftCategories = craftCategoryService.getCraftCategoryAndPart();
                    LOGGER.info("craftloop " + loop + " 次,得到的" + craftCategories.size());
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


        //2、做工类型
       /* List<MakeType> makeTypes = makeTypeService.getAllMakeType();
        result.put("makeTypes", makeTypes);*/
        List<WorkType> workTypes = workTypeService.getWorkTypeAndMakeType();
        result.put("workTypes", workTypes);
        //3、车缝工序等级
        List<CraftGradeEquipment> craftGrades = gradeEquipmentService.getCraftGradeByType(BusinessConstants.CraftGradeEquipmentType.CRAFT_GRADE_SEWING);
        result.put("craftGrades", craftGrades);

        //4、机器设备
        List<Machine> machines = machineService.getAllMachine();
        result.put("machines", machines);

        //5、宽放系数
        List<WideCoefficient> wideCoefficients = wideCoefficientService.getAllWideCoefficient();
        result.put("wideCoefficients", wideCoefficients);

        //6、捆扎时间
        List<StrappingTimeConfig> strappingTimeConfigs = strappingTimeConfigService.getAllStrappingTimeConfigs();
        result.put("strappingTimeConfigs", strappingTimeConfigs);

        //7、面料分值方案
        List<FabricScorePlanVO> fabricScorePlanVOS = fabricScorePlanService.selectFabricScoresAndPlan();
        result.put("fabricScorePlanVOS", fabricScorePlanVOS);

        //8同级工序
        List<SameLevelCraft> sameLevelCrafts = sameLevelCraftService.getAllData();
        result.put("sameLevelCrafts", sameLevelCrafts);
        return Result.ok(MessageConstant.SUCCESS, result);

    }

    @RequestMapping(value = "/getSewingCraftByRandomCode", method = RequestMethod.GET)
    @ApiOperation(value = "getSewingCraftByRandomCode", notes = "通过randomCode查询")
    public Result<JSONObject> getSewingCraftByRandomCode(@RequestParam String randomCode) {
        LOGGER.info("查询的randomCode：" + randomCode);
        if (StringUtils.isEmpty(randomCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "参数为空");
        }
        JSONObject result = new JSONObject();
        try {
            Long code = Long.parseLong(randomCode);
            SewingCraftWarehouse sewingCraftWarehouse = sewingCraftWarehouseService.getDataByRandom(code);
            result = getDetailDataToJSONObject(sewingCraftWarehouse, BusinessConstants.TableName.SEWING_CRAFT, null, null, null);
        } catch (Exception e) {
            LOGGER.error("查询的动作出现异常:" + e.getMessage());
        }
        return Result.ok(MessageConstant.SUCCESS, result);
    }

    @RequestMapping(value = "/getMinuteWage", method = RequestMethod.GET)
    @ApiOperation(value = "getMinuteWage", notes = "通过工厂和工序等级获取分钟工资")
    public Result<BigDecimal> getMinuteWage(@RequestParam String factoryCode, @RequestParam String craftGradeCode) {
        Map<String, BigDecimal> minueMap = getMinueMap();
        BigDecimal data = minueMap.get(factoryCode + "_" + craftGradeCode);
        if (null == data) {
            data = BigDecimal.ZERO;
        }
        return Result.ok(MessageConstant.SUCCESS, data.setScale(3, BigDecimal.ROUND_HALF_UP));
    }

    /**
     * key是工厂_工序等级
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
     * 传入多个工序编码进行查询,目前暂定大货款式工艺使用
     * 里面有一步是重新计算标准时间和标准单价，面料等级，面料系数，订单等级
     */
    @RequestMapping(value = "/getSewingCraftByCraftCodeList", method = RequestMethod.POST)
    @ApiOperation(value = "getSewingCraftByCraftCodeList", notes = "传入多个工序编码进行查询")
    public Result<List<JSONObject>> getSewingCraftByCraftCodeList(HttpServletRequest request) throws Exception {
        //如果用idList 传值过来，就是要查大货款式工艺下面的工序数据
        String data = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        if (StringUtils.isEmpty(data)) {
            return Result.build(MessageConstant.PARAM_NULL, "参数为空");
        }
        LOGGER.info("从部件工艺库导入的参数：" + data);
        JSONObject dataObj = JSONObject.parseObject(data);
        JSONArray idArray = dataObj.getJSONArray("idList");
        String flag = dataObj.getString("flag");
        String searchType = dataObj.getString("searchType");

        String factoryCode = dataObj.getString("factoryCode");
        String mainFrameCode = dataObj.getString("mainFrameCode");
        Integer fabricFraction = dataObj.getInteger("fabricFraction");
        List<SewingCraftWarehouse> sewingCraftWarehouseList = new ArrayList<>(0);
        //Map<String, JSONObject> mapData = new HashMap<>();
        //用一个线程安全的数组
        final List<JSONObject> result = Collections.synchronizedList(new ArrayList());
        Map<String, BigDecimal> minueMap = getMinueMap();
        //防止分页插件bug 偶发性影响
        List<WebConfig> webConfigList = webConfigService.getConfigList();
        //传idList是通过编辑进入的，需要在款式工艺里面的工序里面查找
        if (null != idArray && idArray.size() > 0) {
            List<Long> idList = new ArrayList<>();
            for (int i = 0; i < idArray.size(); i++) {
                idList.add(Long.parseLong(idArray.getJSONObject(i).getString("id")));
            }
            LOGGER.info("idList：" + idList);

            // List<SewingCraftWarehouse> sews = styleSewingCraftWarehouseService.getDataByPartDetailIds(idList);
            //区分是大货款式工艺路径还是工单工艺路径
            String tableName = "order".equalsIgnoreCase(flag) ? BusinessConstants.TableName.BIG_ORDER : BusinessConstants.TableName.BIG_STYLE_ANALYSE;
            List<SewingCraftWarehouse> sews = new ArrayList<>();
            if ("order".equalsIgnoreCase(flag)) {
                LOGGER.info("查询order：");
                sews = bigOrderSewingCraftWarehouseService.getDataByPartDetailIds(idList);
            } else {
                LOGGER.info("查询style：");
                sews = styleSewingCraftWarehouseService.getDataByPartDetailIds(idList);

            }
            //处理工序丢失的问题
            LOGGER.info("idList的length是:" + idList.size() + " sews的length是:" + sews.size());
            if (null != sews && sews.size() > 0 && sews.size() != idList.size()) {
                LOGGER.info("长度不一致啦");
                try {
                    int loop = 10;
                    while (sews.size() != idList.size()) {
                        TimeUnit.MICROSECONDS.sleep(10);
                        if ("order".equalsIgnoreCase(flag)) {
                            LOGGER.info("查询order：");
                            sews = bigOrderSewingCraftWarehouseService.getDataByPartDetailIds(idList);
                        } else {
                            LOGGER.info("查询style：");
                            sews = styleSewingCraftWarehouseService.getDataByPartDetailIds(idList);
                        }
                        LOGGER.info("loop " + loop + " 次,得到的" + sews.size());
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
                LOGGER.info("SIZE：" + sews.size());
                LOGGER.info("sews：" + JSONObject.toJSONString(sews));
                sews.stream().parallel().forEach(sew -> {
                    sew.setId(sew.getPartDetailId());
                    JSONObject obj = getDetailDataToJSONObject(sew, tableName, mainFrameCode, null, null);
                    //mapData.put("" + sew.getRandomCode() + sew.getPartDetailId(), obj);
                    addData(result, obj);
                });
                if (StringUtils.isNotEmpty(factoryCode) && !"order".equalsIgnoreCase(flag)) {
                    for (JSONObject obj : result) {
                        //工单工艺的要重新计算标准单价 标准单价=标准时间*工厂工序等级分钟工资（根据工单号带过来的工厂为准）
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

        } else { //传codeList是通过导入进入的，需要在标准工序管理的工序里面查找
            String codeList = dataObj.getString("codeList");
            LOGGER.info("codeList：" + codeList);
            if (StringUtils.isEmpty(codeList)) {
                return Result.build(MessageConstant.PARAM_NULL, "codeList参数为空");
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
                //增加乐从工单工艺导入工序
                if (StringUtils.isNotEmpty(obj.getString("productionTicketNo"))) {
                    productionTicketNos.add(obj.getString("productionTicketNo"));
                    key = obj.getString("productionTicketNo") + obj.getString("code");
                }
                styleMap.put(key, key);
            }
            //区分到底是款式工艺导入还是工序词库导入,bigstyle 跟前端约定好了
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
            } else if ("orderstyle".equalsIgnoreCase(searchType)) {//从工单工艺导入
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
                    //计算车缝长度
                    //款式库弹框中的工序导入后，工序的时间浮余信息不同步（即从A款 导X工序 到 B款中，X工序的时间浮余信息不同步过来，在B款中的X工序的时间浮余默认为0）
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
                    //处理款式工艺和工单工艺基础时间和基础单价
                    obj.put("baseStandardTime", obj.getString("standardTime"));
                    obj.put("baseStandardPrice", obj.getString("standardPrice"));
                    //第一次从工序词库导入
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
            //工单工艺的要重新计算标准单价 标准单价=标准时间*工厂工序等级分钟工资（根据工单号带过来的工厂为准）
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
        //计算车缝长度
        //车缝长度=车缝动作代码（M开头的动作代码）的车缝长度*频率之和,非M开头的值就是0
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
     * 里面面料等级，面料系数，订单等级
     */
    @RequestMapping(value = "/calSomeData", method = RequestMethod.GET)
    @ApiOperation(value = "calSomeData", notes = "计算面料等级，面料系数，订单等级")
    public Result<JSONObject> calSomeData(@RequestParam(value = "planCode", required = true) String planCode,
                                          @RequestParam(value = "fabricFraction", required = true) Integer fabricFraction) {
        List<FabricGrade> fabricGradeList = fabricGradeService.getAllFabricGrade();
        List<OrderGrade> orderGradeList = orderGradeService.getAllOrderGrade();
        JSONObject data = calData(planCode, fabricFraction, fabricGradeList, orderGradeList);
        return Result.ok(MessageConstant.SUCCESS, data);
    }

    private JSONObject calData(String planCode, Integer
            fabricFraction, List<FabricGrade> fabricGradeList, List<OrderGrade> orderGradeList) {
        //计算面料等级
        //面料等级--默认等级为0
        String fabricGradeCode = "0";
        List<FabricScore> fabricScores = fabricScoreService.getFabricScoreByPlanCode(planCode);
        if (null != fabricScores && fabricScores.size() > 0) {
            for (FabricScore score : fabricScores) {
                if (score.getMinValue() <= fabricFraction && score.getMaxValue() >= fabricFraction) {
                    fabricGradeCode = score.getFabricGradeCode();
                }
            }
        }
        //计算面料系数
        BigDecimal coefficient = BigDecimal.ZERO;
        if (null != fabricGradeList && fabricGradeList.size() > 0) {
            for (FabricGrade fabricGrade : fabricGradeList) {
                if (fabricGrade.getFabricGradeCode().equals(fabricGradeCode)) {
                    coefficient = fabricGrade.getCoefficient();
                }
            }
        }
        //订单等级
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
     * 查询工序的详情数据到JSONObject
     */
    private JSONObject getDetailDataToJSONObject(SewingCraftWarehouse sewingCraftWarehouse, String
            tableName, String mainFrameCode, List<FabricGrade> fabricGradeList, List<OrderGrade> orderGradeList) {
        //工艺部件和工艺品类存储的时候，多个值的时候 中间用逗号分隔，返回前端的时候转成list
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
        //查询工序的动作代码
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
        //查询工序的缝边位置
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
        //查询工位工序
        // taskExecutor.execute(() -> {
        try {
            List<SewingCraftWarehouseWorkplace> workplaceCraftListTmp = new ArrayList<>();
            if (BusinessConstants.TableName.SEWING_CRAFT.equals(tableName)) {
                workplaceCraftListTmp = sewingCraftWarehouseWorkplaceService.getDataByParam(param);
                //款式工艺，部件详情里面有一个工序流，新建的时候，这个工序流从工序里面带出来给前端，
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
        //查询工艺建标，还有里面的图片和视频
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
            //如果没有关联工艺标准编码，则显示GST中导入的做工说明、品质要求（图片和视频暂时以共享的形式存在）
//建标已导入了，暂时不需要自动关联
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
     * 工艺部件和工艺品类存储的时候，多个值的时候 中间用逗号分隔，返回前端的时候转成list
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
    @ApiOperation(value = "getAll", notes = "查询车缝工序列表")
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
    @ApiOperation(value = "searchSewingCraftWarehouse", notes = "弹出框工序选择")
    public Result<List<SewingCraftWarehouse>> searchSewingCraftWarehouse(@RequestParam(defaultValue = "1") Integer
                                                                                 page,
                                                                         @RequestParam(defaultValue = "30") Integer rows,
                                                                         @RequestParam(name = "craftPartCode", required = false) String craftPartCode,
                                                                         @RequestParam(name = "mainFrameCode", required = false) String mainFrameCode) {
        PageHelper.startPage(page, rows);
        LOGGER.info("列表页面查询参数是: page" + page + " rows:" + rows + " craftPartCode:" + craftPartCode + " mainFrameCode:" + mainFrameCode);
        Map<String, Object> map = new HashMap<>();
        map.put("mainFrameCode", mainFrameCode);
        map.put("craftPartCode", craftPartCode);
        List<SewingCraftWarehouse> data = sewingCraftWarehouseService.getDataByMainFramePartCode(map);
        PageInfo<SewingCraftWarehouse> pageInfo = new PageInfo<>(data);
        return Result.ok(data, pageInfo.getTotal());
    }

    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @ApiOperation(value = "updateStatus", notes = "修改工序状态")
    public Result<JSONObject> updateStatus(@RequestParam(name = "randomCode", required = true) String randomCode,
                                           @RequestParam(name = "status", required = true) Integer status,
                                           @RequestParam(name = "userCode", required = false) String userCode) throws Exception {
        final Long randomCodeLong = Long.parseLong(randomCode);
        SewingCraftWarehouse craft = sewingCraftWarehouseService.getDataByRandom(randomCodeLong);
        if (null != craft) {
            if (BusinessConstants.Status.IN_VALID.equals(status)) {
                String used = sewingCraftWarehouseService.getCraftInUsed(craft.getCraftCode());
                if (StringUtils.isNotBlank(used)) {
                    return Result.build(MessageConstant.PROGRAM_ERROR, "工序已使用，不允许删除");
                }
            }

            if (sewingCraftWarehouseService.updateStatus(status, randomCodeLong, userCode)) {
                JSONObject obj = new JSONObject();
                obj.put("randomCode", randomCode);
                obj.put("status", status);
                if (BusinessConstants.Status.PUBLISHED_STATUS.equals(status)) {
                    taskExecutor.execute(() -> {
                        SewingCraftWarehouse house = sewingCraftWarehouseService.getDataByRandom(randomCodeLong);
                        //更新其他表的工序名称信息等
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

    @ApiOperation(value = "getCraftInUsed", notes = "获取工序使用的地方", httpMethod = "GET", response = Result.class)
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
    @ApiOperation(value = "addOrUpdate", notes = "新增或者修改车缝工位工序")
    //@Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<JSONObject> update(HttpServletRequest request) throws Exception {
        String data = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        if (StringUtils.isEmpty(data)) {
            return Result.build(MessageConstant.PARAM_NULL, "参数为空");
        }
        LogUtil.insertBigStyleOperationToLOG(data, "sewing_craft_warehouse");
        LOGGER.info("addOrUpdate车缝工位工序的参数：" + data);
        JSONObject dataObj = JSONObject.parseObject(data);
        //参数校验
        Result<JSONObject> paramCheckResult = checkParam(dataObj);
        if (MessageConstant.PARAM_NULL.equals(paramCheckResult.getCode())) {
            LOGGER.error("参数为空，" + JSONObject.toJSONString(paramCheckResult));
            return paramCheckResult;
        }
        //判断是新增还是修改，randomCode和craftCode三者全部不为空才是新增
        //randomCode
        String randomCode = dataObj.getString("randomCode");
        //craftCode
        String craftCode = dataObj.getString("craftCode");
        String operation = BusinessConstants.Send2Pi.actionType_create;
        if (StringUtils.isNotEmpty(randomCode) && StringUtils.isNotEmpty(craftCode)) {
            LOGGER.info("------车缝工序的操作类型是update--------");
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
     * 因为部件工艺单独存了名称、描述、时间、单价、工序流，当工序变化的时候，要同步更新部件工艺的这些数据
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
    @ApiOperation(value = "generateSewingCraftCode", notes = "产生工序编码")
    public Result<JSONObject> generateSewingCraftCode(HttpServletRequest request) throws Exception {
        String data = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        if (StringUtils.isEmpty(data)) {
            return Result.build(MessageConstant.PARAM_NULL, "参数为空");
        }
        JSONObject dataObj = JSONObject.parseObject(data);
        String code = sewingCraftWarehouseService.justGenerateSewingCraftCode(dataObj);
        JSONObject result = new JSONObject();
        result.put("craftCode", code);
        result.put("randomCode", SnowflakeIdUtil.generateId());
        return Result.ok(MessageConstant.SUCCESS, result);
    }

    @RequestMapping(value = "/saveBigStyleSingleCraft", method = RequestMethod.POST)
    @ApiOperation(value = "saveBigStyleSingleCraft", notes = "实时保存款式工艺的单个工序")
    public Result<JSONObject> saveBigStyleSingleCraft(HttpServletRequest request) throws Exception {
        String data = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        if (StringUtils.isEmpty(data)) {
            return Result.build(MessageConstant.PARAM_NULL, "参数为空");
        }
        JSONObject dataObj = JSONObject.parseObject(data);
        //参数校验
        Result<JSONObject> paramCheckResult = checkParam(dataObj);
        if (MessageConstant.PARAM_NULL.equals(paramCheckResult.getCode())) {
            LOGGER.error("参数为空，" + JSONObject.toJSONString(paramCheckResult));
            return paramCheckResult;
        }
        String partCraftMainCode = dataObj.getString("partCraftMainCode");
        if (StringUtils.isEmpty(partCraftMainCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "部件编码为空");
        }
        Long bigStyleRandomCode = dataObj.getLong("bigStyleRandomCode");
        if (null == bigStyleRandomCode) {
            return Result.build(MessageConstant.PARAM_NULL, "款式工艺编码的randomCode为空");
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
                        //工序是新增的
                        if (null == ids || ids.size() == 0) {
                            List<BigStyleAnalysePartCraftDetail> partCraftDetails = new ArrayList<>();
                            partCraftDetails.add(bigStyleAnalysePartCraftDetail);
                            bigStyleAnalysePartCraftDetailDao.insertPartCraftDetailForeach(partCraftDetails);
                        } else {//工序是已经存在的
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
    @ApiOperation(value = "saveOrderSingleCraft", notes = "实时保存工单工艺的单个工序")
    public Result<JSONObject> saveOrderSingleCraft(HttpServletRequest request) throws Exception {
        String data = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        if (StringUtils.isEmpty(data)) {
            return Result.build(MessageConstant.PARAM_NULL, "参数为空");
        }
        JSONObject dataObj = JSONObject.parseObject(data);
        //参数校验
        Result<JSONObject> paramCheckResult = checkParam(dataObj);
        if (MessageConstant.PARAM_NULL.equals(paramCheckResult.getCode())) {
            LOGGER.error("参数为空，" + JSONObject.toJSONString(paramCheckResult));
            return paramCheckResult;
        }
        String partCraftMainCode = dataObj.getString("partCraftMainCode");
        if (StringUtils.isEmpty(partCraftMainCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "部件编码为空");
        }
        Long orderRandomCode = dataObj.getLong("orderRandomCode");
        if (null == orderRandomCode) {
            return Result.build(MessageConstant.PARAM_NULL, "工单工艺编码的randomCode为空");
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
     * 校验参数
     * operationType 操作类型，因为add和update校验有一点不同，update要必须要验证randomCode和Id不为空
     */
    public Result<JSONObject> checkParam(JSONObject dataObj) {
        //登录用户
        String userCode = dataObj.getString("userCode");
        if (StringUtils.isEmpty(userCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "登录用户为空");
        }
        //登录用户
        String status = dataObj.getString("status");
        if (StringUtils.isEmpty(status)) {
            return Result.build(MessageConstant.PARAM_NULL, "状态为空");
        }
        JSONArray craftCategoryList = dataObj.getJSONArray("craftCategoryList");
        if (null == craftCategoryList || craftCategoryList.size() == 0) {
            return Result.build(MessageConstant.PARAM_NULL, "工艺品类为空");
        }
        JSONArray craftPartList = dataObj.getJSONArray("craftPartList");
        if (null == craftPartList || craftPartList.size() == 0) {
            return Result.build(MessageConstant.PARAM_NULL, "工艺部件为空");
        }
      /* JSONArray workplaceCraftList = dataObj.getJSONArray("workplaceCraftList");
        if (null == workplaceCraftList || workplaceCraftList.size() == 0) {
            return Result.build(MessageConstant.PARAM_NULL, "工位工序为空");
        }
        JSONArray sewPartPositionList = dataObj.getJSONArray("sewPartPositionList");
        if (null == sewPartPositionList || sewPartPositionList.size() == 0) {
            return Result.build(MessageConstant.PARAM_NULL, "缝边位置为空");
        }
        JSONArray motionsList = dataObj.getJSONArray("motionsList");
        if (null == motionsList || motionsList.size() == 0) {
            return Result.build(MessageConstant.PARAM_NULL, "动作代码为空");
        }*/
        JSONArray motionsList = dataObj.getJSONArray("motionsList");
        if (null != motionsList && motionsList.size() > 0) {
            for (int i = 0; i < motionsList.size(); i++) {
                JSONObject obj = motionsList.getJSONObject(i);
                String str = obj.getString("motionName");
                if (str.length() > 500) {
                    return Result.build(MessageConstant.PARAM_NULL, "动作描述太长");
                }
            }

        }


        //做工类型
        String makeTypeCode = dataObj.getString("makeTypeCode");
        if (StringUtils.isEmpty(makeTypeCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "做工类型为空");
        }
        //工序名称
        String craftName = dataObj.getString("craftName");
        if (StringUtils.isEmpty(craftName)) {
            return Result.build(MessageConstant.PARAM_NULL, "工序名称为空");
        }
        if (StringUtils.isNotBlank(craftName)) {
            if (craftName.length() > 500) {
                return Result.build(MessageConstant.PARAM_NULL, "工序名称太长");
            }
        }

        //工序描述
        String description = dataObj.getString("description");
        if (StringUtils.isEmpty(description)) {
            return Result.build(MessageConstant.PARAM_NULL, "工序描述为空");
        }
        if (StringUtils.isNotBlank(description)) {
            if (description.length() > 500) {
                return Result.build(MessageConstant.PARAM_NULL, "工序描述太长");
            }
        }

        //工序等级
        String craftGradeCode = dataObj.getString("craftGradeCode");
        if (StringUtils.isEmpty(craftGradeCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "工序等级为空");
        }
        //机器设备
        String machineCode = dataObj.getString("machineCode");
        if (StringUtils.isEmpty(machineCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "机器设备为空");
        }
        //宽放系数编码
        String allowanceCode = dataObj.getString("allowanceCode");
        if (StringUtils.isEmpty(allowanceCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "宽放系数编码为空");
        }
        //捆扎时间编码
        /*String strappingCode = dataObj.getString("strappingCode");
        if (StringUtils.isEmpty(strappingCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "捆扎时间编码为空");
        }*/
        //表面风格
        String isFabricStyleFix = dataObj.getString("isFabricStyleFix");
        if (StringUtils.isEmpty(isFabricStyleFix)) {
            return Result.build(MessageConstant.PARAM_NULL, "表面风格补贴为空");
        }
        //工艺标准
        /*JSONArray craftStdList = dataObj.getJSONArray("craftStdList");
        if (null == craftStdList || craftStdList.size() == 0) {
            return Result.build(MessageConstant.PARAM_NULL, "工艺标准为空");
        }*/

        //面料分值方案
        String fabricScorePlanCode = dataObj.getString("fabricScorePlanCode");
        if (StringUtils.isEmpty(fabricScorePlanCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "面料分值方案为空");
        }

        //固定时间
        String fixedTime = dataObj.getString("fixedTime");
        if (StringUtils.isEmpty(fixedTime)) {
            dataObj.put("fixedTime", "0");
        }
        //浮动时间
        String floatingTime = dataObj.getString("floatingTime");
        if (StringUtils.isEmpty(floatingTime)) {
            dataObj.put("floatingTime", "0");
        }
        //车缝长度
        String sewingLength = dataObj.getString("sewingLength");
        if (StringUtils.isEmpty(sewingLength)) {
            dataObj.put("sewingLength", "0");
        }
        //参数长度
        String paramLength = dataObj.getString("paramLength");
        if (StringUtils.isEmpty(paramLength)) {
            dataObj.put("paramLength", "0");
        }
        //标准时间
        String standardTime = dataObj.getString("standardTime");
        if (StringUtils.isEmpty(standardTime)) {
            dataObj.put("standardTime", "0");//默认设置为0
        }
        //标准单价
        String standardPrice = dataObj.getString("standardPrice");
        if (StringUtils.isEmpty(standardPrice)) {
            dataObj.put("standardPrice", "0");//默认设置为0
        }
        return Result.build(MessageConstant.SUCCESS, "参数校验OK");
    }

    /**
     * 获取缝边位置
     * param---服装品类的编码
     */
    @ApiOperation(value = "searchPartPosition", notes = "获取缝边位置的数据")
    @RequestMapping(value = "searchPartPosition", method = RequestMethod.GET)
    public Result<List<PartPosition>> searchPartPosition(@RequestParam(defaultValue = "1") Integer page,
                                                         @RequestParam(defaultValue = "30") Integer rows,
                                                         @RequestParam(required = false) String param) {
        PageHelper.startPage(page, rows);
        LOGGER.info("--------获取缝边位置参数:" + param);
        JSONObject obj = new JSONObject();
        List<PartPosition> data = new ArrayList<>(0);
        data = partPositionService.getSewingPartPositions(param);
        LOGGER.info("--------获取缝边位置结果:" + JSONObject.toJSONString(data));
        PageInfo<PartPosition> pageInfo = new PageInfo<>(data);
        return Result.build(MessageConstant.SUCCESS,
                MessageConstant.messageMap.get(MessageConstant.SUCCESS),
                pageInfo.getTotal(), data);
    }

    /**
     * 工位工序ES搜索
     */
    @ApiOperation(value = "searchWorkplaceCraft", notes = "工位工序ES搜索")
    @RequestMapping(value = "searchWorkplaceCraft", method = RequestMethod.GET)
    public Result<List<WorkplaceCraftEsBO>> searchWorkplaceCraft(@RequestParam(defaultValue = "1") Integer page,
                                                                 @RequestParam(defaultValue = "30") Integer rows,
                                                                 @RequestParam(required = false) String param) {
        LOGGER.info("--------工位工序搜索参数:" + param);
        //PageHelper.startPage(page, rows);
        JSONObject obj = new JSONObject();
        List<WorkplaceCraftEsBO> data = new ArrayList<>(0);
        List<WorkplaceCraftEsBO> result = new ArrayList<>(0);
        if (StringUtils.isEmpty(param)) {
            //参数为空，查询所有的
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
            //暂时只支持第一个条件查询，后续再优化通过空格区分的多个条件查询
            String par = param.toLowerCase();
            //工位工序关键字搜索：工位工序编码、名称、工序流，默认瀑布流全部数据展示
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                    .should(QueryBuilders.wildcardQuery("workplaceCraftCode", "*" + par + "*"))//工序品类
                    .should(QueryBuilders.matchQuery("workplaceCraftName", par))//工位工序名称
                    .should(QueryBuilders.matchQuery("craftFlowNum", par));//工序流
            Iterable<WorkplaceCraftEsBO> esData = workplaceCraftESRepository.search(boolQueryBuilder);
            if (null != esData) {
                for (WorkplaceCraftEsBO bo : esData) {
                    data.add(bo);
                }
            }
            LOGGER.info("--------工位工序搜索结果:" + JSONObject.toJSONString(data));

        } catch (Exception e) {
            LOGGER.info("--------工位工序搜索出现异常:" + e.getMessage());
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
        //进行分页
        int currIdx = (page > 1 ? (page - 1) * page : 0);
        List<WorkplaceCraftEsBO> result = new ArrayList<>(0);
        for (int i = 0; i < rows && i < data.size() - currIdx; i++) {
            WorkplaceCraftEsBO vo = data.get(currIdx + i);
            result.add(vo);
        }
        return result;
    }


    /**
     * 工艺标准ES搜索
     */
    @ApiOperation(value = "searchCraftStd", notes = "工艺标准ES搜索")
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
        LOGGER.info("--------工艺标准搜索参数:" + param);
        PageHelper.startPage(page, rows);
        JSONObject obj = new JSONObject();
        List<CraftStd> data = getCraftStdByParam(param, craftCategoryCode, craftPartCode, craftCategoryList, styleCode, craftStdCode, craftStdName);
       /* List<CraftStdEsBO> data = new ArrayList<>(0);
        if (StringUtils.isEmpty(param)) {.e
            //参数为空，查询所有的
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
            //暂时只支持第一个条件查询，后续再优化通过空格区分的多个条件查询
            String par = param.toLowerCase();
            //工位工序关键字搜索：工位工序编码、名称、工序流，默认瀑布流全部数据展示
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                    .should(QueryBuilders.wildcardQuery("craftStdCode", "*" + par + "*"))//工艺标准编码
                    .should(QueryBuilders.matchQuery("craftStdName", par));//工艺标准名称
            Iterable<CraftStdEsBO> esData = craftStdRepository.search(boolQueryBuilder);
            if (null != esData) {
                for (CraftStdEsBO bo : esData) {
                    data.add(bo);
                }
            }
            LOGGER.info("--------工艺标准搜索结果:" + JSONObject.toJSONString(data));

        } catch (Exception e) {
            LOGGER.info("--------工艺标准搜索出现异常:" + e.getMessage());
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

    @RequestMapping("/test")
    @ApiOperation(value = "test", notes = "发送")
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
     * 计算工序标准时间和标准单价
     */
    @RequestMapping(value = "/calSewingPriceAndTime", method = RequestMethod.POST)
    @ApiOperation(value = "calSewingPriceAndTime", notes = "计算工序标准时间和标准单价")
    public Result<JSONObject> calSewingPriceAndTime(HttpServletRequest request) throws Exception {
        String data = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        if (StringUtils.isEmpty(data)) {
            return Result.build(MessageConstant.PARAM_NULL, "参数为空");
        }
        LOGGER.info("计算工序标准时间和标准单价的参数：" + data);
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
            LOGGER.info("计算标准时间和标准单价异常：" + e.getMessage());
        }
        return Result.ok(MessageConstant.SUCCESS, result);
    }

    /**
     * 计算工序标准时间和标准单价
     */
    @RequestMapping(value = "/calAll", method = RequestMethod.POST)
    @ApiOperation(value = "calAll", notes = "款式工艺列表统一调用")
    public Result<JSONObject> calAll(HttpServletRequest request) throws Exception {
        String data = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        if (StringUtils.isEmpty(data)) {
            return Result.build(MessageConstant.PARAM_NULL, "参数为空");
        }
        LOGGER.info("款式工艺列表统一调用--计算工序标准时间和标准单价的参数：" + data);
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
            LOGGER.info("计算标准时间和标准单价异常：" + e.getMessage());
        }
        return Result.ok(MessageConstant.SUCCESS, result);
    }

    /**
     * 计算计算固定时间、浮动时间和车缝参数
     */
    @RequestMapping(value = "/calStandTimeAndPrice", method = RequestMethod.POST)
    @ApiOperation(value = "calStandTimeAndPrice", notes = "计算固定时间、浮动时间和车缝参数")
    public Result<JSONObject> calStandTimeAndPrice(HttpServletRequest request) throws Exception {
        String data = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        if (StringUtils.isEmpty(data)) {
            return Result.build(MessageConstant.PARAM_NULL, "参数为空");
        }
        LOGGER.info("计算时间的参数：" + data);
        JSONObject result = new JSONObject();
        try {
            JSONObject dataObj = JSONObject.parseObject(data);
            //转速
            BigDecimal rpm = dataObj.getBigDecimal("rpm");
            //针距
            BigDecimal stitchLength = dataObj.getBigDecimal("stitchLength");
            //频率

            JSONArray choosedList = dataObj.getJSONArray("choosedList");
            JSONArray notChoosedList = dataObj.getJSONArray("notChoosedList");
            List<String> motionCodeList = new ArrayList<>();
            JSONArray motionsList = new JSONArray();
            if (null == choosedList || choosedList.size() == 0) {
                //在动作代码那里没有打钩，则固定时间是0
                result.put("fixedTime", BigDecimal.ZERO.toString());
            } else {
                motionsList.addAll(choosedList);
                result.put("fixedTime", calMachineAndManualTime(notChoosedList, rpm, stitchLength, motionCodeList).intValue());
            }
            if (null == notChoosedList || notChoosedList.size() == 0) {
                //在动作代码那里全部打钩，则浮动时间是0
                result.put("floatingTime", BigDecimal.ZERO.toString());
            } else {
                motionsList.addAll(notChoosedList);
                result.put("floatingTime", calMachineAndManualTime(choosedList, rpm, stitchLength, motionCodeList).intValue());
            }
            //计算车缝长度
            //车缝长度=车缝动作代码（M开头的动作代码）的车缝长度*频率之和,非M开头的值就是0
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
            LOGGER.info("--------计算固定时间、浮动时间和车缝参数:" + e.getMessage());
            return Result.build(MessageConstant.PROGRAM_ERROR,
                    MessageConstant.messageMap.get(MessageConstant.PROGRAM_ERROR),
                    0L, null);
        }
        return Result.ok(MessageConstant.SUCCESS, result);

    }

    /**
     * 计算机器时间和人工时间之和
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
            //code为这=====种格式的直接过滤
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
    @ApiOperation(value = "/searchSewingCraftData", notes = "根据条件搜索车缝工序数据", httpMethod = "GET", response = Result.class)
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

    @ApiOperation(value = "exportSewingCraft", notes = "导出工序管理数据清单", httpMethod = "GET", response = Result.class)
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
        LOGGER.info("出工序管理数据清单参数是:  " + " keywords:" + keywords);
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
            return Result.ok("无数据导出");
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
            cell.setCellValue("工序编码");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("结构部件");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("工序名称");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("工序等级");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("机器设备");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("标准时间");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("标准单价");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("做工类型");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("宽放系数");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("创建人");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("创建时间");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("发布人");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("发布时间");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("状态");
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
            String fileName = URLEncoder.encode("工序管理清单.xlsx", "UTF-8");
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
            result = "草稿";
        } else if (BusinessConstants.Status.AUDITED_STATUS.equals(status)) {
            result = "已审核";
        } else if (BusinessConstants.Status.PUBLISHED_STATUS.equals(status)) {
            result = "已发布";
        } else if (BusinessConstants.Status.IN_VALID.equals(status)) {
            result = "删除";
        } else if (BusinessConstants.Status.INVALID_STATUS.equals(status)) {
            result = "失效";
        } else if (BusinessConstants.Status.NOT_ACTIVE_STATUS.equals(status)) {
            result = "未生效";
        }
        return result;
    }

    @ApiOperation(value = "getSewingCraftRandomCode", notes = "获取工序randomCode", httpMethod = "GET", response = Result.class)
    @RequestMapping(value = "/getSewingCraftRandomCode", method = RequestMethod.GET)
    public Result getSewingCraftRandomCode(
            @RequestParam(name = "craftCode", required = true) String craftCode) throws Exception {
        return sewingCraftWarehouseService.getSewingCraftRandomCode(craftCode);
    }


    @ApiOperation(value = "batchSendCraft", notes = "通过工序编码模糊查找批量发送工序给ME", httpMethod = "GET", response = Result.class)
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
        LOGGER.info("工序发送给PI的数据是:" + JSONObject.toJSONString(obj));
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
                data.put("gxdm", sewingCraftWarehouse.getCraftCode());//工序编号
                String desc = sewingCraftWarehouse.getDescription();
                if (StringUtils.isNotEmpty(desc) && desc.length() > 20) {
                    desc = desc.substring(0, 20);
                }
                data.put("gxmc", desc);//工序描述
                data.put("jqdm", sewingCraftWarehouse.getMachineCode());//设备类型
                String machineName = sewingCraftWarehouse.getMachineName();
                if (StringUtils.isNotEmpty(machineName) && machineName.length() > 20) {
                    machineName = machineName.substring(0, 20);
                }
                data.put("jqmc", machineName);//设备名称
                data.put("SMV", sewingCraftWarehouse.getStandardTime());//标准时间
                data.put("dj", sewingCraftWarehouse.getStandardPrice());//工价
                data.put("zglb", sewingCraftWarehouse.getMakeTypeCode());//做工类别
                if (StringUtils.isNotBlank(sewingCraftWarehouse.getMakeTypeCode())) {
                    MakeType makeType = makeTypeMap.getOrDefault(sewingCraftWarehouse.getMakeTypeCode(), null);
                    if (makeType != null) {
                        String makeTypeName = makeType.getMakeTypeName();
                        if (StringUtils.isNotEmpty(makeTypeName) && makeTypeName.length() > 20) {
                            makeTypeName = makeTypeName.substring(0, 20);
                        }
                        data.put("zgmc", makeTypeName);//做工名称
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
                    data.put("craftCategoryCode", craft_category_codes);//工艺品类代码craft_category_code[i]
                }

                if (StringUtils.isNotEmpty(craft_category_names) && craft_category_names.length() > 20) {
                    data.put("craftCategoryName", craft_category_names.substring(0, 20));//工艺品类名称craft_category_name[i]
                } else {
                    data.put("craftCategoryName", craft_category_names);//工艺品类名称craft_category_name[i]
                }
                data.put("gybj", craftPartsCodes);//工艺品类代码craftPartsCode[i]

                if (StringUtils.isNotEmpty(craftPartsNames) && craftPartsNames.length() > 20) {
                    data.put("gybjmc", craftPartsNames.substring(0, 20));//工艺品类名称craftPartsName[i]
                } else {
                    data.put("gybjmc", craftPartsNames);//工艺品类名称craftPartsName[i]
                }
                String sameCraft = sewingCraftWarehouse.getSameLevelCraftNumericalCode();
                if (StringUtils.isNotEmpty(sameCraft)) {
                    data.put("sjgxdm", sameCraft);//同级工序代码
                } else {
                    data.put("sjgxdm", "");//同级工序代码
                }

                if (sameLevelCraftMap.containsKey(sameCraft)) {
                    data.put("sjgxmc", sameLevelCraftMap.get(sameCraft).getSameLevelCraftName());//同级工序名称
                    data.put("hardLevel", sameLevelCraftMap.get(sameCraft).getHardLevel()); //难度等级
                } else {
                    data.put("sjgxmc", "");
                }
                data.put("bjdm", "");//设计部件
                data.put("bjmc", "");//设计部件名称
                data.put("wxbz", "N");//是否外协
                data.put("sgzdm", sewingCraftWarehouse.getCraftCode());//上岗证代码 取工序编码
                String aaa = sewingCraftWarehouse.getCraftName();
                if (StringUtils.isNotEmpty(aaa) && aaa.length() > 20) {
                    aaa = aaa.substring(0, 20);
                }
                data.put("sgzmc", aaa);//上岗证名称取工序名称
                data.put("cutSign", "N");//裁剪标志(Y/N) 通过工序管理触发的赋值“N”
                if (StaticDataCache.WORK_TYPE_DICTIONARY_MAP.containsKey(sewingCraftWarehouse.getWorkTypeCode())) {
                    String section = StaticDataCache.WORK_TYPE_DICTIONARY_MAP.get(sewingCraftWarehouse.getWorkTypeCode()).getRemark();
                    if (StringUtils.isNotEmpty(section)) {
                        String sec[] = section.split("_");
                        data.put("bmdm", sec[0]);//工种代码
                        data.put("bmmc", sec[1]);//工种名称
                    }
                }
                if (null != sewingCraftWarehouseWorkplaces && sewingCraftWarehouseWorkplaces.size() > 0) {
                    for (SewingCraftWarehouseWorkplace workplace : sewingCraftWarehouseWorkplaces) {
                        if (workplace.getCraftCategoryCode().equals(craft_category_code[i])) {
                            data.put("main_frame_code", workplace.getMainFrameCode());//默认主框架代码
                            data.put("main_frame_name", workplace.getMainFrameName());//默认主框架名称
                            if (StringUtils.isNotEmpty(workplace.getProductionPartCode())) {
                                String partCode = workplace.getProductionPartCode();
                                Map<String, ProductionPart> partMap = productionPartService.getMapByMainFrameCode(workplace.getMainFrameCode());
                                if (partMap.containsKey(workplace.getProductionPartCode())) {
                                    data.put("scbj", partMap.get(workplace.getProductionPartCode()).getCustomProductionAreaCode());//生产部件
                                }

                            } else {
                                data.put("scbj", "");//生产部件
                            }
                            if (StringUtils.isNotEmpty(workplace.getProductionPartName())) {
                                data.put("scbjmc", workplace.getProductionPartName());//生产部件名称
                            } else {
                                data.put("scbjmc", "");//生产部件名称
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
                data.put("gjdm", toolCode);//辅助工具
                if (StringUtils.isNotEmpty(toolName) && toolName.length() > 20) {
                    toolName = toolName.substring(0, 20);
                }
                data.put("gjmc", toolName);//工具名称
                if (StringUtils.isNotEmpty(qualitySpec) && qualitySpec.length() > 20) {
                    qualitySpec = qualitySpec.substring(0, 20);
                }
                if (StringUtils.isNotEmpty(makeDescription) && makeDescription.length() > 20) {
                    makeDescription = makeDescription.substring(0, 20);
                }
                data.put("pzsm", qualitySpec);//品质说明
                data.put("zgsm", makeDescription);//工艺要求
                data.put("fjPath", picUrl);//图片Url
                data.put("videoURL", vedioUrl);//视频URL
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
