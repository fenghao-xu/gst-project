package com.ylzs.dao.craftstd;

import com.ylzs.entity.craftstd.MaxCode;
import org.apache.ibatis.annotations.Param;

public interface MaxCodeDao {
    Integer addMaxCode(MaxCode maxCode);
    Integer deleteMaxCode(Integer id);
    Integer updateMaxCode(MaxCode maxCode);
    MaxCode getOneByCode(@Param("moduleCode") String moduleCode, @Param("preStr") String preStr);
    MaxCode getOneById(Integer id);
    Integer updateMaxId(MaxCode maxCode);
    Integer selectMaxId(@Param("moduleCode") String moduleCode, @Param("preStr") String preStr);
}