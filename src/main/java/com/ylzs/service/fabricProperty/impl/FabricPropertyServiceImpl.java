package com.ylzs.service.fabricProperty.impl;

import com.ylzs.common.util.Result;
import com.ylzs.dao.fabricProperty.FabricPropertyDao;
import com.ylzs.entity.fabricProperty.FabricProperty;
import com.ylzs.entity.materialCraft.resp.MaterialCraftPropertyResp;
import com.ylzs.service.fabricProperty.IFabricPropertyService;
import com.ylzs.service.impl.OriginServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author weikang
 * @Description
 * @Date 2020/3/9
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FabricPropertyServiceImpl extends OriginServiceImpl<FabricPropertyDao, FabricProperty> implements IFabricPropertyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FabricPropertyServiceImpl.class);

    @Resource
    private FabricPropertyDao fabricPropertyDao;

    @Override
    public Result getFabricProperty(String kindCode) {
        List<MaterialCraftPropertyResp> dataList = fabricPropertyDao.selectFabricPropertyList(kindCode);
        return Result.ok(dataList);
    }
}
