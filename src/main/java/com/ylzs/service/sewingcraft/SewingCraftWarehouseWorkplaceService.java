package com.ylzs.service.sewingcraft;

import com.ylzs.dao.sewingcraft.SewingCraftWarehouseWorkplaceDao;
import com.ylzs.entity.sewingcraft.SewingCraftWarehouseWorkplace;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author xuwei
 * @create 2020-03-17 9:22
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SewingCraftWarehouseWorkplaceService {

    @Resource
    private SewingCraftWarehouseWorkplaceDao sewingCraftWarehouseWorkplaceDao;

    public List<SewingCraftWarehouseWorkplace> getDataBySewingCraftRandomCode(Long randomCode) {
        return sewingCraftWarehouseWorkplaceDao.getDataBySewingCraftRandomCode(randomCode);
    }

    public List<SewingCraftWarehouseWorkplace> getDataByParam(Map<String, Object> map) {
        return sewingCraftWarehouseWorkplaceDao.getDataByParam(map);
    }

    public int updateCraftFlowNum(String workplaceCraftCode, Integer craftFlowNum) {
        return sewingCraftWarehouseWorkplaceDao.updateCraftFlowNum(workplaceCraftCode, craftFlowNum);

    }
}
