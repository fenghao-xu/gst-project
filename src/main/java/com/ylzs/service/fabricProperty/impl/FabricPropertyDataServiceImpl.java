package com.ylzs.service.fabricProperty.impl;

import com.ylzs.dao.fabricProperty.FabricPropertyDataDao;
import com.ylzs.entity.fabricProperty.FabricPropertyData;
import com.ylzs.entity.fabricProperty.resp.FabricPropertyDataResp;
import com.ylzs.service.fabricProperty.IFabricPropertyDataService;
import com.ylzs.service.impl.OriginServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author weikang
 * @Description
 * @Date 2020/3/9
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FabricPropertyDataServiceImpl extends OriginServiceImpl<FabricPropertyDataDao, FabricPropertyData> implements IFabricPropertyDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FabricPropertyDataServiceImpl.class);

    @Resource
    private FabricPropertyDataDao propertyDataDao;

    @Override
    public List<FabricPropertyDataResp> selectPropertyDataList(String fabricPropertyCode, String propertyValue,String kindCode) {
        Map<String,String> map = new HashMap();
        map.put("fabricPropertyCode",fabricPropertyCode);
        map.put("propertyValue",propertyValue);
        map.put("kindCode",kindCode);
        List<FabricPropertyDataResp> propertyDataRespList = propertyDataDao.selectPropertyDataList(map);
        return propertyDataRespList;
    }

    @Override
    public List<FabricPropertyDataResp> selectMateriLCodePropertyDataList(String materialCode) {
        return propertyDataDao.selectMateriLCodePropertyDataList(materialCode);
    }
}
