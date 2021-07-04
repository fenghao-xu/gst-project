package com.ylzs.service.bigstylecraft;

import com.ylzs.dao.bigstylecraft.StyleSewingCraftStdDao;
import com.ylzs.dao.sewingcraft.SewingCraftStdDao;
import com.ylzs.entity.sewingcraft.SewingCraftStd;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author xuwei
 * @create 2020-03-17 17:01
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class StyleSewingCraftStdService {

    @Resource
    private StyleSewingCraftStdDao styleSewingCraftStdDao;

    public  void addSewingCraftStd(Map<String,Object> map){
        styleSewingCraftStdDao.addSewingCraftStd(map);
    }

    public List<SewingCraftStd> getDataBySewingCraftRandomCodeAndCraftCode(Long randomCode,String craftCode){
        return styleSewingCraftStdDao.getDataBySewingCraftRandomCodeAndCraftCode(randomCode,craftCode);
    }
}
