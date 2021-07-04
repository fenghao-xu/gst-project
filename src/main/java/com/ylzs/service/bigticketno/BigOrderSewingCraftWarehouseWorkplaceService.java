package com.ylzs.service.bigticketno;

import com.ylzs.dao.bigstylecraft.StyleSewingCraftWarehouseWorkplaceDao;
import com.ylzs.dao.bigticketno.BigOrderSewingCraftWarehouseWorkplaceDao;
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
public class BigOrderSewingCraftWarehouseWorkplaceService {

    @Resource
    private BigOrderSewingCraftWarehouseWorkplaceDao bigOrderSewingCraftWarehouseWorkplaceDao;

    public List<SewingCraftWarehouseWorkplace> getDataByParamAndCraftCode(Long randomCode, String craftCode) {
        return bigOrderSewingCraftWarehouseWorkplaceDao.getDataBySewingCraftRandomCodeAndCraftCode(randomCode, craftCode);
    }

    public List<SewingCraftWarehouseWorkplace> getDataByParam(Map<String, Object> map) {
        return bigOrderSewingCraftWarehouseWorkplaceDao.getDataByParam(map);
    }
}
