package com.ylzs.controller.plm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ylzs.common.bo.ResultObject;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.constant.MessageConstant;
import com.ylzs.common.constant.OderProcessingStatusConstants;
import com.ylzs.common.util.StringUtils;
import com.ylzs.common.utils.object.ObjectUtils;
import com.ylzs.entity.orderprocessing.OrderProcessingStatus;
import com.ylzs.entity.plm.*;
import com.ylzs.entity.receivepilog.ReceivePiLog;
import com.ylzs.service.custom.ICustomStyleCraftCourseService;
import com.ylzs.service.orderprocessing.OrderProcessingStatusService;
import com.ylzs.service.plm.IPICustomOrderPartMaterialService;
import com.ylzs.service.plm.IPICustomOrderPartService;
import com.ylzs.service.plm.IPICustomOrderService;
import com.ylzs.service.plm.cutParameter.impl.*;
import com.ylzs.service.receivepilog.IReceivePiLogService;
import com.ylzs.vo.plm.CutParameterMasterDataMarkInfoHemsVO;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author:tt
 * @Description：定制款裁剪参数主数据接口
 * @Date: Created in 2020/3/18
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping(value = "/cutParameterData")
@Transactional(rollbackFor = Exception.class)
public class CutParameterMasterDataController {

    @Resource
    private CutParameterMasterDataDosagesService dosagesService;
    @Resource
    private CutParameterMasterDataMarkerInfoService markerInfoService;
    @Resource
    private CutParameterMasterDataMarketTablesColorsService marketTablesColorsService;
    @Resource
    private CutParameterMasterDataMarketTablesService marketTablesService;
    @Resource
    private CutParameterMasterDataMarketTablesSizesService marketTablesSizesService;
    @Resource
    private CutParameterMasterDataService dataService;
    @Resource
    private CutParameterMasterDataSizeService dataSizeService;
    @Resource
    private CutParameterMasterDataSizeSpecsService dataSizeSpecsService;
    @Resource
    private IReceivePiLogService receivePiLogService;
    @Resource
    private CutParameterMasterDataMarkerInfoHemsService cutParameterMasterDataMarkerInfoHemsService;
    @Resource
    private IPICustomOrderService piCustomOrderService;
    @Resource
    private IPICustomOrderPartService piCustomOrderPartService;
    @Resource
    private IPICustomOrderPartMaterialService partMaterialService;
    @Autowired
    private ICustomStyleCraftCourseService customStyleCraftCourseService;
    @Resource
    private ThreadPoolTaskExecutor threadPoolExecutor;

    @Resource
    private OrderProcessingStatusService orderProcessingStatusService;


    @ResponseBody
    @RequestMapping("/getCutParameterMasterData")
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
        receivePiLog.setReceiveType(BusinessConstants.ReceivePiLog.receiveType_customcropparam);

        // 1、解析data数据
        JSONObject obj = JSONObject.parseObject(data);
        JSONArray items = obj.getJSONArray("items");
        JSONObject item = new JSONObject();
        if (items.size() > 0) {
            for (int i = 0; i < items.size(); i++) {
                JSONObject jsonObject = items.getJSONObject(i);
                item = jsonObject.getJSONObject("item");
            }
        }

        CutParameterMasterData masterData = JSON.parseObject(item.toJSONString(), CutParameterMasterData.class);

