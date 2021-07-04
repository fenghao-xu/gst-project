package com.ylzs.dao.plm;

import com.ylzs.entity.plm.DesignPartMasterDataBom;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @Author:tt
* @Description：部件主数据
* @Date: Created in 2020/3/12
*/
@Mapper
public interface DesignPartDataBomDao {
    int addOrUpdatePartDataBom(List<DesignPartMasterDataBom> designPartBomDataList);//增加或修改部件主数据bom子表
}
