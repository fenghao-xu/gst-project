package com.ylzs.service.bigstylecraft;

import com.ylzs.dao.bigstylecraft.StyleSewingCraftPartPositionDao;
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
public class StyleSewingCraftPartPositionService {

    @Resource
    private StyleSewingCraftPartPositionDao styleSewingCraftPartPositionDao;

    public List<SewingCraftPartPosition> getDataBySewingCraftRandomCodeAndCraftCode(Long randomCode,String craftCode){
        return  styleSewingCraftPartPositionDao.getDataBySewingCraftRandomCodeAndCraftCode(randomCode,craftCode);
    }
}
