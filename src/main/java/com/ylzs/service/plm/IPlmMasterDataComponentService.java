package com.ylzs.service.plm;


import com.ylzs.entity.plm.PlmMasterDataComponent;

import java.util.List;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 14:02 2020/3/12
 */
public interface IPlmMasterDataComponentService {

    void addPlmMasterDataComponent (List<PlmMasterDataComponent> plmMasterDataComponentList);

    void delPlmMasterDataComponent (String styleCode);

    List<PlmMasterDataComponent> getAll();


}
