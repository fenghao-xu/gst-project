package com.ylzs.service.plm;

import com.ylzs.entity.plm.ProductModelMasterData;

import java.util.List;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 14:02 2020/3/12
 */
public interface IProductModelMasterDataService {

    void addOrUpdateProductModelDataDao(ProductModelMasterData productModelMasterData);

    List<String> getAllStyleCode();


}
