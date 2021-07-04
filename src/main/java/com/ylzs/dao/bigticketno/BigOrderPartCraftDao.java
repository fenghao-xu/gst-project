package com.ylzs.dao.bigticketno;

import com.ylzs.entity.bigstylecraft.BigStyleAnalysePartCraft;
import com.ylzs.entity.designPart.DesignPart;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author xuwei
 * @create 2020-03-24 9:55
 */
public interface BigOrderPartCraftDao {
    public void insertPartCraftForeach(@Param("partCraftList") List<BigStyleAnalysePartCraft> partCraftList);

    public void deleteByStyleIds(@Param("ids") List<Long> ids);

    public List<BigStyleAnalysePartCraft> getDataByStyleRandomCode(Long styleRandomCode);


    public List<Long> getIdByStyleRandomCode(Long styleRandomCode);

    public List<BigStyleAnalysePartCraft> getPartAndDetailByStyleRandomCode(Long styleRandomCode);

    @MapKey("remark")
    Map<String, DesignPart> getBigOrderDesignPart(@Param("styleRandomCode") Long styleRandomCode);

    public List<BigStyleAnalysePartCraft> getPartAndDetailByStyleRandomCodeAndOrderByCraftNo(Long styleRandomCode);
}
