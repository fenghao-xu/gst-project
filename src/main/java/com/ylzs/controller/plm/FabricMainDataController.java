package com.ylzs.controller.plm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.github.pagehelper.util.StringUtil;
import com.ylzs.common.bo.ResultObject;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.constant.MessageConstant;
import com.ylzs.common.util.PiDataUtil;
import com.ylzs.entity.plm.CategoryMasterData;
import com.ylzs.entity.plm.FabricMainData;
import com.ylzs.entity.receivepilog.ReceivePiLog;
import com.ylzs.service.plm.IFabricMainDataService;
import com.ylzs.service.receivepilog.IReceivePiLogService;
import com.ylzs.vo.system.DataChild;
import com.ylzs.vo.system.DataParent;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: watermelon.xzx
 * @Description:PI材料主数据接收(面料主数据)
 * @Date: Created in 15:29 2020/3/11
 */

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping(value = "/fabricMainData")
public class FabricMainDataController {
    @Resource
    private IFabricMainDataService iFabricMainDataService;
    @Resource
    private IReceivePiLogService receivePiLogService;

    @ResponseBody
    @RequestMapping("/getFabricMainData")
    public ResultObject getFabricMainData(HttpServletRequest request) throws Exception {
        String data = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        if (StringUtil.isEmpty(data)) {
            return ResultObject.createErrorInstance(MessageConstant.Request_Method_Not_Allowed, MessageConstant.STATUS_TEXT_DATA_NULL);
        }
        ReceivePiLog receivePiLog = new ReceivePiLog();
        Date orderStartDate = new Date();
        receivePiLog.setCreateTime(orderStartDate);
        receivePiLog.setStartTime(orderStartDate);
        receivePiLog.setUpdateTime(orderStartDate);
        receivePiLog.setData(data);
        receivePiLog.setReceiveType(BusinessConstants.ReceivePiLog.receiveType_fabricMasterData);
        // 1、解析data数据
        DataParent<FabricMainData> parent = (DataParent<FabricMainData>) JSON.parseObject(data, new TypeReference<DataParent<FabricMainData>>() {});

        // 2、获取count
        String countStr = parent.getCount();
        Integer count = 0;
        try {
            count = Integer.parseInt(countStr);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return ResultObject.createErrorInstance(MessageConstant.SERVER_FIELD_ERROR, MessageConstant.STATUS_TEXT_Error_Count);
        }
        // 3、获取主数据
        PiDataUtil<FabricMainData> util = new PiDataUtil<FabricMainData>();
        List<DataChild<FabricMainData>> children = util.getChildData(parent, FabricMainData.class);

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
        List<CategoryMasterData> result = new ArrayList<CategoryMasterData>(0);
        for (DataChild<FabricMainData> child : children) {
            FabricMainData fabricMainData = child.getItem();
            try {
                fabricMainData.setReceiveTime(new Date());
                if(fabricMainData.getWidth()==null){
                    fabricMainData.setWidth(new BigDecimal(0));
                }
                if(fabricMainData.getActualWidth()==null){
                        fabricMainData.setActualWidth(new BigDecimal(0));
                }
                if(fabricMainData.getPrice()==null){
                        fabricMainData.setPrice(new BigDecimal(0));
                }
                if(fabricMainData.getWarpstart()==null){
                    fabricMainData.setWarpstart(new BigDecimal(0));
                }
                if(fabricMainData.getWarpRepeat()==null){
                    fabricMainData.setWarpRepeat(new BigDecimal(0));
                }
                if(fabricMainData.getWarpRepeat()==null){
                    fabricMainData.setWarpRepeat(new BigDecimal(0));
                }
                if(fabricMainData.getWarpRepeatLine1()==null){
                    fabricMainData.setWarpRepeatLine1(new BigDecimal(0));
                }
                if(fabricMainData.getWarpRepeatLine2()==null){
                    fabricMainData.setWarpRepeatLine2(new BigDecimal(0));
                }
                if(fabricMainData.getWarpRepeatLine3()==null){
                        fabricMainData.setWarpRepeatLine3(new BigDecimal(0));
                }
                if(fabricMainData.getWarpRepeatLine4()==null){
                        fabricMainData.setWarpRepeatLine4(new BigDecimal(0));
                }
                if(fabricMainData.getWarpRepeatLine5()==null){
                        fabricMainData.setWarpRepeatLine5(new BigDecimal(0));
                }
                if(fabricMainData.getWeftRepeat()==null){
                        fabricMainData.setWeftRepeat(new BigDecimal(0));
                }


                if(fabricMainData.getWeftRepeatLine1()==null){
                        fabricMainData.setWeftRepeatLine1(new BigDecimal(0));
                }

                if(fabricMainData.getWeftRepeatLine2()==null){
                        fabricMainData.setWeftRepeatLine2(new BigDecimal(0));
                }

                if(fabricMainData.getWeftRepeatLine3()==null){
                        fabricMainData.setWeftRepeatLine3(new BigDecimal(0));
                }

                if(fabricMainData.getWeftRepeatLine4()==null){
                        fabricMainData.setWeftRepeatLine4(new BigDecimal(0));
                }

                if(fabricMainData.getWeftRepeatLine5()==null){

                        fabricMainData.setWeftRepeatLine5(new BigDecimal(0));
                }

                if(fabricMainData.getLengthSpec()==null){

                        fabricMainData.setLengthSpec(new BigDecimal(0));
                }
                if(fabricMainData.getColoSadeValue() == null) {
                    fabricMainData.setColoSadeValue(new BigDecimal(0));
                }
                if(fabricMainData.getBulkProductionLeadTime() == null) {
                    fabricMainData.setBulkProductionLeadTime(new BigDecimal(0));
                }


                if(fabricMainData.getGrandCategory().indexOf("-")!=-1){
                    fabricMainData.setGrandCategory(fabricMainData.getGrandCategory().substring(0,fabricMainData.getGrandCategory().indexOf("-")));
                }else{
                    fabricMainData.setGrandCategory(fabricMainData.getGrandCategory());
                }

                if(fabricMainData.getMidCategory().indexOf("-")!=-1){
                    fabricMainData.setMidCategory(fabricMainData.getMidCategory().substring(0,fabricMainData.getMidCategory().indexOf("-")));
                }else{
                    fabricMainData.setMidCategory(fabricMainData.getMidCategory());
                }

                if(fabricMainData.getSmallCategory().indexOf("-")!=-1){
                    fabricMainData.setSmallCategory(fabricMainData.getSmallCategory().substring(0,fabricMainData.getSmallCategory().indexOf("-")));
                }else{
                    fabricMainData.setSmallCategory(fabricMainData.getSmallCategory());
                }
                if(fabricMainData.getTestingOK()==null){
                fabricMainData.setTestingOK(true);
                }
                iFabricMainDataService.addOrUpdateFabricDataDao(fabricMainData);
                receivePiLog.setEndTime(new Date());
            } catch (Exception e) {
                e.printStackTrace();
                receivePiLog.setReturnStatus(String.valueOf(MessageConstant.SERVER_FIELD_ERROR));
                receivePiLog.setReturnDescribe(MessageConstant.STATUS_TEXT_Error_Count);
                receivePiLog.setFailData(data);
                // 记录日志
                receivePiLogService.add(receivePiLog);
                return ResultObject.createErrorInstance(MessageConstant.SERVER_FIELD_ERROR,MessageConstant.STATUS_TEXT_Error_Count);

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
