package com.ylzs.controller.orderprocessing;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ylzs.common.constant.MessageConstant;
import com.ylzs.common.util.Result;
import com.ylzs.common.util.StringUtils;
import com.ylzs.common.utils.object.ObjectUtils;
import com.ylzs.controller.sewingcraft.SewingCraftWarehouseController;
import com.ylzs.entity.bigstylecraft.BigStyleAnalyseMaster;
import com.ylzs.entity.custom.CustomStyleCraftCourse;
import com.ylzs.entity.custom.CustomStylePart;
import com.ylzs.entity.orderprocessing.OrderProcessingStatus;
import com.ylzs.entity.plm.PICustomOrder;
import com.ylzs.entity.plm.PICustomOrderPart;
import com.ylzs.entity.system.User;
import com.ylzs.service.custom.*;
import com.ylzs.service.orderprocessing.OrderProcessingStatusService;
import com.ylzs.service.plm.IPICustomOrderPartService;
import com.ylzs.service.plm.IPICustomOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author xuwei
 * @create 2020-07-18 10:02
 */
@Api(tags = "订单状态节点")
@RestController
@RequestMapping("/orderprocessing")
public class OrderProcessingStatusController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderProcessingStatusController.class);
    @Resource
    private OrderProcessingStatusService orderProcessingStatusService;

    @Resource
    private IPICustomOrderService piCustomOrderService;

    @Autowired
    private ICustomStyleCraftCourseService customStyleCraftCourseService;

    @Resource
    private ICustomStylePartService customStylePartService;

    @Resource
    ICustomStyleRuleService customStyleRuleService;

    @Resource
    private ICustomStyleSewPositionService customStyleSewPositionService;

    @Resource
    private IPICustomOrderPartService piCustomOrderPartService;

    @Resource
    private ICustomStylePartCraftService customStylePartCraftService;

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "getAll", notes = "查询订单状态节点")
    public Result<List<OrderProcessingStatus>> getAll(@RequestParam(defaultValue = "1") Integer page,
                                                      @RequestParam(defaultValue = "30") Integer rows,
                                                      @RequestParam(name = "orderNo", required = false) String orderNo,
                                                      @RequestParam(name = "processingStatusName", required = false) String processingStatusName,
                                                      @RequestParam(name = "processingStatus", required = false) Integer processingStatus,
                                                      @RequestParam(name = "createTimeBeginDate", required = false) String createTimeBeginDate,
                                                      @RequestParam(name = "createTimeEndDate", required = false) String createTimeEndDate) {

        LOGGER.info("列表页面查询参数是: page" + page + " rows:" + rows + " orderNo:" + orderNo + " processingStatusName:" + processingStatusName + " processingStatus:" + processingStatus);
        Map<String, Object> param = new HashMap<>();
        param.put("orderNo", orderNo);
        param.put("processingStatusName", processingStatusName);
        param.put("processingStatus", processingStatus);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (StringUtils.isNotEmpty(createTimeBeginDate)) {
                Date createTimeBegin = sdf.parse(createTimeBeginDate);
                param.put("createTimeBeginDate", createTimeBegin);
            }
            if (StringUtils.isNotEmpty(createTimeEndDate)) {
                Date createTimeEnd = sdf.parse(createTimeEndDate);
                param.put("createTimeEndDate", createTimeEnd);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        PageHelper.startPage(page, rows);
        List<OrderProcessingStatus> data = orderProcessingStatusService.getDataByParam(param);
        PageInfo<OrderProcessingStatus> pageInfo = new PageInfo<>(data);
        return Result.ok(data, pageInfo.getTotal());
    }

    @RequestMapping(value = "/reCreateOrder", method = RequestMethod.GET)
    @ApiOperation(value = "reCreateOrder", notes = "查询订单状态节点")
    public Result<JSONObject> reCreateOrder(@RequestParam(name = "orderNo", required = false) String orderNo) {
        JSONObject result = new JSONObject();
        if (StringUtils.isNotEmpty(orderNo) && orderNo.indexOf("-") != -1) {
            String order11[] = orderNo.split("-");
            if (order11 != null && order11.length == 2) {
                //先把老的订单数据全部删除干净
                List<CustomStyleCraftCourse> courses = customStyleCraftCourseService.getOrderCustmStyleBaseList(order11[0], order11[1]);
                List<Long> couresRandomList = new ArrayList<>();
                if (courses != null && courses.size() > 0) {
                    couresRandomList = courses.stream().map(x -> {
                        return x.getRandomCode();
                    }).collect(Collectors.toList());
                    if (null != couresRandomList && couresRandomList.size() > 0) {
                        List<CustomStylePart> parts = customStylePartService.getDataByMainRandomList(couresRandomList);
                        if (null != parts && parts.size() > 0) {
                            List<Long> partsRandomList = new ArrayList<>();
                            partsRandomList = parts.stream().map(x -> {
                                return x.getRandomCode();
                            }).collect(Collectors.toList());
                            //删除custom_style_part
                            if (null != partsRandomList && partsRandomList.size() > 0) {
                                customStylePartService.deleteBatchCustomStylePart(partsRandomList);
                                //删除custom_style_part_craft
                                customStylePartCraftService.deleteCustomStyleCraftList(partsRandomList);
                            }
                        }
                        //删除custom_style_rule
                        customStyleRuleService.deleteBatchCustomStyleRule(couresRandomList);
                        //删除删除custom_style_sew_position
                        customStyleSewPositionService.deleteBatchCustomStylePosition(couresRandomList);
                        //删除custom_style_craft_course
                        customStyleCraftCourseService.deleteByRandomList(couresRandomList);
                    }
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (Exception e) {

                }
                PICustomOrder piCustomOrder = piCustomOrderService.getOrderId(order11[0], order11[1]);
                if (null != order11) {
                    List<PICustomOrderPart> PICustomOrderPartList = piCustomOrderPartService.getOrderAll(order11[0], order11[1]);
                    if (ObjectUtils.isNotEmptyList(PICustomOrderPartList)) {
                        System.out.println("进入方法,参数：" + piCustomOrder.toString());
                        piCustomOrder.setUnits(PICustomOrderPartList);
                        try {
                            customStyleCraftCourseService.createCustomStyleCratCouresData(piCustomOrder);
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                }
            }

        }


        return Result.ok(MessageConstant.SUCCESS, result);
    }


}
