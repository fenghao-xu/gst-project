package com.ylzs.controller.plm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.ylzs.common.bo.ResultObject;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.constant.MessageConstant;
import com.ylzs.common.constant.OderProcessingStatusConstants;
import com.ylzs.common.util.PiDataUtil;
import com.ylzs.common.util.StringUtils;
import com.ylzs.entity.orderprocessing.OrderProcessingStatus;
import com.ylzs.entity.plm.BigStyleMasterData;
import com.ylzs.entity.plm.BigStyleMasterDataSKC;
import com.ylzs.entity.plm.BigStyleMasterDataSKCMaterial;
import com.ylzs.entity.plm.BigStyleMasterDataWXProcedures;
import com.ylzs.entity.receivepilog.ReceivePiLog;
import com.ylzs.service.bigstylerecord.IBigStyleNodeRecordService;
import com.ylzs.service.orderprocessing.OrderProcessingStatusService;
import com.ylzs.service.plm.impl.*;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: tt
 * @Description： 大货款主数据接口
 * @Date: Created in 2020/3/16
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping(value = "/bigStyleData")
public class BigStyleMasterDataController {

    @Resource
    private BigStyleMasterDataService bigStyleMasterDataService;
    @Resource
    private BigStyleMasterDataSKCService bigStyleMasterDataSKCService;
    @Resource
    private BigStyleMasterDataSKCMaterialService bigStyleMasterDataSKCMaterialService;
    @Resource
    private BigStyleMasterDataColorService bigStyleMasterDataColorService;
    @Resource
    BigStyleMasterDataWXProceduresService bigStyleMasterDataWXProceduresService;
    @Resource
    private IReceivePiLogService receivePiLogService;

    @Resource
    private IBigStyleNodeRecordService bigStyleNodeRecordService;

    @Resource
    private OrderProcessingStatusService orderProcessingStatusService;

    @ResponseBody
    @RequestMapping("/getBigStyleMainData")
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
        receivePiLog.setReceiveType(BusinessConstants.ReceivePiLog.receiveType_mbsData);

        // 1、解析data数据
        DataParent<BigStyleMasterData> parent = (DataParent<BigStyleMasterData>) JSON.parseObject(data, new TypeReference<DataParent<BigStyleMasterData>>() {
        });
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
        PiDataUtil<BigStyleMasterData> util = new PiDataUtil<BigStyleMasterData>();
        List<DataChild<BigStyleMasterData>> children = util.getChildData(parent, BigStyleMasterData.class);//items中的数据
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
        List<BigStyleMasterData> result = new ArrayList<BigStyleMasterData>(0);
        for (DataChild<BigStyleMasterData> child : children) {
            BigStyleMasterData bigStyleMasterData = child.getItem();//得到主数据
            String ctStyleCode = bigStyleMasterData.getCtStyleCode();//物料编码(款号)
            try {
                bigStyleMasterData.getGrandCategory();
                if (bigStyleMasterData.getGrandCategory().indexOf("-") != -1) {
                    bigStyleMasterData.setGrandCategory(bigStyleMasterData.getGrandCategory().substring(0, bigStyleMasterData.getGrandCategory().indexOf("-")));
                } else {
                    bigStyleMasterData.setGrandCategory(bigStyleMasterData.getGrandCategory());
                }
                bigStyleMasterDataService.addOrUpdateBigStyleData(bigStyleMasterData);//主表数据存入
                try {
                    //更新节点信息
                    orderProcessingStatusService.addOrUpdate(ctStyleCode, OderProcessingStatusConstants.BigStyleStatusName.BIG_STYLE_NAME_1120, OderProcessingStatusConstants.BigStyleStatus.BIG_STYLE_1120, "", "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //B.得到部件WXProcedres子表数据并存入数据库
                List<BigStyleMasterDataWXProcedures> wx = bigStyleMasterData.getWXProcedures();
                if (null != wx && wx.size() > 0) {
                    for (BigStyleMasterDataWXProcedures bigStyleMasterDataWXProcedures : wx) {
                        bigStyleMasterDataWXProcedures.setCtStyleCode(ctStyleCode);
                    }
                    bigStyleMasterDataWXProceduresService.addOrUpdateBigStyleDataWXProceduresList(wx);
                }
                //C.得到部件SKC子表数据并存入数据库
                List<BigStyleMasterDataSKC> skc = bigStyleMasterData.getSkc();
                if (null != skc && skc.size() > 0) {
                    for (BigStyleMasterDataSKC bigStyleMasterDataSKC : skc) {
                        bigStyleMasterDataSKC.setCtStyleCode(ctStyleCode);
                    }
                    bigStyleMasterDataSKCService.addOrUpdateBigStyleDataSKCList(skc);
                    //D.得到SKC-material子表数据并存入数据库
                    for (BigStyleMasterDataSKC bigStyleMasterDataSKC : skc) {
                        String styleSkcCode = bigStyleMasterDataSKC.getStyleSKCcode();///SKC款号编码
                        List<BigStyleMasterDataSKCMaterial> list = bigStyleMasterDataSKC.getMaterials();
                        if (null != list) {
                            for (BigStyleMasterDataSKCMaterial bigStyleMasterDataSKCMaterial : list) {
                                bigStyleMasterDataSKCMaterial.setStyleSkcCode(styleSkcCode);
                                if (bigStyleMasterDataSKCMaterial.getIsMainMaterial() != null) {
                                    if (Boolean.TRUE.equals(bigStyleMasterDataSKCMaterial.getIsMainMaterial())) {
                                        bigStyleMasterDataSKCMaterial.setIsMainMaterial(true);
                                    } else {
                                        bigStyleMasterDataSKCMaterial.setIsMainMaterial(false);
                                    }
                                } else {
                                    bigStyleMasterDataSKCMaterial.setIsMainMaterial(false);
                                }
                            }
                            bigStyleMasterDataSKCMaterialService.addOrUpdateBigStyleDataSKCMaterialList(list);
                        }
                    }
                }
                receivePiLog.setEndTime(new Date());

                //更新到大货记录表里
                bigStyleNodeRecordService.addOrUpdateBigStyleNodeRecord(bigStyleMasterData, skc);

            } catch (Exception e) {
                e.printStackTrace();
                receivePiLog.setReturnStatus(String.valueOf(MessageConstant.SERVER_FIELD_ERROR));
                receivePiLog.setReturnDescribe(MessageConstant.STATUS_TEXT_Error_Count);
                receivePiLog.setFailData(data);
                // 记录日志
                receivePiLogService.add(receivePiLog);
                return ResultObject.createErrorInstance(MessageConstant.SERVER_FIELD_ERROR, MessageConstant.STATUS_TEXT_Error_Count);
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
