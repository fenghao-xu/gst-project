package com.ylzs.dao.designPart;


import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.designPart.DesignPart;
import com.ylzs.vo.designpart.DesignPartSourceVo;
import com.ylzs.vo.designpart.DesignPartVo;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设计部件
 * 
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-29 11:53:47
 */
@Mapper
public interface DesignPartDao extends BaseDAO<DesignPart> {

    List<DesignPartVo> getDesignDataList(HashMap map);

    DesignPartVo selectByRandomCode(Long randomCode);

    int addOrUpdatePartData(DesignPart designPartVo);//增加或修改部件主数据主表

    List<DesignPartVo> getDesignCodeAll(@Param("partCodes") String [] partCodes);

    List<DesignPartSourceVo> getDesignPartSourceVos(@Param("designPartCode") String designPartCode, @Param("useIn") Integer useIn);

    @MapKey("designCode")
    public Map<String,DesignPartVo> getAllEmbroideryParts();



}
