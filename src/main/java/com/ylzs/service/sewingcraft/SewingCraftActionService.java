package com.ylzs.service.sewingcraft;

import com.ylzs.dao.sewingcraft.SewingCraftActionDao;
import com.ylzs.entity.sewingcraft.SewingCraftAction;
import com.ylzs.entity.sewingcraft.SewingCraftWarehouse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xuwei
 * @create 2020-03-17 9:07
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SewingCraftActionService {

    @Resource
    private SewingCraftActionDao sewingCraftActionDao;

    public List<SewingCraftAction> getDataBySewingCraftRandomCode(Long randomCode) {
        return sewingCraftActionDao.getDataBySewingCraftRandomCode(randomCode);
    }

    public List<SewingCraftAction> getDataBySewingCraftCode(String code) {
        return sewingCraftActionDao.getDataBySewingCraftCode(code);
    }
}
