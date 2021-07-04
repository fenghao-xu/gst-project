package com.ylzs.service.plm;

import com.ylzs.entity.plm.DesignPartMasterDataBom;

import java.util.List;

/**
* @Author:tt
* @Description： 部件主数据
* @Date: Created in  2020/3/12
*/
public interface IDesignPartMainDataBomService {
    int addOrUpdatePartDataBom(List<DesignPartMasterDataBom> designPartMasterDataBomList);//增加或修改部件主数据bom子表
}
