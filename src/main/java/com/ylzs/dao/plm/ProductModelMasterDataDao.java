package com.ylzs.dao.plm;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.plm.FabricMainData;
import com.ylzs.entity.plm.ProductModelMasterData;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 11:11 2020/3/12
 */
@Mapper
public interface ProductModelMasterDataDao extends BaseDAO<ProductModelMasterData> {

    void addOrUpdateProductModelDataDao(ProductModelMasterData productModelMasterData);
}
