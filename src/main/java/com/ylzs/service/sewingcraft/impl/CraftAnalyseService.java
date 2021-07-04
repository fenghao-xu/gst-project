package com.ylzs.service.sewingcraft.impl;

import com.ylzs.common.util.StringUtils;
import com.ylzs.dao.craftAnalyse.CraftAnalyseDao;
import com.ylzs.dao.sewingcraft.SewingCraftWarehouseDao;
import com.ylzs.service.sewingcraft.ICraftAnalyseService;
import com.ylzs.vo.sewing.CraftAnalyseVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CraftAnalyseService implements ICraftAnalyseService {
    @Resource
    private CraftAnalyseDao craftAnalyseDao;

    /**
     * 查询部件工艺
     */
    public List<CraftAnalyseVo>getFromPartCraft(Map<String,Object> param){
        return craftAnalyseDao.getFromPartCraft(param);
    }

    /**
     * 查询智库款工艺
     */
    public List<CraftAnalyseVo>getFromThinkStyle(Map<String,Object> param){
        return craftAnalyseDao.getFromThinkStyle(param);
    }

    /**
     * 查询大货工艺
     */
    public List<CraftAnalyseVo>getFromBigStyle(Map<String,Object> param){
        return craftAnalyseDao.getFromBigStyle(param);
    }

    /**
     * 查询工单工艺
     */
    public List<CraftAnalyseVo>getFromBigOrder(Map<String,Object> param){
        return craftAnalyseDao.getFromBigOrder(param);
    }

    /**
     * 查询定制工艺
     */
    public List<CraftAnalyseVo>getFromCustomerOrder(Map<String,Object> param){
        return craftAnalyseDao.getFromCustomerOrder(param);
    }
}
