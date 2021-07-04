package com.ylzs.service.timeparam;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.util.Result;
import com.ylzs.common.util.SnowflakeIdUtil;
import com.ylzs.controller.auth.UserContext;
import com.ylzs.dao.timeparam.StrappingTimeConfigDao;
import com.ylzs.entity.craftstd.MakeType;
import com.ylzs.entity.materialCraft.MaterialCraft;
import com.ylzs.entity.timeparam.StrappingTimeConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author xuwei
 * @create 2020-02-27 16:06
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class StrappingTimeConfigService {

    @Resource
    private StrappingTimeConfigDao strappingTimeConfigDao;

    /**
     * 获取所有的捆扎系数
     */
    public List<StrappingTimeConfig> getAllStrappingTimeConfigs() {
        return strappingTimeConfigDao.getAllStrappingTimeConfigs();
    }

    public Result addStrappingTime(StrappingTimeConfig strappingTimeConfig,UserContext currentUser) {
        strappingTimeConfig.setCreateUser(currentUser.getUser());
        strappingTimeConfig.setCreateTime(new Date());
        strappingTimeConfig.setUpdateUser(currentUser.getUser());
        strappingTimeConfig.setUpdateTime(new Date());
        strappingTimeConfig.setStatus(BusinessConstants.Status.DRAFT_STATUS);
        strappingTimeConfig.setRandomCode(SnowflakeIdUtil.generateId());
        strappingTimeConfigDao.insert(strappingTimeConfig);
        return Result.ok();
    }

    public Result deleteStrappingTime(String[] split, UserContext currentUser) {
        ArrayList<String> strappingCodeList = new ArrayList<String>(split.length);
        Collections.addAll(strappingCodeList, split);
        List<StrappingTimeConfig> strappingTimeList= strappingTimeConfigDao.getStrappingTimeByCode(strappingCodeList);
        for(StrappingTimeConfig strappingTimeConfig : strappingTimeList){
            strappingTimeConfig.setStatus(BusinessConstants.Status.IN_VALID);
            strappingTimeConfig.setUpdateTime(new Date());
            strappingTimeConfig.setUpdateUser(currentUser.getUser());
        }
        strappingTimeConfigDao.deleteStrappingTime(strappingTimeList);
        return Result.ok();

    }

    public Result updateStrappingTime(StrappingTimeConfig strappingTimeConfig) {
        UpdateWrapper<StrappingTimeConfig> craftUpdateWrapper = new UpdateWrapper<>();
        craftUpdateWrapper.eq("strapping_code", strappingTimeConfig.getStrappingCode());
        strappingTimeConfigDao.update(strappingTimeConfig,craftUpdateWrapper);
        return Result.ok();
    }

    public StrappingTimeConfig getStrappingTimeByCode(String strappingCode) {

        return strappingTimeConfigDao.getStrappingTimeByStrappingCode(strappingCode);
    }

    public List<StrappingTimeConfig> getStrappingTimeByPage(String keywords, Date newbeginDate, Date newendDate){
        return strappingTimeConfigDao.getStrappingTimeByPage(keywords,newbeginDate,newendDate);
    }

}
