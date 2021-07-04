package com.ylzs.service.timeparam;

import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.util.Result;
import com.ylzs.common.util.SnowflakeIdUtil;
import com.ylzs.controller.auth.UserContext;
import com.ylzs.dao.craftstd.CraftCategoryDao;
import com.ylzs.dao.timeparam.WideCoefficientDao;
import com.ylzs.entity.timeparam.WideCoefficient;
import com.ylzs.entity.timeparam.WideCoefficientReq;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author xuwei
 * @create 2020-02-27 15:43
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class WideCoefficientService {
    @Resource
    private WideCoefficientDao wideCoefficientDao;
    @Resource
    private CraftCategoryDao craftCategoryDao;

    /**
     * 获取所有的宽放系数
     */
    public List<WideCoefficient> getAllWideCoefficient() {
        return wideCoefficientDao.getAllWideCoefficient();
    }

    /**
     * 根据品类查询对应的宽放系数
     */
    public List<WideCoefficient> getWideCoefficientByCraftCatagory() {
        return wideCoefficientDao.getWideCoefficientByCraftCatagory();
    }

    public Result addWideCoefficient(WideCoefficientReq wideCoefficientReq, UserContext currentUser) {
        String[] craftCategoryCodes = wideCoefficientReq.getCraftCategoryCodes();
        ArrayList<String> craftCategoryCodeList = new ArrayList<String>(Arrays.asList(craftCategoryCodes));
        List<WideCoefficient> wideCoefficientList = new ArrayList<>();
        for(String craftCategoryCode : craftCategoryCodeList){
            WideCoefficient wideCoefficient = new WideCoefficient();
            wideCoefficient.setWideName(wideCoefficientReq.getWideName());
            wideCoefficient.setWideCode(wideCoefficientReq.getWideCode());
            wideCoefficient.setCoefficient(wideCoefficientReq.getCoefficient());
            wideCoefficient.setCraftCategoryCode(craftCategoryCode);
            wideCoefficient.setCreateUser(currentUser.getUser());
            wideCoefficient.setCreateTime(new Date());
            wideCoefficient.setUpdateUser(currentUser.getUser());
            wideCoefficient.setUpdateTime(new Date());
            wideCoefficient.setStatus(BusinessConstants.Status.DRAFT_STATUS);
            wideCoefficient.setRemark(wideCoefficientReq.getRemark());
            wideCoefficient.setRandomCode(SnowflakeIdUtil.generateId());
            wideCoefficientList.add(wideCoefficient);
        }
        wideCoefficientDao.addWideCoefficient(wideCoefficientList);


        return Result.ok();
    }

    public Result updateWideCoefficient(WideCoefficientReq wideCoefficientReq) {
        String[] craftCategoryCodes = wideCoefficientReq.getCraftCategoryCodes();
        if(craftCategoryCodes.length>0){
            WideCoefficient wideCoefficientone = new WideCoefficient();
            wideCoefficientone.setWideCode(wideCoefficientReq.getWideCode());
            wideCoefficientone.setUpdateUser(wideCoefficientReq.getUpdateUser());
            wideCoefficientone.setUpdateTime(wideCoefficientReq.getUpdateTime());
            wideCoefficientone.setStatus(BusinessConstants.Status.IN_VALID);
            wideCoefficientDao.deleteByWideCode(wideCoefficientone);
            ArrayList<String> craftCategoryCodeList = new ArrayList<String>(Arrays.asList(craftCategoryCodes));
            List<WideCoefficient> wideCoefficientList = new ArrayList<>();
            for(String  craftCategoryCode : craftCategoryCodeList){
                WideCoefficient wideCoefficient = new WideCoefficient();
                wideCoefficient.setRandomCode(SnowflakeIdUtil.generateId());
                wideCoefficient.setCraftCategoryCode(craftCategoryCode);
                wideCoefficient.setCoefficient(wideCoefficientReq.getCoefficient());
                wideCoefficient.setWideCode(wideCoefficientReq.getWideCode());
                wideCoefficient.setWideName(wideCoefficientReq.getWideName());
                wideCoefficient.setRemark(wideCoefficientReq.getRemark());
                wideCoefficient.setUpdateTime(new Date());
                wideCoefficient.setStatus(BusinessConstants.Status.DRAFT_STATUS);
                wideCoefficient.setUpdateUser(wideCoefficientReq.getUpdateUser());
                wideCoefficient.setRemark(wideCoefficientReq.getRemark());
                wideCoefficientList.add(wideCoefficient);
            }
            wideCoefficientDao.updateByWideAndCategory(wideCoefficientList);
        }
        return Result.ok();
    }

    public Result deleteWideCoefficiente(String[] split, UserContext currentUser) {
        ArrayList<String> strappingCodeList = new ArrayList<String>(split.length);
        Collections.addAll(strappingCodeList, split);
        List<WideCoefficient> wideCoefficientList= wideCoefficientDao.getWideCoefficientByCode(strappingCodeList);
        for(WideCoefficient wideCoefficient : wideCoefficientList){
            wideCoefficient.setStatus(BusinessConstants.Status.IN_VALID);
            wideCoefficient.setUpdateTime(new Date());
            wideCoefficient.setUpdateUser(currentUser.getUser());
        }
        wideCoefficientDao.deleteWideCoefficient(wideCoefficientList);
        return Result.ok();

    }

    public List<WideCoefficient> getWideCoefficientByPage(String keywords, Date newbeginDate, Date newendDate){
        return wideCoefficientDao.getWideCoefficientByPage(keywords,newbeginDate,newendDate);
    }

    public WideCoefficientReq getWideCoefficientByWideCode(String wideCode) {
        WideCoefficientReq wideCoefficientReq  =new WideCoefficientReq();
        WideCoefficient wideCoefficient = wideCoefficientDao.getWideCoefficientByWideCode(wideCode);
        wideCoefficientReq.setStatus( String.valueOf(wideCoefficient.getStatus()));
        wideCoefficientReq.setCraftCategoryCode(wideCoefficient.getCraftCategoryCode());
        wideCoefficientReq.setCoefficient(wideCoefficient.getCoefficient());
        wideCoefficientReq.setWideName(wideCoefficient.getWideName());
        wideCoefficientReq.setWideCode(wideCoefficient.getWideCode());
        wideCoefficientReq.setRemark(wideCoefficient.getRemark());
        if(wideCoefficient.getCraftCategoryCode().indexOf(",")!=-1){
            String[] split = wideCoefficient.getCraftCategoryCode().split(",");
            wideCoefficientReq.setCraftCategoryCodes(split);
        }else{
            String[] split = new String[1];
            split[0] = wideCoefficient.getCraftCategoryCode();
            wideCoefficientReq.setCraftCategoryCodes(split);
        }

        return wideCoefficientReq;
    }


}
