package com.ylzs.service.plm;

import com.ylzs.entity.plm.BigStyleMasterDataWXProcedures;

import java.util.List;

/**
 * @Author:tt
 * @Description：大货主数据WXProcedures
 * @Date: Created in 2020/3/14
 */
public interface IBigStyleMasterDataWXProceduresService {
    int addOrUpdateBigStyleDataWXProceduresList(List<BigStyleMasterDataWXProcedures> bigStyleMasterDataWXProcedures);//增加或修改大货主数据 WXProcedures子表

}
