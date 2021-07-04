package com.ylzs.service.sewingcraft;

import com.ylzs.dao.sewingcraft.SewingCraftPartPositionDao;
import com.ylzs.entity.sewingcraft.SewingCraftPartPosition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xuwei
 * @create 2020-03-17 9:15
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SewingCraftPartPositionService {

    @Resource
    private SewingCraftPartPositionDao sewingCraftPartPositionDao;

    public List<SewingCraftPartPosition> getDataBySewingCraftRandomCode(Long randomCode){
        return  sewingCraftPartPositionDao.getDataBySewingCraftRandomCode(randomCode);
    }
}
