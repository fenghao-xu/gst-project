package com.ylzs.dao.plm;

import com.ylzs.entity.plm.BigStyleMasterDataSKC;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author:tt
 * @Description：大货主数据-SKC子表
 * @Date: Created in 2020/3/14
 */
@Mapper
public interface BigStyleMasterDataSKCDao {
    int addOrUpdateBigStyleDataSKCList(List<BigStyleMasterDataSKC> bigStyleMasterDataSKC);//增加或修改大货主数据 SKC子表
}
