package com.ylzs.service.craftmainframe.impl;

import com.ylzs.dao.craftmainframe.CraftMainFrameRouteDao;
import com.ylzs.entity.craftmainframe.CraftMainFrameRoute;
import com.ylzs.service.craftmainframe.ICraftMainFrameRouteService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
@Service
public class CraftMainFrameRouteService implements ICraftMainFrameRouteService {

    @Resource
    private CraftMainFrameRouteDao craftMainFrameRouteDao;





    @Override
    public int deleteByPrimaryKey(Long randomCode) {
        return craftMainFrameRouteDao.deleteByPrimaryKey(randomCode);
    }

    @Override
    public int insert(CraftMainFrameRoute record) {
        return craftMainFrameRouteDao.insert(record);
    }

    @Override
    public int insertSelective(CraftMainFrameRoute record) {
        return craftMainFrameRouteDao.insertSelective(record);
    }



    @Override
    public CraftMainFrameRoute selectByPrimaryKey(Long randomCode) {
        return craftMainFrameRouteDao.selectByPrimaryKey(randomCode);
    }





    @Override
    public int updateByPrimaryKeySelective(CraftMainFrameRoute record) {
        return craftMainFrameRouteDao.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(CraftMainFrameRoute record) {
        return craftMainFrameRouteDao.updateByPrimaryKey(record);
    }

    @Override
    public int batchInsert(List<CraftMainFrameRoute> list) {
        return craftMainFrameRouteDao.batchInsert(list);
    }

    @Override
    public List<CraftMainFrameRoute> getByMainFrameRandomCode(Long mainFrameRandomCode) {
        return craftMainFrameRouteDao.getByMainFrameRandomCode(mainFrameRandomCode);
    }

    @Override
    public List<CraftMainFrameRoute> getMainFrameRouteByCode(String mainFrameCode) {
        return craftMainFrameRouteDao.getByMainFrameCode(mainFrameCode);
    }

    @Override
    public List<CraftMainFrameRoute> getByCondition(Long mainFrameRandomCode, String keywords, Date beginDate, Date endDate) {
        return craftMainFrameRouteDao.getByCondition(mainFrameRandomCode, keywords, beginDate, endDate);
    }

}
