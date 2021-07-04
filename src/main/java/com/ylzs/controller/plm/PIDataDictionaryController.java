package com.ylzs.controller.plm;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.github.pagehelper.util.StringUtil;
import com.ylzs.common.bo.ResultObject;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.constant.MessageConstant;
import com.ylzs.common.util.PiDataUtil;
import com.ylzs.common.util.SnowflakeIdUtil;
import com.ylzs.common.util.StringUtils;
import com.ylzs.entity.craftstd.Dictionary;
import com.ylzs.entity.craftstd.PartPosition;
import com.ylzs.entity.datadictionary.DictionaryType;
import com.ylzs.entity.plm.PIDataDictionary;
import com.ylzs.entity.plm.PIDataDictionaryDicts;
import com.ylzs.entity.receivepilog.ReceivePiLog;
import com.ylzs.service.craftstd.IDictionaryService;
import com.ylzs.service.datadictionary.IDictionaryTypeService;
import com.ylzs.service.receivepilog.IReceivePiLogService;
import com.ylzs.service.staticdata.PartPositionService;
import com.ylzs.vo.system.DataChild;
import com.ylzs.vo.system.DataParent;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @Author: watermelon.xzx
 * @Description: PI传数据字典
 * @Date: Created in 17:21 2020/3/16
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping(value = "/PIDataDictionary")
public class PIDataDictionaryController {
    @Resource
    private IDictionaryService IdictionaryService;
    @Resource
    private IReceivePiLogService receivePiLogService;
    @Resource
    private PartPositionService partPositionService;
    @Resource
    private IDictionaryTypeService iDictionaryTypeService;

