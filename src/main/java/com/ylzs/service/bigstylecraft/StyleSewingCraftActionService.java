package com.ylzs.service.bigstylecraft;

import com.ylzs.dao.bigstylecraft.StyleSewingCraftActionDao;
import com.ylzs.dao.sewingcraft.SewingCraftActionDao;
import com.ylzs.entity.sewingcraft.SewingCraftAction;
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
public class StyleSewingCraftActionService {

    @Resource
    private StyleSewingCraftActionDao styleSewingCraftActionDao;

    public List<SewingCraftAction> getDataBySewingCraftRandomCodeAndCraftCode(Long randomCode,String craftCode) {
        return styleSewingCraftActionDao.getDataBySewingCraftRandomCodeAndCraftCode(randomCode,craftCode);
    }

    public List<SewingCraftAction> getDataBySewingCraftCode(String code) {
        return styleSewingCraftActionDao.getDataBySewingCraftCode(code);
    }
}
