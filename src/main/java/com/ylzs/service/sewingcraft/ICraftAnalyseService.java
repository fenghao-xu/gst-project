package com.ylzs.service.sewingcraft;

import com.ylzs.vo.sewing.CraftAnalyseVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ICraftAnalyseService {
    /**
     * 查询部件工艺
     */
    public List<CraftAnalyseVo>getFromPartCraft(Map<String,Object> param);

    /**
     * 查询智库款工艺
     */
    public List<CraftAnalyseVo>getFromThinkStyle(Map<String,Object> param);

    /**
     * 查询大货工艺
     */
    public List<CraftAnalyseVo>getFromBigStyle(Map<String,Object> param);

    /**
     * 查询工单工艺
     */
    public List<CraftAnalyseVo>getFromBigOrder(Map<String,Object> param);

    /**
     * 查询定制工艺
     */
    public List<CraftAnalyseVo>getFromCustomerOrder(Map<String,Object> param);
}
