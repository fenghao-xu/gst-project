package com.ylzs.service.sewingcraftTmp;

import com.ylzs.dao.sewingcraftTmp.SewingcraftTmpDao;
import com.ylzs.entity.sewingcraft.SewingCraftWarehouse;
import com.ylzs.vo.SewingCraftResource;
import com.ylzs.vo.craftstd.CraftStdVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author xuwei
 * @create 2020-10-15 15:24
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SewingcraftTmpService {

    @Resource
    private SewingcraftTmpDao sewingcraftTmpDao;

    public List<SewingCraftWarehouse> getDataByPager(Map<String, Object> param) {
        return sewingcraftTmpDao.getDataByPager(param);
    }

    public CraftStdVo checkStdData(Long randomCode, String craftCode) {
        return sewingcraftTmpDao.checkStdData(randomCode, craftCode);
    }

    public List<String> getWorkplaceBySewingRandomAndCraft(Long randomCode, String craftCode) {
        return sewingcraftTmpDao.getWorkplaceBySewingRandomAndCraft(randomCode, craftCode);
    }

    public boolean updateSysnStatus(Long randomCode, String craftCode, Integer sysnStatus) {
        boolean flag = false;
        try {
            sewingcraftTmpDao.updateSysnStatus(randomCode, craftCode, sysnStatus);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
    public List<SewingCraftResource> getSewingStdPicAndVedio(Long randomCode, String craftCode){
        return  sewingcraftTmpDao.getSewingStdPicAndVedio(randomCode,craftCode);
    }
}
