package com.ylzs.dao.plm;

import com.ylzs.entity.plm.BigStyleMasterData;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author:tt
 * @Description：大货主数据
 * @Date: Created in 2020/3/14
 */
public interface BigStyleMasterDataDao {
    public int addOrUpdateBigStyleData(BigStyleMasterData bigStyleMasterData);//增加或修改大货主数据

    public List<BigStyleMasterData> getAllDataForPage(Map<String, Object> params);

    BigStyleMasterData getBigStyleMasterDataOne(@Param("styleCode") String styleCode);

    public List<BigStyleMasterData> getDataByStyleRandomCode(@Param("tableName") String tableName, @Param("styleRandomCode") Long styleRandomCode);
}
