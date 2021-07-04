package com.ylzs.controller.aps;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.ylzs.common.bo.ResultObject;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.constant.MessageConstant;
import com.ylzs.common.utils.object.ObjectUtils;
import com.ylzs.entity.aps.CappPiPreScheduleResult;
import com.ylzs.entity.receivepilog.ReceivePiLog;
import com.ylzs.service.aps.IApsReceiveService;
import com.ylzs.service.bigstylerecord.IBigStyleNodeRecordService;
import com.ylzs.service.receivepilog.IReceivePiLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "APS接收接口")
@RestController
@RequestMapping(value = "/aps")
public class ApsReceiveController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApsReceiveController.class);
    @Resource
    private IReceivePiLogService receivePiLogService;
    @Resource
    private IApsReceiveService apsReceiveService;

    @Resource
    private IBigStyleNodeRecordService bigStyleNodeRecordService;


    @ApiOperation(value = "接收预排产结果")
    @RequestMapping(value = "/receivePreScheduleResult", method = RequestMethod.POST)
    public ResultObject receivePreScheduleResult(HttpServletRequest request, @RequestBody String jsonString)  throws Exception {
        String data = jsonString;
        LOGGER.info("接收预排产结果：" + jsonString);

        if (StringUtil.isEmpty(data)) {
            return ResultObject.createErrorInstance(MessageConstant.Request_Method_Not_Allowed, MessageConstant.STATUS_TEXT_DATA_NULL);
        }

        ResultObject resultObject = new ResultObject();
        resultObject.setErrorCode(MessageConstant.SUCCESS);
        resultObject.setErrorMessage(MessageConstant.STATUS_TEXT_DONE_SUCCESS);


        ReceivePiLog receivePiLog = new ReceivePiLog();
        Date now = new Date();
        receivePiLog.setCreateTime(now);
        receivePiLog.setStartTime(now);
        receivePiLog.setUpdateTime(now);
        receivePiLog.setData(data);
        receivePiLog.setReceiveType(BusinessConstants.ReceivePiLog.receiveType_preschedureresult);
        try {
            List<CappPiPreScheduleResult> resultList = new ArrayList<>();
            JSONArray jsonArr = JSONArray.parseArray(jsonString);
            if(jsonArr.size() > 0) {
                for(int i = 0; i < jsonArr.size(); i++) {
                    JSONObject jsonObject = jsonArr.getJSONObject(i);
                    String strPurchaseRequisition = jsonObject.getString("purchaseRequisition");
                    JSONArray jsonItems = jsonObject.getJSONArray("items");

                    for (int j = 0; j < jsonItems.size(); j++) {
                        JSONObject jsonItm = jsonItems.getJSONObject(j);
                        CappPiPreScheduleResult result = new CappPiPreScheduleResult();
                        result.setType(jsonItm.getString("type"));
                        result.setPurchaseRequisition(strPurchaseRequisition);
                        result.setDeliveryTime(jsonItm.getDate("deliveryTime"));
                        result.setPlanCode(jsonItm.getString("planCode"));
                        result.setProduct(jsonItm.getString("product"));
                        result.setQuantity(jsonItm.getDouble("quantity"));
                        result.setScheduleTime(jsonItm.getDate("scheduleTime"));
                        result.setWorkcenterCode(jsonItm.getString("workcenterCode"));
                        result.setReceiveTime(now);
                        resultList.add(result);
                    }
                }

                if(ObjectUtils.isNotEmptyList(resultList)) {
                    //更新大货款记录
                    for(CappPiPreScheduleResult itm: resultList) {
                        apsReceiveService.addPreScheduleResult(itm);
                        if(itm.getType().equals("0040")) {
                            bigStyleNodeRecordService.updateStyleNodeRecord(itm);
                        }
                    }
                }
            }
            resultObject.setData(resultList);

        } catch (Exception e) {
            e.printStackTrace();
            receivePiLog.setReturnStatus(String.valueOf(MessageConstant.SERVER_FIELD_ERROR));
            receivePiLog.setReturnDescribe(e.getMessage());
            receivePiLog.setEndTime(new Date());

            // 记录日志
            receivePiLogService.add(receivePiLog);
            return ResultObject.createErrorInstance(MessageConstant.SERVER_FIELD_ERROR,MessageConstant.STATUS_TEXT_INTERNAL_ERROR);

        }

        // 记录日志
        receivePiLog.setReturnStatus(String.valueOf(MessageConstant.SUCCESS));
        receivePiLog.setReturnDescribe(MessageConstant.STATUS_TEXT_DONE_SUCCESS);
        receivePiLog.setEndTime(new Date());
        receivePiLogService.add(receivePiLog);
        return resultObject;
    }
}
