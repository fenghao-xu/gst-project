package com.ylzs.service.plm.impl;

import com.ylzs.dao.plm.PiCutParameterMarkerinfoHemsDao;
import com.ylzs.entity.plm.PiCutParameterMarkerinfoHems;
import com.ylzs.service.impl.OriginServiceImpl;
import com.ylzs.service.plm.IPiCutParameterMarkerinfoHemsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @className PiCutParameterMarkerinfoHemsServiceImpl
 * @Description
 * @Author sky
 * @create 2020-03-20 10:06:46
 */
@Service
public class PiCutParameterMarkerinfoHemsServiceImpl extends OriginServiceImpl<PiCutParameterMarkerinfoHemsDao, PiCutParameterMarkerinfoHems> implements IPiCutParameterMarkerinfoHemsService {

    private static final Logger LOG = LoggerFactory.getLogger(PiCutParameterMarkerinfoHemsServiceImpl.class);
    @Autowired
    private PiCutParameterMarkerinfoHemsDao piCutParameterMarkerinfoHemsDao;

    @Override
    public List<PiCutParameterMarkerinfoHems> getOrderMarkerInfoHemosAll(String orderId,String orderLineId) {
        List<PiCutParameterMarkerinfoHems> hemsList = new ArrayList<>();
        try {
            hemsList = piCutParameterMarkerinfoHemsDao.getOrderMarkerInfoHemosAll(orderId,orderLineId);
        }catch (Exception ex){
            LOG.error("查询缝边位置数据异常:{0}",ex);
            hemsList = new ArrayList<>();
        }
        return hemsList;
    }
}
