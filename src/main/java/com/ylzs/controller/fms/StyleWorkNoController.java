package com.ylzs.controller.fms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.constant.OderProcessingStatusConstants;
import com.ylzs.common.util.Result;
import com.ylzs.common.util.SnowflakeIdUtil;
import com.ylzs.common.util.StringUtils;
import com.ylzs.common.utils.object.ObjectUtils;
import com.ylzs.entity.bigticketno.FmsTicketNo;
import com.ylzs.entity.custom.CustomStyleCraftCourse;
import com.ylzs.entity.custom.CustomStylePartCraft;
import com.ylzs.entity.factory.ProductionGroup;
import com.ylzs.entity.fms.*;
import com.ylzs.entity.staticdata.CraftGrade;
import com.ylzs.entity.staticdata.CraftGradeEquipment;
import com.ylzs.service.bigticketno.FmsTicketNoService;
import com.ylzs.service.custom.ICustomStyleCraftCourseService;
import com.ylzs.service.custom.ICustomStylePartCraftService;
import com.ylzs.service.custom.ICustomStylePartService;
import com.ylzs.service.factory.IProductionGroupService;
import com.ylzs.service.fms.IStyleProductionWorkInformationService;
import com.ylzs.service.orderprocessing.OrderProcessingStatusService;
import com.ylzs.service.sewingcraft.SewingCraftWarehouseService;
import com.ylzs.service.timeparam.CraftGradeEquipmentService;
import com.ylzs.vo.sewing.VSewingCraftVo;
import com.ylzs.web.OriginController;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @className StyleWorkNoController
 * @Description
 * @Author sky
 * @create 2020-04-24 10:05:41
 */
