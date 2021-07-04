package com.ylzs.service.plm;

import com.ylzs.entity.plm.BigStyleMasterDataSKC;

import java.util.List;

/**
 * @Author:tt
 * @Description：大货主数据-SKC
 * @Date: Created in 2020/3/14
 */
public interface IBigStyleMasterDataSKCService {
    int addOrUpdateBigStyleDataSKCList(List<BigStyleMasterDataSKC> bigStyleMasterDataSKC);//增加或修改大货主数据 SKC子表
}
