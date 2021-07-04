package com.ylzs.service.timeparam.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.constant.RedisContants;
import com.ylzs.common.util.SnowflakeIdUtil;
import com.ylzs.common.util.StringUtils;
import com.ylzs.common.util.UUIDUtil;
import com.ylzs.controller.timeparam.SameLevelCraftController;
import com.ylzs.dao.timeparam.SameLevelCraftDao;
import com.ylzs.entity.bigstylecraft.BigStyleAnalyseMaster;
import com.ylzs.entity.bigstylecraft.BigStyleAnalysePartCraft;
import com.ylzs.entity.bigstylecraft.BigStyleOperationLog;
import com.ylzs.entity.craftstd.CraftCategory;
import com.ylzs.entity.craftstd.MakeType;
import com.ylzs.entity.timeparam.SameLevelCraft;
import com.ylzs.service.craftstd.impl.CraftCategoryService;
import com.ylzs.service.craftstd.impl.MakeTypeService;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author xuwei
 * @create 2020-06-22 15:04
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SameLevelCraftService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SameLevelCraftService.class);

    @Resource
    private SameLevelCraftDao sameLevelCraftDao;
    @Resource
    private CraftCategoryService craftCategoryService;
    @Resource
    private MakeTypeService makeTypeService;

    public List<SameLevelCraft> getAllData() {
        return sameLevelCraftDao.getAllData();
    }

    public Map<String, SameLevelCraft> getAllDataToMap() {
        return sameLevelCraftDao.getAllDataToMap();
    }

    public List<SameLevelCraft> getByParam(String keywords,String craftCategoryCode,String makeTypeCode) {

        return sameLevelCraftDao.getByParam(keywords,craftCategoryCode,makeTypeCode);
    }

    public SameLevelCraft getDetails(Integer id){
        return sameLevelCraftDao.getDetails(id);
    }

    public String getMaxNumber(String craftCategoryCode){
        return sameLevelCraftDao.getMaxNumber(craftCategoryCode);
    }

    public void updateSameLevelCraft(SameLevelCraft sameLevelCraft){
        sameLevelCraftDao.updateSameLevelCraft(sameLevelCraft);
    }

    public void addSameLevelCraft(SameLevelCraft sameLevelCraft){
        sameLevelCraftDao.addSameLevelCraft(sameLevelCraft);
    }

    public JSONObject deleteSameLevelCraft(Long id){
        JSONObject reults = new JSONObject();
        try{
            sameLevelCraftDao.deleteSameLevelCraft(id);
            reults.put("mes","200");
        }catch (Exception e){
            e.printStackTrace();
        }
        return reults;

    }

    public JSONObject addOrUpdate(JSONObject dataObj, String operation){
        JSONObject result = new JSONObject();
        final Map<String,CraftCategory> craftCategoryMap = selcraftCategory();
        final Map<String,MakeType> makeTypeMap = selMakeType();
        //款式编码
        try {
            SameLevelCraft sameLevelCraft = new SameLevelCraft();
            String sameLevelCraftName = dataObj.getString("sameLevelCraftName");
            sameLevelCraft.setSameLevelCraftName(sameLevelCraftName);
            String craftCategoryCode = dataObj.getString("craftCategoryCode");
            sameLevelCraft.setCraftCategoryCode(craftCategoryCode);
            String maxNumber = getMaxNumber(craftCategoryCode);//同一工艺品类下最大的流水号
            String craftCategoryName = craftCategoryMap.get(craftCategoryCode).getCraftCategoryName();
            sameLevelCraft.setCraftCategoryName(craftCategoryName);
            String makeTypeCode = dataObj.getString("makeTypeCode");
            sameLevelCraft.setMakeTypeCode(makeTypeCode);
            String makeTypeName = makeTypeMap.get(makeTypeCode).getMakeTypeName();
            sameLevelCraft.setMakeTypeName(makeTypeName);
            String hardLevel = dataObj.getString("hardLevel");
            sameLevelCraft.setHardLevel(hardLevel);
            String remark  = dataObj.getString("remark");
            sameLevelCraft.setRemark(remark);
            //同级工序流水号
            Integer nowNumber = Integer.parseInt(maxNumber);
            nowNumber = nowNumber + 1;
            //同级工序编码
            String sameLevelCraftNumericalCode  ="";
            String sameLevelCraftSerial = String.valueOf(nowNumber);
            sameLevelCraft.setSameLevelCraftSerial(sameLevelCraftSerial);
            if(StringUtils.isNotBlank(craftCategoryMap.get(craftCategoryCode).getClothesBigCategoryCode()) && StringUtils.isNotBlank(makeTypeMap.get(makeTypeCode).getMakeTypeNumericalCode())){
                sameLevelCraft.setClothesBigCatogoryCode(craftCategoryMap.get(craftCategoryCode).getClothesBigCategoryCode());
                sameLevelCraft.setMakeTypeNumericalCode(makeTypeMap.get(makeTypeCode).getMakeTypeNumericalCode());
                sameLevelCraftNumericalCode = craftCategoryMap.get(craftCategoryCode).getClothesBigCategoryCode()+makeTypeMap.get(makeTypeCode).getMakeTypeNumericalCode()+sameLevelCraftSerial;
                sameLevelCraft.setSameLevelCraftNumericalCode(sameLevelCraftNumericalCode);
            }else{
                LOGGER.info("工艺品类或做工类型数据不完整");
                return result;
            }

            if (BusinessConstants.Send2Pi.actionType_update.equals(operation)) {
                //修改
                long id = dataObj.getLong("id");
                String updateUser = dataObj.getString("userCode");
                sameLevelCraft.setUpdateUser(updateUser);
                sameLevelCraft.setUpdateTime(new Date());
                sameLevelCraft.setId(id);
                sameLevelCraftDao.updateSameLevelCraft(sameLevelCraft);
                result.put("mes","200");

            } else {
                //新增
                String createUser = dataObj.getString("userCode");
                sameLevelCraft.setCreateUser(createUser);
                sameLevelCraft.setCreateTime(new Date());
                sameLevelCraftDao.addSameLevelCraft(sameLevelCraft);
                result.put("mes","200");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public Map<String, CraftCategory> selcraftCategory(){
        Map<String, CraftCategory> craftCategoryMap = new HashMap<>();
        List<CraftCategory> craftCategories = craftCategoryService.getAllValidCraftCategory();
        for(CraftCategory craftCategory : craftCategories){
            craftCategoryMap.put(craftCategory.getCraftCategoryCode(),craftCategory);
        }
        return craftCategoryMap;

    }

    public Map<String,MakeType> selMakeType(){
        Map<String, MakeType> makeTypeMap = new HashMap<>();
        List<MakeType> makeTypes = makeTypeService.getAllMakeType();
        if(null != makeTypes && makeTypes.size()>0){
            for(MakeType makeType : makeTypes){
                makeTypeMap.put(makeType.getMakeTypeCode(),makeType);
            }
        }
        return makeTypeMap;
    }
}
