package com.ylzs.controller.plm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.ylzs.common.bo.ResultObject;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.constant.MessageConstant;
import com.ylzs.common.constant.OderProcessingStatusConstants;
import com.ylzs.common.util.PiDataUtil;
import com.ylzs.common.util.SnowflakeIdUtil;
import com.ylzs.common.util.StringUtils;
import com.ylzs.common.utils.object.ObjectUtils;
import com.ylzs.entity.craftstd.PartPosition;
import com.ylzs.entity.plm.FabricMainData;
import com.ylzs.entity.plm.PICustomOrder;
import com.ylzs.entity.plm.PICustomOrderPart;
import com.ylzs.entity.plm.PICustomOrderPartMaterial;
import com.ylzs.entity.receivepilog.ReceivePiLog;
import com.ylzs.service.custom.ICustomStyleCraftCourseService;
import com.ylzs.service.orderprocessing.OrderProcessingStatusService;
import com.ylzs.service.plm.IFabricMainDataService;
import com.ylzs.service.plm.IPICustomOrderPartMaterialService;
import com.ylzs.service.plm.IPICustomOrderPartService;
import com.ylzs.service.plm.IPICustomOrderService;
import com.ylzs.service.receivepilog.IReceivePiLogService;
import com.ylzs.service.staticdata.PartPositionService;
import com.ylzs.vo.system.DataChild;
import com.ylzs.vo.system.DataParent;
import org.apache.commons.io.IOUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @Author: watermelon.xzx
 * @Description: PI传入定制订单
 * @Date: Created in 15:38 2020/3/19
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping(value = "/customOrderData")
public class PICustomOrderDataComtroller {
    @Resource
    private IPICustomOrderService ipiCustomOrderService;

    @Resource
    private IPICustomOrderPartService ipiCustomOrderPartService;

    @Resource
    private IPICustomOrderPartMaterialService ipiCustomOrderPartMaterialService;

    @Resource
    private IReceivePiLogService receivePiLogService;

    @Resource
    private PartPositionService partPositionService;

    @Resource
    private IFabricMainDataService iFabricMainDataService;

    @Resource
    private OrderProcessingStatusService orderProcessingStatusService;

    @Resource
    private ThreadPoolTaskExecutor taskExecutor;

    @Resource
    private ICustomStyleCraftCourseService customStyleCraftCourseService;