    @ResponseBody
    @RequestMapping("/getPIDataDictionary")
    public ResultObject getPIDataDictionary(HttpServletRequest request) throws Exception {
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
        receivePiLog.setReceiveType(BusinessConstants.ReceivePiLog.receiveType_categoryMasterData);
        // 1、解析data数据
        DataParent<PIDataDictionary> parent = (DataParent<PIDataDictionary>) JSON.parseObject(data, new TypeReference<DataParent<PIDataDictionary>>() {});

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
        PiDataUtil<PIDataDictionary> util = new PiDataUtil<PIDataDictionary>();
        List<DataChild<PIDataDictionary>> children = util.getChildData(parent, PIDataDictionary.class);

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
        List<PIDataDictionary> result = new ArrayList<PIDataDictionary>(0);
        //判断此字典值类型是否存在
        List<DictionaryType> iDictionaryTypeServiceAll = iDictionaryTypeService.getAll();
        Map<String,Object> dictionaryTypeMap = new HashMap<>();
        for (DictionaryType dictionaryType : iDictionaryTypeServiceAll){
            dictionaryTypeMap.put(dictionaryType.getDictionaryTypeCode(),dictionaryType);
        }
        //判断此无parentCode字典值是否存在
        List<Dictionary> dictionaryList = IdictionaryService.getNoParentCode();
        Map<String,Dictionary> dictionaryMap = new HashMap<>();
        for (Dictionary dictionary : dictionaryList){
            if(null != dictionary.getDictionaryTypeCode() && StringUtils.isNotBlank(dictionary.getDictionaryTypeCode()) && null != dictionary.getDicValue() && StringUtils.isNotBlank(dictionary.getDicValue())){
                dictionaryMap.put(dictionary.getDicValue()+"_"+dictionary.getDictionaryTypeCode(),dictionary);
            }
        }

        for (DataChild<PIDataDictionary> child : children) {//循环一个个items下面的对象,到时候下面逻辑移到service层去
            try {
                PIDataDictionary item = child.getItem();
                if("StylePosition".equalsIgnoreCase(item.getCode())||"缝边位置".equals(item.getName())||"TAPosition".equalsIgnoreCase(item.getCode())||"图案位置".equals(item.getName()
                )||"BJPosition".equalsIgnoreCase(item.getCode())||"部件位置".equals(item.getName())||"ZSPosition".equalsIgnoreCase(item.getCode())||"装饰位置".equals(item.getName())
                ){//存入part_psoition表
                    List<PIDataDictionaryDicts> dictsList = item.getDicts();//PI数据
                    List<PartPosition> itemList = new ArrayList<>();
                    for(PIDataDictionaryDicts piData : dictsList){
                        if(piData.getParentValues()!=null|| StringUtils.isNotBlank(piData.getParentValues())) {
                            List<String> parentValues = Arrays.asList(piData.getParentValues().split(","));
                            for (String parentValue :parentValues){
                                PartPosition partPosition  =new PartPosition();
                                partPosition.setRandomCode(SnowflakeIdUtil.generateId());
                                partPosition.setPartPositionCode(piData.getValue());
                                partPosition.setPartPositionName(piData.getLabel());
                                //sewing缝边 embroider绣花 decoration装饰 bjposition 部件
                                String returnDiv = returnDiv(item);
                                if(null!=returnDiv && StringUtils.isNotBlank(returnDiv)){
                                    partPosition.setPartType(returnDiv);
                                }
                                partPosition.setClothingCategoryCode(parentValue.substring(0,parentValue.indexOf("-")));
                                partPosition.setCreateUser("PLM");
                                partPosition.setCreateTime(new Date());
                                if("1".equals(piData.getStatus())){
                                    partPosition.setIsInvalid(false);
                                }else{
                                    partPosition.setIsInvalid(true);
                                }

                                itemList.add(partPosition);
                            }
                        }else{
                            PartPosition partPosition  =new PartPosition();
                            partPosition.setRandomCode(SnowflakeIdUtil.generateId());
                            partPosition.setPartPositionCode(piData.getValue());
                            partPosition.setPartPositionName(piData.getLabel());
                            partPosition.setClothingCategoryCode("1");
                            String returnDiv = returnDiv(item);
                            if(null!=returnDiv && StringUtils.isNotBlank(returnDiv)){
                                partPosition.setPartType(returnDiv);
                            }
                            partPosition.setCreateUser("PLM");
                            partPosition.setCreateTime(new Date());
                            if("1".equals(piData.getStatus())){
                                partPosition.setIsInvalid(false);
                            }else{
                                partPosition.setIsInvalid(true);
                            }
                            itemList.add(partPosition);
                        }

                    }
                    for (PartPosition partPosition : itemList){
                        partPosition.setCreateTime(new Date());
                        partPosition.setUpdateTime(new Date());
                        partPosition.setCreateUser("PI");
                        partPosition.setUpdateUser("PI");
                    }
                    partPositionService.addOrUpdatePartPosition(itemList);

                }else{

                    //不包含此字典值类型直接新增
                    if(!dictionaryTypeMap.containsKey(item.getCode())){
                        DictionaryType dictionaryType = new DictionaryType();
                        dictionaryType.setDictionaryTypeName(item.getName());
                        if(item.getCode()!=null){
                            dictionaryType.setDictionaryTypeCode(item.getCode());
                        }
                        dictionaryType.setCreateTime(new Date());
                        dictionaryType.setCreateUser("PLM");
                        dictionaryType.setIsInvalid(true);
                        iDictionaryTypeService.addDictionaryType(dictionaryType);
                    }

                    //其余的存入字典值
                    List<Dictionary> DictionaryList = new ArrayList<>();//有parentCode直接走索引
                    List<Dictionary> updateList = new ArrayList<>();
                    List<Dictionary> addList = new ArrayList<>();

                    List<PIDataDictionaryDicts> dictsList = item.getDicts();
                    for(PIDataDictionaryDicts piDataDictionaryDicts : dictsList){
                        if(piDataDictionaryDicts.getParentValues()!=null|| StringUtils.isNotBlank(piDataDictionaryDicts.getParentValues())) {
                            List<String> parentValues = Arrays.asList(piDataDictionaryDicts.getParentValues().split(","));
                            for(String parentValue : parentValues){
                                Dictionary dictionary = new Dictionary();
                                dictionary.setValueDesc(piDataDictionaryDicts.getLabel());
                                if(piDataDictionaryDicts.getValue()!=null&&piDataDictionaryDicts.getValue().length()>0){
                                    if(piDataDictionaryDicts.getValue().indexOf("-")!=-1){
                                        dictionary.setDicValue(StrUtil.sub(piDataDictionaryDicts.getValue(),0,piDataDictionaryDicts.getValue().indexOf("-")));
                                    }else{
                                        dictionary.setDicValue(piDataDictionaryDicts.getValue());
                                    }
                                }
                                dictionary.setDictionaryTypeCode(item.getCode());
                                if(parentValue.indexOf("-")!=-1){
                                    dictionary.setParentCode(StrUtil.sub(parentValue,0,parentValue.indexOf("-")));
                                }else{
                                    dictionary.setParentCode(parentValue);
                                }
                                if("1".equals(piDataDictionaryDicts.getStatus())){
                                    dictionary.setInvalid(false);
                                }else{
                                    dictionary.setInvalid(true);
                                }
                                dictionary.setUpdateUser("PLM");
                                dictionary.setCreateUser("PLM");
                                dictionary.setUpdateTime(new Date());
                                dictionary.setCreateTime(new Date());
                                DictionaryList.add(dictionary);
                            }
                        }else{
                            //dictionary.getDicValue()+"_"+dictionary.getDictionaryTypeCode()
                            Dictionary dictionary = new Dictionary();
                            dictionary.setValueDesc(piDataDictionaryDicts.getLabel());
                            if(piDataDictionaryDicts.getValue()!=null&&piDataDictionaryDicts.getValue().length()>0){
                                if(piDataDictionaryDicts.getValue().indexOf("-")!=-1){
                                    dictionary.setDicValue(StrUtil.sub(piDataDictionaryDicts.getValue(),0,piDataDictionaryDicts.getValue().indexOf("-")));
                                }else{
                                    dictionary.setDicValue(piDataDictionaryDicts.getValue());
                                }
                            }
                            if("1".equals(piDataDictionaryDicts.getStatus())){
                                dictionary.setInvalid(false);
                            }else{
                                dictionary.setInvalid(true);
                            }
                            dictionary.setDictionaryTypeCode(item.getCode());


                            if(dictionaryMap.containsKey(dictionary.getDicValue()+"_"+dictionary.getDictionaryTypeCode())){
                                Dictionary oldDictionary = dictionaryMap.get(dictionary.getDicValue() + "_" + dictionary.getDictionaryTypeCode());
                                dictionary.setId(oldDictionary.getId());
                                dictionary.setUpdateUser("PLM");
                                dictionary.setUpdateTime(new Date());
                                updateList.add(dictionary);
                            }else{
                                dictionary.setCreateUser("PLM");
                                dictionary.setCreateTime(new Date());
                                addList.add(dictionary);
                            }
                        }
                    }
                    if(null != addList && addList.size()>0){
                        IdictionaryService.addDictionaryList(addList);
                    }
                    if(null != updateList && updateList.size()>0){
                        IdictionaryService.updateDictionaryList(updateList);
                    }
                    if(null != DictionaryList && DictionaryList.size()>0){
                        IdictionaryService.updateAndAddDictionary(DictionaryList);
                    }


                }
                receivePiLog.setEndTime(new Date());
            } catch (Exception e) {
                e.printStackTrace();
                receivePiLog.setReturnDescribe(MessageConstant.STATUS_TEXT_Error_Count);
                receivePiLog.setReturnStatus(String.valueOf(MessageConstant.SERVER_FIELD_ERROR));
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


    public String returnDiv(PIDataDictionary item) {
        String returnDiv = "";
        if ("StylePosition".equalsIgnoreCase(item.getCode()) || "缝边位置".equals(item.getName())) {
            returnDiv = "sewing";
        } else if ("TAPosition".equalsIgnoreCase(item.getCode()) || "图案位置".equals(item.getName())) {
            returnDiv = "embroider";
        } else if ("BJPosition".equalsIgnoreCase(item.getCode()) || "部件位置".equals(item.getName())) {
            returnDiv = "bjposition";
        } else if ("ZSPosition".equalsIgnoreCase(item.getCode()) || "装饰位置".equals(item.getName())) {
            returnDiv = "decoration";
        }
        return returnDiv;
    }








}
