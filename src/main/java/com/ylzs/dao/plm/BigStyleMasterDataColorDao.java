package com.ylzs.dao.plm;

import com.ylzs.entity.plm.BigStyleMasterDataColor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author:tt
 * @Description：大货主数据-Color子表
 * @Date: Created in 2020/3/14
 */
@Mapper
public interface BigStyleMasterDataColorDao {
    int addOrUpdateBigStyleDataColorList(List<BigStyleMasterDataColor> bigStyleMasterDataColor);//增加或修改大货主数据 Color子表
}