@Api(tags = "生产工单WEB层")
@RestController
@RequestMapping(value = "/styleWork")
public class StyleWorkNoController extends OriginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StyleWorkNoController.class);

    @Autowired
    private IStyleProductionWorkInformationService styleProductionWorkInformationService;
    @Autowired
    private ICustomStyleCraftCourseService customStyleCraftCourseService;
    @Autowired
    private IProductionGroupService productionGroupService;
    @Autowired
    private CraftGradeEquipmentService craftGradeEquipmentService;
    @Autowired
    private ICustomStylePartService customStylePartService;
    @Autowired
    private ICustomStylePartCraftService customStylePartCraftService;
    @Autowired
    private SewingCraftWarehouseService sewingCraftWarehouseService;
    @Autowired
    private CraftGradeEquipmentService gradeEquipmentService;
    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Resource
    private FmsTicketNoService fmsTicketNoService;

    @Resource
    private OrderProcessingStatusService orderProcessingStatusService;


    private static List<CraftGrade> craftGradeList = new ArrayList<>();

    @RequestMapping(value = "/getProductionWorkOrderInformation", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "/getProductionWorkOrderInformation", httpMethod = "POST", response = Result.class)
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", dataType = "MessageParam", name = "data", value = "信息参数", required = true)})
    public Result getProductionWorkOrderInformation(HttpServletRequest request) {
        LOGGER.info("接收fms生产工单信息==============>>>>>>>>>>>>>>>");
        try {
            String data = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
            data = data.replace(" ", "");
            data = data.replace("\n", "");
            if (StringUtils.isEmpty(data)) {
                return Result.build(BusinessConstants.CommonConstant.ZERO, "数据不能为空");
            }
            if (ObjectUtils.isEmptyList(craftGradeList)) {
                craftGradeList = craftGradeEquipmentService.getCraftGradeAll();
            }
            StyleProductionWorkInformation workInformation = new StyleProductionWorkInformation();
            workInformation.setRandomCode(SnowflakeIdUtil.generateId());
            workInformation.setRequestParam(data);
            styleProductionWorkInformationService.save(workInformation);
            ProductionOrders productionOrders = JSONObject.parseObject(data, ProductionOrders.class);
            List<Items> itemsList = productionOrders.getItems();
            Map<String, ProductionGroup> map = productionGroupService.getAllToMap();
            for (Items items : itemsList) {
                Item item = items.getItem();
                String orderLineNo = "1";
                if (StringUtils.isNotEmpty(item.getMtmorder()) && StringUtils.isNotEmpty(item.getExtwg()) &&
                        (item.getExtwg().equalsIgnoreCase("02") ||
                                item.getExtwg().equalsIgnoreCase("03") ||
                                item.getExtwg().equalsIgnoreCase("04"))) {
                    OrderItem orderItem = null;
                    if (ObjectUtils.isNotEmptyList(item.getOrderItem())) {
                        orderItem = item.getOrderItem().get(0);
                    }

                    if (ObjectUtils.isNotEmpt(orderItem)) {
                        int orderLiNo = Integer.valueOf(orderItem.getZmtmitm());
                        orderLineNo = String.valueOf(orderLiNo);
                    }
                    CustomStyleCraftCourse course = customStyleCraftCourseService.getOrderCustomStyleByVersion(item.getMtmorder(), orderLineNo, "1.0");
                    if (ObjectUtils.isNotEmpt(course)) {
                        LOGGER.info(String.format("生产工单接口-当前订单号:%s, 当前生产工单号:%s", course.getOrderNo() + "-" + course.getOrderLineId(), item.getAufnr()));
                        ProductionGroup productionGroup = productionGroupService.getProductCodeByNameAndCraftCatetory(item.getWorkCenterId(), course.getCraftCategoryCode());
//                        //可能 还是要根据工艺品类和生产组别一起去找
//                        List<ProductionGroup>groups = productionGroupService.getProductCodeByName(item.getWorkCenterId());
//                        if(null != groups && groups.size()>0){
//                            productionGroup = groups.get(0);
//                        }
                        if (ObjectUtils.isNotEmpt(productionGroup)) {
                            course.setProductionCategory(productionGroup.getProductionGroupName());
                            course.setMainFrameCode(productionGroup.getMainFrameCode());
                            course.setMainFrameName(productionGroup.getMainFrameName());
                            //异常状态下的订单状态不能  发布
                            if (!BusinessConstants.Status.ORDER_EXCEPTION.equals(course.getStatus())) {
                                course.setStatus(BusinessConstants.Status.PUBLISHED_STATUS);
                            }
                            course.setReleaseUser("plm");
                            course.setRemark("系统自动发布");
                            course.setReleaseTime(new Date());
                            List<Long> stylePartsListRandomCode = customStylePartService.getCustomStyleRandomCodeByMainRnadomCode(course.getRandomCode());
                            if (ObjectUtils.isNotEmpt(stylePartsListRandomCode)) {
                                List<CustomStylePartCraft> craftList = customStylePartCraftService.getPartRandomCodeCraftList(stylePartsListRandomCode);
                                if (ObjectUtils.isNotEmptyList(craftList)) {
                                    String[] craftCodeArray = new String[craftList.size()];
                                    for (int i = 0; i < craftList.size(); i++) {
                                        craftCodeArray[i] = craftList.get(i).getCraftCode();
                                    }
                                    List<VSewingCraftVo> sewingCraftVos = sewingCraftWarehouseService.getCraftCodeCraftFlowNumDataAll(craftCodeArray, productionGroup.getMainFrameCode());
                                    for (CustomStylePartCraft craft : craftList) {
                                        LOGGER.info(String.format("生产工单接口-当前订单号:%s, 原订单工厂编码:%s", course.getOrderNo() + "-" + course.getOrderLineId(), course.getFactoryCode()));
                                        CraftGradeEquipment equipment = gradeEquipmentService.getCraftGradeEquipment(item.getWerks(), craft.getCraftGradeCode());
                                        LOGGER.info(String.format("生产工单接口-当前工序编码:%s,标准时间%s,计算前标准单价:%s", craft.getCraftCode(), craft.getStandardTime(), craft.getStandardPrice()));
                                        LOGGER.info(String.format("生产工单接口-接收到工单工厂信息:%s", JSON.toJSON(equipment)));
                                        craft.setStandardPrice(craft.getStandardTime().multiply(equipment.getMinuteWage()).setScale(3, BigDecimal.ROUND_HALF_UP));
                                        LOGGER.info(String.format("生产工单接口-当前工序编码:%s,计算后标准单价:%s", craft.getCraftCode(), craft.getStandardPrice()));
                                        for (VSewingCraftVo sewingCraftVo : sewingCraftVos) {
                                            if (craft.getCraftCode().equalsIgnoreCase(sewingCraftVo.getCraftCode())) {
                                                if (StringUtils.isNotBlank(sewingCraftVo.getCraftFlowNum())) {
                                                    craft.setCraftFlowNum(sewingCraftVo.getCraftFlowNum());
                                                } else {
                                                    craft.setCraftFlowNum("");
                                                }
                                            }
                                        }
                                    }

                                    BigDecimal totalStandardTime = BigDecimal.ZERO.setScale(3);
                                    BigDecimal totalStandardPrice = BigDecimal.ZERO.setScale(3);
                                    for (CustomStylePartCraft craft : craftList) {
                                        totalStandardTime = totalStandardTime.add(craft.getStandardTime()).setScale(3, BigDecimal.ROUND_HALF_UP);
                                        totalStandardPrice = totalStandardPrice.add(craft.getStandardPrice()).setScale(3, BigDecimal.ROUND_HALF_UP);
                                    }
                                    course.setStanderTime(totalStandardTime);
                                    course.setTotalPrice(totalStandardPrice);
                                    customStylePartCraftService.updateBatchById(craftList);

                                }
                            }
                        }

                        course.setProductionTicketNo(item.getAufnr());
                        course.setFactoryCode(item.getWerks());
                        if (ObjectUtils.isNotEmptyList(craftGradeList)) {
                            for (CraftGrade craftGrade : craftGradeList) {
                                if (craftGrade.getFactoryCode().equalsIgnoreCase(item.getWerks())) {
                                    course.setFactoryName(craftGrade.getFactoryName());
                                    break;
                                }
                            }
                        } else {
                            course.setFactoryName(item.getWerks());
                        }
                        Boolean bol = customStyleCraftCourseService.updateById(course);
                        if (bol) {
                            threadPoolTaskExecutor.submit(() -> {
                                customStyleCraftCourseService.releaseCustomStyle(course);
                            });

                        }
                    }
                }

                //ORDER_ITEM 数组存在表示正常工单下发、GSMNG = 0.000 表示工单取消，CAPP只需要此两种情况的数据
                boolean updateFlag = false;
                try {
                    if (StringUtils.isNotEmpty(item.getGsmng())) {
                        BigDecimal gsmng = new BigDecimal(item.getGsmng());
                        if (BigDecimal.ZERO.compareTo(gsmng) == 0) {
                            updateFlag = true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                List<OrderItem> list = item.getOrderItem();
                if (null != list && list.size() > 0) {
                    updateFlag = true;
                }
                try {
                    if (updateFlag) {
                        FmsTicketNo no = change(item, map);
                        no.setOrderLineId(orderLineNo);
                        //把定制工单和大货工单存到fms_ticket_no
                        fmsTicketNoService.addOrUpdateData(no);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    //更新接收工单信息节点信息
                    orderProcessingStatusService.addOrUpdate(item.getMatnr(), OderProcessingStatusConstants.BigStyleStatusName.BIG_STYLE_NAME_1260, OderProcessingStatusConstants.BigStyleStatus.BIG_STYLE_1260, "", "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Result.ok();
    }

    private FmsTicketNo change(Item item, Map<String, ProductionGroup> map) throws Exception {
        FmsTicketNo no = new FmsTicketNo();
        no.setCreateTime(new Date());
        no.setUpdateTime(new Date());
        no.setProductionTicketNo(item.getAufnr());
        no.setStyleCode(item.getMatnr());
        no.setStyleType(item.getExtwg());
        no.setClothesCategoryCode(item.getMatkl());
        no.setBrand(item.getBrandId());
        no.setFactoryCode(item.getWerks());
        no.setProductionCategory(item.getWorkCenterId());
        if (map.containsKey(item.getWorkCenterId())) {
            no.setProductionCategoryName(map.get(item.getWorkCenterId()).getProductionGroupName());
        }
        no.setUnit(item.getGmein());
        BigDecimal value = BigDecimal.ZERO;
        List<OrderItem> list = item.getOrderItem();
        if (null != list && list.size() > 0) {
            for (OrderItem oo : list) {
                value = value.add(oo.getGsmng()).setScale(3, BigDecimal.ROUND_HALF_UP);
            }
        }
        no.setNumber(value.setScale(0, BigDecimal.ROUND_HALF_UP).toPlainString());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        if (StringUtils.isNotEmpty(item.getGstrs())) {
            no.setStartDay(sdf.parse(item.getGstrs()));
        }
        if (StringUtils.isNotEmpty(item.getGltrs())) {
            no.setEndDay(sdf.parse(item.getGltrs()));
        }
        no.setStartTime(item.getGsuzs());
        no.setEndTime(item.getGluzs());
        no.setStyleCodeColor(item.getPlnbez());
        no.setMtmOrder(item.getMtmorder());
        if (StringUtils.isNotEmpty(item.getMtmorder())) {
            no.setCustomStyleCode(item.getMatnr());
        }
        no.setAdaptCode(item.getQualf());
        return no;
    }
}
