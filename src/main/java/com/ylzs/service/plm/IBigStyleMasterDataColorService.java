package com.ylzs.service.plm;

import com.ylzs.entity.plm.BigStyleMasterDataColor;

import java.util.List;

/**
 * @Author:tt
 * @Description：大货主数据-Color
 * @Date: Created in 2020/3/14
 */
public interface IBigStyleMasterDataColorService {
    int addOrUpdateBigStyleDataColorList(List<BigStyleMasterDataColor> bigStyleMasterDataColor);//增加或修改大货主数据 Color子表

}
