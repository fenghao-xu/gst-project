package com.ylzs.dao.bigticketno;

import com.ylzs.entity.bigstylecraft.BigStyleAnalyseSkc;
import com.ylzs.entity.plm.BigStyleMasterDataSKC;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xuwei
 * @create 2020-03-24 15:10
 */
public interface BigOrderMasterSKCDao {
    public void insertSKCForeach(@Param("skcList") List<BigStyleMasterDataSKC> skcList);

    public void deleteByStyleRandomCode(Long randomCode);

    public void deleteByID(@Param("id") Long id);

    public List<BigStyleAnalyseSkc> selectDataByStyleRandomCode(@Param("randomCode")Long randomCode);
}
