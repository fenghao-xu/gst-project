package com.ylzs.service.custom.impl;

import com.ylzs.dao.custom.CustomStylePartCraftMotionDao;
import com.ylzs.entity.custom.CustomStylePartCraftMotion;
import com.ylzs.service.custom.ICustomStylePartCraftMotionService;
import com.ylzs.service.impl.OriginServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service()
public class CustomStylePartCraftMotionServiceImpl extends OriginServiceImpl<CustomStylePartCraftMotionDao, CustomStylePartCraftMotion> implements ICustomStylePartCraftMotionService {


    @Autowired
    private CustomStylePartCraftMotionDao motionDao;


    @Override
    public List<CustomStylePartCraftMotion> getCraftRandomCodeMotionList(List<Long> randomCodes) {
        List<CustomStylePartCraftMotion> list = new ArrayList<>();
        try {
            if(null != randomCodes && randomCodes.size()>0){
                list = motionDao.getCraftRandomCodeMotionList(randomCodes);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return list;
    }
    @Override
    public List<CustomStylePartCraftMotion> getCraftRandomCodeMotionList(Long partCraftRandomCode) {
        List<CustomStylePartCraftMotion> list = new ArrayList<>();
        try {
            list = motionDao.getCraftRandomCodeMotionListOne(partCraftRandomCode);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return list;
    }

    @Override
    public int deleteCustomStyleMotionList(List<Long> randomCodes) {
        return motionDao.deleteCustomStyleMotionList(randomCodes);
    }
}