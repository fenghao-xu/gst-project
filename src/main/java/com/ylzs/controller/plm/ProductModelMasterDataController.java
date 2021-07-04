package com.ylzs.controller.plm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.github.pagehelper.util.StringUtil;
import com.ylzs.common.bo.ResultObject;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.constant.MessageConstant;
import com.ylzs.common.util.PiDataUtil;
import com.ylzs.entity.plm.ProductModelMasterData;
import com.ylzs.entity.receivepilog.ReceivePiLog;
import com.ylzs.service.plm.IProductModelMasterDataService;
import com.ylzs.service.receivepilog.IReceivePiLogService;
import com.ylzs.service.thinkstyle.IThinkStyleWarehouseService;
import com.ylzs.vo.system.DataChild;
import com.ylzs.vo.system.DataParent;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: watermelon.xzx
 * @Description: PI智库款主数据接收
 * @Date: Created in 14:06 2020/3/12
 */

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping(value = "/productModelMasterData")
public class ProductModelMasterDataController {
    @Resource
    private IReceivePiLogService receivePiLogService;
    @Resource
    private IProductModelMasterDataService iProductModelMasterDataService;
    @Resource
    private IThinkStyleWarehouseService thinkStyleWarehouseService;


    @ResponseBody
    @RequestMapping("/getProductModelData")
    public ResultObject getProductModelData(HttpServletRequest request) throws Exception {
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
        receivePiLog.setReceiveType(BusinessConstants.ReceivePiLog.receiveType_productModelData);
        // 1、解析data数据
        DataParent<ProductModelMasterData> parent = (DataParent<ProductModelMasterData>) JSON.parseObject(data, new TypeReference<DataParent<ProductModelMasterData>>() {});

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
        PiDataUtil<ProductModelMasterData> util = new PiDataUtil<ProductModelMasterData>();
        List<DataChild<ProductModelMasterData>> children = util.getChildData(parent, ProductModelMasterData.class);

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
        List<ProductModelMasterData> result = new ArrayList<ProductModelMasterData>(0);
        for (DataChild<ProductModelMasterData> child : children) {
            ProductModelMasterData productModelMasterData = child.getItem();
            try {
                productModelMasterData.setReceiveTime(new Date());
                iProductModelMasterDataService.addOrUpdateProductModelDataDao(productModelMasterData);
                thinkStyleWarehouseService.addOrUpdateProductModelDataDao(productModelMasterData.getCode());
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
