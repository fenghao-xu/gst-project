package com.ylzs.service.sewingcraft;

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
public class SewingCraftStdService {

    @Resource
    private SewingCraftStdDao sewingCraftStdDao;

    public  void addSewingCraftStd(Map<String,Object> map){
        sewingCraftStdDao.addSewingCraftStd(map);
    }

    public List<SewingCraftStd> getDataBySewingCraftRandomCode(Long randomCode){
        return sewingCraftStdDao.getDataBySewingCraftRandomCode(randomCode);
    }
}
