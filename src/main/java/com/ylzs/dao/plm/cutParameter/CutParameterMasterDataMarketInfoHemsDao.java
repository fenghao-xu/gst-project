package com.ylzs.dao.plm.cutParameter;

import com.ylzs.entity.plm.CutParameterMasterDataMarkInfo;
import com.ylzs.entity.plm.CutParameterMasterDataMarkInfoHems;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 9:59 2020/5/4
 */
@Mapper
public interface CutParameterMasterDataMarketInfoHemsDao {
   void addOrUpdateCutParameterMasterDataMarketInfoHems (List<CutParameterMasterDataMarkInfoHems> CutParameterMasterDataMarkInfoHems);
}