        // 5、存入到数据库异常，抛出异常
        List<CutParameterMasterData> result = new ArrayList<CutParameterMasterData>(0);
        try {
            if (null != masterData) {
                //A.主数据存入数据库
                dataService.addOrUpdateCutParameterMasterData(masterData);
                String orderID = masterData.getOrderId();
                String orderLineID = masterData.getOrderLineId();
                String styleCode = masterData.getStyleCode();
                //保存接收裁剪参数的状态
                if (StringUtils.isNotEmpty(styleCode) && StringUtils.isEmpty(orderLineID)) {
                    try {
                        orderProcessingStatusService.addOrUpdate(styleCode, OderProcessingStatusConstants.BigStyleStatusName.BIG_STYLE_NAME_1140, OderProcessingStatusConstants.BigStyleStatus.BIG_STYLE_1140, "", "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //B.得到部件dosages子表数据并存入数据库
                List<CutParameterMasterDataDosages> dosages = masterData.getDosages();
                if (null != dosages) {
                    for (CutParameterMasterDataDosages dataDosages : dosages) {
                        dataDosages.setOrderId(orderID);
                        dataDosages.setOrderLineId(orderLineID);
                    }
                    dosagesService.addOrUpdateCutParameterMasterDataDosages(dosages);
                }
                //C.得到部件sizes子表数据并存入数据库
                List<CutParameterMasterDataSizes> sizes = masterData.getSizes();
                if (null != sizes) {
                    for (CutParameterMasterDataSizes dataSizes : sizes) {
                        dataSizes.setOrderId(orderID);
                        dataSizes.setOrderLineId(orderLineID);
                        //D.得到sizes子表specs数据并存入数据库
                        String id = dataSizes.getId();
                        List<CutParameterMasterDataSizesSpecs> specs = dataSizes.getSpecs();
                        if (null != specs) {
                            for (CutParameterMasterDataSizesSpecs sizesSpecs : specs) {
                                sizesSpecs.setId(id);
                                sizesSpecs.setOrderId(orderID);
                                sizesSpecs.setOrderLineId(orderLineID);
                            }
                            dataSizeSpecsService.addOrUpdateCutParameterMasterDataSpecs(specs);
                        }
                    }
                    dataSizeService.addOrUpdateCutParameterMasterDataSizes(sizes);
                }
                //E.得到markerInfo子表数据并存入数据库
                List<CutParameterMasterDataMarkInfo> markerInfoList = masterData.getMarkerInfo();
                if (null != markerInfoList && markerInfoList.size() > 0) {
                    for (CutParameterMasterDataMarkInfo markerInfo : markerInfoList) {
                        if (null != markerInfo) {
                            markerInfo.setOrderId(orderID);
                            markerInfo.setOrderLineId(orderLineID);
                            markerInfoService.addOrUpdateCutParameterMasterDataMarketInfo(markerInfo);
                            List<CutParameterMasterDataMarkInfoHemsVO> hems = markerInfo.getHems();
                            if (StringUtils.isNotEmpty(orderLineID) && StringUtils.isNotEmpty(orderID)) {
                                Integer status = OderProcessingStatusConstants.CusOrderStatus.CUS_ORDER_1140;
                                String statusName = OderProcessingStatusConstants.CusOrderStatusName.CUS_ORDER_NAME_1140;
                                if (null == hems || hems.size() == 0) {
                                    status = OderProcessingStatusConstants.CusOrderStatus.CUS_ORDER_1130;
                                    statusName = OderProcessingStatusConstants.CusOrderStatusName.CUS_ORDER_NAME_1130;
                                }
                                try {
                                    //更新节点信息
                                    orderProcessingStatusService.addOrUpdate(orderID + "-" + orderLineID, statusName, status, "", "");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            List<CutParameterMasterDataMarkInfoHems> HemsList = new ArrayList<>();
                            if (null != hems && hems.size() > 0) {
                                for (CutParameterMasterDataMarkInfoHemsVO cutParameterMasterDataMarkInfoHems : hems) {
                                    CutParameterMasterDataMarkInfoHems MarkInfoHems = new CutParameterMasterDataMarkInfoHems();
                                    MarkInfoHems.setOrderId(orderID);
                                    MarkInfoHems.setOrderLineId(orderLineID);
                                    MarkInfoHems.setHemsCode(cutParameterMasterDataMarkInfoHems.getCode());
                                    MarkInfoHems.setValue(cutParameterMasterDataMarkInfoHems.getValue());
                                    HemsList.add(MarkInfoHems);
                                }
                                cutParameterMasterDataMarkerInfoHemsService.addOrUpdateCutParameterMasterDataMarketInfoHems(HemsList);
                            }
                            if (StringUtils.isNotBlank(orderID) && StringUtils.isNotBlank(orderLineID)) {
                                threadPoolExecutor.execute(() -> {
                                    try {
                                        String orderId = orderID;
                                        String orderLineId = orderLineID;
                                        long startTime = System.currentTimeMillis();
                                        PICustomOrder order = piCustomOrderService.getOrderId(orderId, orderLineId);
                                        if (null != order) {
                                            List<PICustomOrderPart> PICustomOrderPartList = piCustomOrderPartService.getOrderAll(order.getOrderId(), order.getOrderLineId());
                                            if (ObjectUtils.isNotEmptyList(PICustomOrderPartList)) {
                                                System.out.println("进入方法,参数：" + order.toString());
                                                order.setUnits(PICustomOrderPartList);
                                                try {
                                                    customStyleCraftCourseService.createCustomStyleCratCouresData(order);
                                                } catch (Exception e) {
                                                    e.printStackTrace();

                                                }

                                                long endTime = System.currentTimeMillis();
                                                System.out.println("消耗时间:" + (endTime - startTime));
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                });
                            }
                        }
                    }
                }

                List<CutParameterMasterDataMaterialMarkers> materialMarkers = masterData.getMaterialMarkers();
                if (null != materialMarkers && materialMarkers.size() > 0) {
                    for (CutParameterMasterDataMaterialMarkers materialMarker : materialMarkers) {
                        //F.得到markerTables子表数据并存入数据库
                        List<CutParameterMasterDataMarkerTables> markerTables = materialMarker.getMarkerTables();
                        if (null != markerTables) {
                            for (CutParameterMasterDataMarkerTables tables : markerTables) {
                                tables.setOrderId(orderID);
                                tables.setOrderLineId(orderLineID);
                                String index = tables.getIndex();
                                //G.得到markerTables子表sizes数据并存入数据库
                                List<CutParameterMasterDataMarkerTablesSizes> sizes2 = tables.getSizes();
                                if (null != sizes2) {
                                    for (CutParameterMasterDataMarkerTablesSizes tablesSizes : sizes2) {
                                        tablesSizes.setIndex(index);
                                        tablesSizes.setOrderId(orderID);
                                        tablesSizes.setOrderLineId(orderLineID);
                                    }
                                    marketTablesSizesService.addOrUpdateCutParameterMasterDataMarkerTablesSizes(sizes2);
                                }
                                //H.得到markerTables子表color数据并存入数据库
                                List<CutParameterMasterDataMarkerTablesColors> colors = tables.getColors();
                                if (null != colors) {
                                    for (int i = 0; i < colors.size(); i++) {
                                        CutParameterMasterDataMarkerTablesColors tablesColors = colors.get(i);
                                        tablesColors.setOrderId(orderID);
                                        tablesColors.setOrderLineId(orderLineID);
                                    }
                                    marketTablesColorsService.addOrUpdateCutParameterMasterDataMarkerTablesColors(colors);
                                }
                            }
                            marketTablesService.addOrUpdateCutParameterMasterDataMarkerTables(markerTables);
                        }
                    }
                }
            }
            receivePiLog.setEndTime(new Date());
        } catch (Exception e) {
            e.printStackTrace();
            receivePiLog.setReturnStatus(String.valueOf(MessageConstant.SERVER_FIELD_ERROR));
            receivePiLog.setReturnDescribe(MessageConstant.STATUS_TEXT_Error_Count);
            receivePiLog.setFailData(data);
            // 记录日志
            receivePiLogService.add(receivePiLog);
            return ResultObject.createErrorInstance(MessageConstant.SERVER_FIELD_ERROR, MessageConstant.STATUS_TEXT_Error_Count);
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
