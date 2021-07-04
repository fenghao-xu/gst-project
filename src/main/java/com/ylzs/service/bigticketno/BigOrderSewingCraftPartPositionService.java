package com.ylzs.service.bigticketno;

import com.ylzs.dao.bigstylecraft.StyleSewingCraftPartPositionDao;
import com.ylzs.dao.bigticketno.BigOrderSewingCraftPartPositionDao;
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
public class BigOrderSewingCraftPartPositionService {

    @Resource
    private BigOrderSewingCraftPartPositionDao bigOrderSewingCraftPartPositionDao;

    public List<SewingCraftPartPosition> getDataBySewingCraftRandomCodeAndCraftCode(Long randomCode,String craftCode){
        return  bigOrderSewingCraftPartPositionDao.getDataBySewingCraftRandomCodeAndCraftCode(randomCode,craftCode);
    }
}