    @ResponseBody
    @RequestMapping("/getCustomOrderData")
    public ResultObject getPartMainData(HttpServletRequest request) throws Exception {

        String data = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        if (StringUtils.isEmpty(data)) {
            return ResultObject.createErrorInstance(MessageConstant.Request_Method_Not_Allowed, MessageConstant.STATUS_TEXT_DATA_NULL);
        }
        ReceivePiLog receivePiLog = new ReceivePiLog();
        Date startDate = new Date();
        receivePiLog.setCreateTime(startDate);
        receivePiLog.setStartTime(startDate);
        receivePiLog.setUpdateTime(startDate);
        receivePiLog.setData(data);
        receivePiLog.setReceiveType(BusinessConstants.ReceivePiLog.receiveType_mtmorder);

        // 1、解析data数据
        DataParent<PICustomOrder> parent = (DataParent<PICustomOrder>) JSON.parseObject(data, new TypeReference<DataParent<PICustomOrder>>() {
        });

        // 2、获取count
        String countStr = parent.getCount();
        Integer count = 0;
        try {
            count = Integer.parseInt(countStr);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return ResultObject.createErrorInstance(MessageConstant.SERVER_FIELD_ERROR, e.getMessage());
        }
        // 3、获取主数据
        PiDataUtil<PICustomOrder> util = new PiDataUtil<PICustomOrder>();
        List<DataChild<PICustomOrder>> children = util.getChildData(parent, PICustomOrder.class);//items中的数据

        if (null == children || children.size() == 0) {
            ResultObject resultObject = new ResultObject();
            resultObject.setData(null);
            resultObject.setErrorCode(200);
            resultObject.setErrorMessage("success");
            return resultObject;
        }

        // 4、接收到的数据条数与count不对，抛出异常
        if (count != children.size()) {
            return ResultObject.createErrorInstance(MessageConstant.Request_Method_Not_Allowed, MessageConstant.STATUS_TEXT_Error_Count);
        }


        // 5、存入到数据库异常，抛出异常
        List<PICustomOrder> result = new ArrayList<PICustomOrder>(0);
        List<PICustomOrder> piCustomOrderList = new ArrayList<>();
        List<PICustomOrderPart> piCustomOrderPartList = new ArrayList<>();
        List<PICustomOrderPartMaterial> piCustomOrderPartMaterialList = new ArrayList<>();

        /*List<FabricMainData> allFabricDataList = iFabricMainDataService.getAllFabricData();//获取全部面料数据
        Map<String, FabricMainData> fabricDataMap = new HashMap<>();
        for (FabricMainData fabricMainData : allFabricDataList) {
            fabricDataMap.put(fabricMainData.getCode(), fabricMainData);//存入Map
        }*/

        List<PICustomOrderPart> newPiCustomOrderParts = new ArrayList<>();
        //List<PartPosition> partPositionList = partPositionService.getAll();
        List<PartPosition> partPositionList = partPositionService.getPartPositionByType("bjposition");
        Map<String, Object> partPositionMap = new HashMap();
        for (PartPosition partPosition : partPositionList) {
            partPositionMap.put(partPosition.getPartPositionCode(), partPosition);
        }
        for (DataChild<PICustomOrder> child : children) {
            PICustomOrder piCustomOrder = child.getItem();

            try {
                if (StringUtils.isNotBlank(piCustomOrder.getOrderId()) && StringUtils.isNotBlank(piCustomOrder.getOrderLineId())) {
                    piCustomOrder.setReceiveTime(new Date());
                    piCustomOrderList.add(piCustomOrder);
                    ipiCustomOrderService.addCustomOrder(piCustomOrderList);
                    Integer status = OderProcessingStatusConstants.CusOrderStatus.CUS_ORDER_1100;
                    String statusName = OderProcessingStatusConstants.CusOrderStatusName.CUS_ORDER_NAME_1100;
                    if ("20".equals(piCustomOrder.getOrderType())) {
                        status = OderProcessingStatusConstants.CusOrderStatus.CUS_ORDER_1110;
                        statusName = OderProcessingStatusConstants.CusOrderStatusName.CUS_ORDER_NAME_1110;
                    }
                    try {
                        //更新节点信息接收定制订单  接收靠码定制订单
                        orderProcessingStatusService.addOrUpdate(piCustomOrder.getOrderId() + "-" + piCustomOrder.getOrderLineId(), statusName, status, "", "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                List<PICustomOrderPart> piCustomOrderParts = piCustomOrder.getUnits();
                Map<String, String> partMap = new HashMap<>();
                if (null != piCustomOrderParts && piCustomOrderParts.size() > 0) {
                    for (PICustomOrderPart piCustomOrderPart : piCustomOrderParts) {//除去部件list里面垃圾数据
                        if (StringUtils.isNotBlank(piCustomOrderPart.getUnitCode())) {
                            newPiCustomOrderParts.add(piCustomOrderPart);
                        }
                    }

                    for (PICustomOrderPart piCustomOrderPart : newPiCustomOrderParts) {
                        long parentRandom = SnowflakeIdUtil.generateId();
                        Map<String, String> positionMap = new HashMap<>();
                        List<PICustomOrderPartMaterial> piCustomOrderPartMaterials = piCustomOrderPart.getUms();
                        //如果没有 一个 可用位置，则放一个空位置
                        //可用位置标识，默认是没有可用位置
                        boolean flag = false;
                        for (PICustomOrderPartMaterial piCustomOrderPartMaterial : piCustomOrderPartMaterials) {
                            //不在partPositionMap里面的位置，才是有效位置
                            if (StringUtils.isNotBlank(piCustomOrderPartMaterial.getPositionCode()) && !partPositionMap.containsKey(piCustomOrderPartMaterial.getPositionCode())) {//缝边位置和MTM位置去重
                                positionMap.put(piCustomOrderPartMaterial.getPositionCode(), "");
                                partMap.put(piCustomOrderPart.getUnitCode(), "");
                                flag = true;
                            }
                            if (StringUtils.isNotBlank(piCustomOrderPartMaterial.getMtmPositionCode()) && !partPositionMap.containsKey(piCustomOrderPartMaterial.getMtmPositionCode())) {
                                positionMap.put(piCustomOrderPartMaterial.getMtmPositionCode(), "");
                                partMap.put(piCustomOrderPart.getUnitCode(), "");
                                flag = true;
                            }
                        }
                        //flag等于false就是没有可用位置，则要放一个空位置，我们用特殊AAAAAA标识，到后再把AAAAAA替换成空字符串
                        //如果
                        if (!flag) {
                            positionMap.put("AAAAAA", "");
                        }
                        for (String key : positionMap.keySet()) {
                            PICustomOrderPart piCustomOrderPart1 = new PICustomOrderPart();
                            piCustomOrderPart1.setRandomCode(parentRandom);
                            piCustomOrderPart1.setOrderId(piCustomOrder.getOrderId());
                            piCustomOrderPart1.setOrderLineId(piCustomOrder.getOrderLineId());
                            piCustomOrderPart1.setReceiveTime(new Date());
                            if ("AAAAAA".equals(key)) {
                                piCustomOrderPart1.setUnitPositionCode(null);
                            } else {
                                piCustomOrderPart1.setUnitPositionCode(key);
                            }
                            piCustomOrderPart1.setUnitCode(piCustomOrderPart.getUnitCode());
                            piCustomOrderPartList.add(piCustomOrderPart1);

                        }


                        for (PICustomOrderPartMaterial piCustomOrderPartMaterial : piCustomOrderPartMaterials) {
                            if (StringUtils.isNotBlank(piCustomOrderPartMaterial.getCode())) {
                                if (piCustomOrderPartMaterial.getCode() != null && piCustomOrderPartMaterial.getCode() != "") {
                                    if (piCustomOrder.getMainMaterialCode() != null && piCustomOrder.getMainMaterialCode() != "") {
                                        if (piCustomOrderPartMaterial.getCode().equals(piCustomOrder.getMainMaterialCode())) {
                                            piCustomOrderPartMaterial.setIsMain(true);
                                        }
                                    }
                                }
                                piCustomOrderPartMaterial.setOrderId(piCustomOrder.getOrderId());
                                piCustomOrderPartMaterial.setOrderLineId(piCustomOrder.getOrderLineId());
                                piCustomOrderPartMaterial.setParentRandomCode(parentRandom);
                                if (StringUtils.isNotBlank(piCustomOrder.getMainMaterialCode())) {
                                    if (piCustomOrder.getMainMaterialCode().equals(piCustomOrderPartMaterial.getCode())) {
                                        piCustomOrderPartMaterial.setIsMain(true);
                                    } else {
                                        piCustomOrderPartMaterial.setIsMain(false);
                                    }
                                }

                                List<FabricMainData> fabricMainDataList = iFabricMainDataService.getFabricMainDataByCode(piCustomOrderPartMaterial.getCode());
                                if(ObjectUtils.isNotEmptyList(fabricMainDataList)) {
                                    FabricMainData fabricMainData = fabricMainDataList.get(0);
                                    piCustomOrderPartMaterial.setSystemCode(fabricMainData.getSystemCode());
                                    piCustomOrderPartMaterial.setSystemName(fabricMainData.getSystemName());
                                    piCustomOrderPartMaterial.setColorSystemCode(fabricMainData.getColorSystemCode());
                                    piCustomOrderPartMaterial.setColorSystemName(fabricMainData.getColorSystemName());
                                }
                                /*for (String key : fabricDataMap.keySet()) {
                                    if (StringUtils.isNotBlank(key)) {
                                        if (key.equals(piCustomOrderPartMaterial.getCode())) {
                                            piCustomOrderPartMaterial.setSystemCode(fabricDataMap.get(key).getSystemCode());
                                            piCustomOrderPartMaterial.setSystemName(fabricDataMap.get(key).getSystemName());
                                            piCustomOrderPartMaterial.setColorSystemCode(fabricDataMap.get(key).getColorSystemCode());
                                            piCustomOrderPartMaterial.setColorSystemName(fabricDataMap.get(key).getColorSystemName());
                                        }

                                    }
                                }*/

                                String weight = piCustomOrderPartMaterial.getWeight();
                                if(StringUtils.isEmpty(weight)){
                                    piCustomOrderPartMaterial.setWeight("0");//如果为空，设置克重为0
                                }
                                piCustomOrderPartMaterialList.add(piCustomOrderPartMaterial);
                            }
                        }
                        if (null != piCustomOrderPartMaterialList && piCustomOrderPartMaterialList.size() > 0) {
                            ipiCustomOrderPartMaterialService.addCustomOrderPartMaterialList(piCustomOrderPartMaterialList);
                        }

                    }
                }


                if (null != piCustomOrderPartList && piCustomOrderPartList.size() > 0) {
                    //如果部件已经有位置了，那么这个部件如果还有空位置的要删除
                    try {
                        Iterator<PICustomOrderPart> it = piCustomOrderPartList.iterator();
                        while (it.hasNext()) {
                            PICustomOrderPart part = it.next();
                            String partCode_ = part.getUnitCode();
                            String pp = part.getUnitPositionCode();
                            if (partMap.containsKey(partCode_) && StringUtils.isEmpty(pp)) {
                                it.remove();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                    ipiCustomOrderPartService.addCustomOrderPartList(piCustomOrderPartList);
                }
                receivePiLog.setEndTime(new Date());

                //生成靠码定制单
                if ("20".equals(piCustomOrder.getOrderType())) {
                    taskExecutor.execute(() -> {
                        customStyleCraftCourseService.createCustomStyleByBigStyle(piCustomOrder.getOrderId(), piCustomOrder.getOrderLineId());
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
                receivePiLog.setReturnStatus(String.valueOf(MessageConstant.SERVER_FIELD_ERROR));
                receivePiLog.setReturnDescribe(e.getMessage());
                receivePiLog.setFailData(data);
                // 记录日志
                receivePiLogService.add(receivePiLog);
                return ResultObject.createErrorInstance(MessageConstant.SERVER_FIELD_ERROR, e.getMessage());

            }
        }

        ResultObject resultObject = new ResultObject();
        // 失败数据
        resultObject.setData(result);
        resultObject.setErrorCode(MessageConstant.SUCCESS);
        resultObject.setErrorMessage(MessageConstant.STATUS_TEXT_DONE_SUCCESS);

        // 记录日志
        receivePiLog.setReturnStatus(String.valueOf(MessageConstant.SUCCESS));
        receivePiLog.setReturnDescribe(MessageConstant.STATUS_TEXT_DONE_SUCCESS);
        receivePiLog.setFailData(JSONArray.toJSONString(result));
        receivePiLog.setFailCount(result.size());
        receivePiLogService.add(receivePiLog);

        return resultObject;
    }


}
