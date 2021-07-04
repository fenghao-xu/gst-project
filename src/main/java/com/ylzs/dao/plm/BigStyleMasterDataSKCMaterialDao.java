package com.ylzs.dao.plm;

import com.ylzs.entity.plm.BigStyleMasterDataSKCMaterial;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author:tt
 * @Description：大货主数据SKC-Material
 * @Date: Created in 2020/3/14
 */
@Mapper
public interface BigStyleMasterDataSKCMaterialDao {
    int addOrUpdateBigStyleDataSKCMaterialList(List<BigStyleMasterDataSKCMaterial> bigStyleMasterDataSKCMaterial);//增加或修改大货主数据 SKC子表-Material子表
}
