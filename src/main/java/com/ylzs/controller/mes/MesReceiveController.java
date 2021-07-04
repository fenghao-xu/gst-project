package com.ylzs.controller.mes;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.ylzs.common.bo.ResultObject;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.constant.MessageConstant;
import com.ylzs.entity.mes.CappPiPilotProductionResult;
import com.ylzs.entity.receivepilog.ReceivePiLog;
import com.ylzs.service.mes.ICappPiPilotProductionResultService;
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
import java.util.Date;

@Api(tags = "MES接收接口")
@RestController
@RequestMapping(value = "/mes")
public class MesReceiveController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MesReceiveController.class);
    @Resource
    private IReceivePiLogService receivePiLogService;
    @Resource
    ICappPiPilotProductionResultService cappPiPilotProductionResultService;


    @ApiOperation(value = "接收试产结果")
    @RequestMapping(value = "/receivePilotProductionResult", method = RequestMethod.POST)
    public ResultObject receivePreScheduleResult(HttpServletRequest request, @RequestBody String jsonString)  throws Exception {
        String data = jsonString;
        LOGGER.info("接收试产结果：" + jsonString);

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
            JSONObject jsonObject = JSONObject.parseObject(jsonString);
            CappPiPilotProductionResult pilotResult = jsonObject.toJavaObject(CappPiPilotProductionResult.class);
            pilotResult.setReceiveTime(now);
            cappPiPilotProductionResultService.save(pilotResult);
            resultObject.setData(pilotResult);

        } catch (Exception e) {
            e.printStackTrace();
            receivePiLog.setReturnStatus(String.valueOf(MessageConstant.SERVER_FIELD_ERROR));
            receivePiLog.setReturnDescribe(e.getMessage());

            // 记录日志
            receivePiLogService.add(receivePiLog);
            return ResultObject.createErrorInstance(MessageConstant.SERVER_FIELD_ERROR,MessageConstant.STATUS_TEXT_INTERNAL_ERROR);

        }

        // 记录日志
        receivePiLog.setReturnStatus(String.valueOf(MessageConstant.SUCCESS));
        receivePiLog.setReturnDescribe(MessageConstant.STATUS_TEXT_DONE_SUCCESS);
        receivePiLogService.add(receivePiLog);
        return resultObject;
    }
}
