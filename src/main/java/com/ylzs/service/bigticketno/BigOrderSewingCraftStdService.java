package com.ylzs.service.bigticketno;

import com.ylzs.dao.bigstylecraft.StyleSewingCraftStdDao;
import com.ylzs.dao.bigticketno.BigOrderSewingCraftStdDao;
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
public class BigOrderSewingCraftStdService {

    @Resource
    private BigOrderSewingCraftStdDao bigOrderSewingCraftStdDao;

    public  void addSewingCraftStd(Map<String,Object> map){
        bigOrderSewingCraftStdDao.addSewingCraftStd(map);
    }

    public List<SewingCraftStd> getDataBySewingCraftRandomCodeAndCraftCode(Long randomCode,String craftCode){
        return bigOrderSewingCraftStdDao.getDataBySewingCraftRandomCodeAndCraftCode(randomCode,craftCode);
    }
}
