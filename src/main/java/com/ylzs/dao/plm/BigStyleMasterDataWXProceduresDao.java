package com.ylzs.dao.plm;

import com.ylzs.entity.plm.BigStyleMasterDataWXProcedures;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author:tt
 * @Description：大货主数据-WXProcedures子表
 * @Date: Created in 2020/3/14
 */
@Mapper
public interface BigStyleMasterDataWXProceduresDao {
    int addOrUpdateBigStyleDataWXProceduresList(List<BigStyleMasterDataWXProcedures> bigStyleMasterDataWXProcedures);//增加或修改大货主数据 WXProcedures子表

}
