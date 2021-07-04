package com.ylzs.service.craftstd.impl;

import com.ylzs.dao.craftstd.CraftCategoryDao;
import com.ylzs.dao.craftstd.CraftPartDao;
import com.ylzs.entity.craftstd.CraftPart;
import com.ylzs.service.craftstd.ICraftPartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 说明：工艺部件服务实现
 *
 * @author Administrator
 * 2019-09-30 10:06
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CraftPartService implements ICraftPartService {
    @Resource
    private CraftPartDao craftPartDao;

    @Resource
    private CraftCategoryDao craftCategoryDao;

    @Override
    public Integer addCraftPart(CraftPart craftPart) {
        return craftPartDao.addCraftPart(craftPart);
    }

    @Override
    public Integer updateCraftPart(CraftPart craftPart) {
        return craftPartDao.updateCraftPart(craftPart);
    }

    @Override
    public Integer deleteCraftPart(String craftPartCode, String userCode) {
//        Long count = craftPartDao.getStdCountByCraftPartCode(craftPartCode);
        Integer ret = 0;
//        if(count != null && count > 0) {
        CraftPart craftPart = new CraftPart();
        craftPart.setCraftPartCode(craftPartCode);
        craftPart.setIsInvalid(true);
        craftPart.setUpdateTime(new Date());
        craftPart.setUpdateUser(userCode);
        ret = craftPartDao.updateCraftPart(craftPart);
//        } else {
//            ret = craftPartDao.deleteCraftPart(craftPartCode);
//        }
        return ret;
    }

    @Override
    public List<CraftPart> getCraftPartByCode(String[] craftParCodes) {
        return craftPartDao.getCraftPartByCode(craftParCodes);
    }

    @Override
    public List<CraftPart> getAllValidCraftPart() {
        return craftPartDao.getAllValidCraftPart();
    }

    @Override
    public List<CraftPart> getCraftPartByPage(String keywords, Integer craftCategoryId, Date beginDate, Date endDate,String craftCategoryCode) {
        return craftPartDao.getCraftPartByPage(keywords, craftCategoryId, beginDate, endDate,craftCategoryCode);
    }

    @Override
    public List<CraftPart> getCraftPartByCategoryId(Integer craftCategoryId) {
        return craftPartDao.getCraftPartByCategoryId(craftCategoryId);
    }

    @Override
    public CraftPart getCraftPartById(Integer id) {
        return craftPartDao.getCraftPartById(id);
    }

    @Override
    public List<CraftPart> getCateGoryCodePartAll(String codeArray) {
        List<CraftPart> list =new ArrayList<>();
        try {
            list = craftPartDao.getCateGoryCodePartAll(codeArray);
        }catch (Exception ex){
            list = new ArrayList<>();
        }
        return list;
    }
}
