package com.ylzs.controller.plm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.ylzs.common.bo.ResultObject;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.constant.MessageConstant;
import com.ylzs.common.util.PiDataUtil;
import com.ylzs.common.util.SnowflakeIdUtil;
import com.ylzs.common.util.StringUtils;
import com.ylzs.entity.designPart.DesignPart;
import com.ylzs.entity.plm.DesignPartMasterData;
import com.ylzs.entity.receivepilog.ReceivePiLog;
import com.ylzs.service.designPart.IDesignPartService;
import com.ylzs.service.partCraft.IPartCraftMasterDataService;
import com.ylzs.service.plm.IDesignPartMainDataBomService;
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
* @Description： PI部件主数据接收
* @Date: Created in 2020/3/11
*/
@CrossOrigin(origins = "*",maxAge = 3600)
@Controller
@RequestMapping(value = "/partMainData")
public class DesignPartMainMasterController {

    @Resource
    private IDesignPartService iDesignPartService;

    @Resource
    private IDesignPartMainDataBomService iDesignPartMainDataBomService;

    @Resource
    private IReceivePiLogService receivePiLogService;

    @Resource
    private IPartCraftMasterDataService partCraftMasterDataService;

    @ResponseBody
    @RequestMapping("/getPartMainData")
    public ResultObject getPartMainData(HttpServletRequest request)throws Exception{
        String data = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        if (StringUtils.isEmpty(data)){
            return ResultObject.createErrorInstance(MessageConstant.Request_Method_Not_Allowed, MessageConstant.STATUS_TEXT_DATA_NULL);
        }
        ReceivePiLog receivePiLog = new ReceivePiLog();
        Date startDate = new Date();
        receivePiLog.setCreateTime(startDate);
        receivePiLog.setStartTime(startDate);
        receivePiLog.setUpdateTime(startDate);
        receivePiLog.setData(data);
        receivePiLog.setReceiveType(BusinessConstants.ReceivePiLog.receiveType_partMasterData);

        // 1、解析data数据
        DataParent<DesignPartMasterData> parent = (DataParent<DesignPartMasterData>) JSON.parseObject(data, new TypeReference<DataParent<DesignPartMasterData>>() {});

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
        PiDataUtil<DesignPartMasterData> util = new PiDataUtil<DesignPartMasterData>();
        List<DataChild<DesignPartMasterData>> children = util.getChildData(parent, DesignPartMasterData.class);//items中的数据

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
        List<DesignPartMasterData> result = new ArrayList<DesignPartMasterData>(0);
        for (DataChild<DesignPartMasterData> child : children) {
            DesignPartMasterData designPartMasterData = child.getItem();
            try {
                DesignPart designPart  =new DesignPart();
                designPart.setDesignName(designPartMasterData.getComponentName());
                designPart.setDesignCode(designPartMasterData.getComponentCode());
                designPart.setDesignImage(designPartMasterData.getImageUrl());
                if(designPartMasterData.getGrandCategory().indexOf("-")!=-1){
                    designPart.setClothingCategory(designPartMasterData.getGrandCategory().substring(0,designPartMasterData.getGrandCategory().indexOf("-")));
                }else{
                    designPart.setClothingCategory(designPartMasterData.getGrandCategory());
                }

                if(designPartMasterData.getMidCategory().indexOf("-")!=-1){
                    designPart.setPartMiddleCode(designPartMasterData.getMidCategory().substring(0,designPartMasterData.getMidCategory().indexOf("-")));
                }else{
                    designPart.setPartMiddleCode(designPartMasterData.getMidCategory());
                }

                if(designPartMasterData.getSmallCategory().indexOf("-")!=-1){
                    designPart.setSmallCategory(designPartMasterData.getSmallCategory().substring(0,designPartMasterData.getSmallCategory().indexOf("-")));
                }else{
                    designPart.setSmallCategory(designPartMasterData.getSmallCategory());
                }
                designPart.setRandomCode(SnowflakeIdUtil.generateId());
                //因没有UI界面，所以写死发布状态，后期改为草稿
                designPart.setStatus(BusinessConstants.Status.PUBLISHED_STATUS);
                designPart.setPartPosition(designPartMasterData.getMtmPositionCode());
                designPart.setPatternType(designPartMasterData.getPatternTechnology());
                designPart.setPatternMode(designPartMasterData.getEmbroideryStyle());
                designPart.setStyleType(designPartMasterData.getStyleType());
                designPart.setStyleCode(designPartMasterData.getStyleCode());
                designPart.setSeamLine1(designPartMasterData.getSeamLine1());
                designPart.setSeamLine2(designPartMasterData.getSeamLine2());
                designPart.setStitchMidType(designPartMasterData.getStitchMidType());
                designPart.setStitchSmallType(designPartMasterData.getStitchSmallType());
                designPart.setImageUrlMtm(designPartMasterData.getImageUrlMtm());
                designPart.setImageDurlMtm(designPartMasterData.getImageDurlMtm());
                designPart.setWarpOffset(designPartMasterData.getWarpOffset());
                designPart.setWeftOffset(designPartMasterData.getWeftOffset());
                designPart.setObliqueOffset(designPartMasterData.getObliqueOffset());
                designPart.setActive(designPartMasterData.getActive());
                designPart.setApplicablePosition(designPartMasterData.getApplicablePosition());
                designPart.setStitchNumber(designPartMasterData.getStitchNumber());
                designPart.setWStitch(designPartMasterData.getWStitch());
                designPart.setGongYiExplain(designPartMasterData.getGongYiExplain());
                designPart.setPatternTechnologyD(designPartMasterData.getPatternTechnologyD());
                designPart.setReceiveTime(new Date());
                designPart.setCreateUser("PLM");
                designPart.setUpdateUser("PLM");
                designPart.setPatternMsg(designPartMasterData.getPatternMsg());
                designPart.setAffectCraft(designPartMasterData.getAffectCraft());
                designPart.setRandomCode(SnowflakeIdUtil.generateId());
                //存入部件主数据
                if(iDesignPartService.addOrUpdatePartData(designPart) > 0) {
                    //把使用该设计部件状态都改成草稿状态
                    if(null != designPart.getAffectCraft() && designPart.getAffectCraft()) {
                        partCraftMasterDataService.updateStatusByDesignPartCode(BusinessConstants.Status.DRAFT_STATUS, designPart.getDesignCode());
                    }
                }
                receivePiLog.setEndTime(new Date());
                //List<DesignPartMasterDataBom> bom = designPartMasterData.getBom();//得到部件bom子表数据
                /*if (null != bom){
                    for (DesignPartMasterDataBom designPartMasterDataBom : bom){
                        designPartMasterDataBom.setReceiveTime(new Date());
                        designPartMasterDataBom.setDesignCode(designPartMasterData.getDesignCode());
                    }
                    iDesignPartMainDataBomService.addOrUpdatePartDataBom(bom);
                }*/
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
