package com.ylzs.dao.plm;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.plm.PlmMasterDataComponent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 11:11 2020/3/12
 */
@Mapper
public interface PlmMasterDataComponentDao  extends BaseDAO<PlmMasterDataComponent> {
    void addPlmMasterDataComponent (List<PlmMasterDataComponent> plmMasterDataComponentList);

    void delPlmMasterDataComponent (@Param("styleCode")String styleCode);

    List<PlmMasterDataComponent> getAll();

    List<PlmMasterDataComponent> findMasterDataByCode(@Param("code") String code);


}
