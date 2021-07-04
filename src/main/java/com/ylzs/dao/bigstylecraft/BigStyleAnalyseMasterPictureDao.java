package com.ylzs.dao.bigstylecraft;

import com.ylzs.entity.bigstylecraft.BigStyleAnalyseMasterPicture;
import com.ylzs.entity.plm.BigStyleMasterDataSKC;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xuwei
 * @create 2020-03-24 15:34
 */
public interface BigStyleAnalyseMasterPictureDao {
    public void insertPicForeach(@Param("picList") List<BigStyleAnalyseMasterPicture> picList);

    public void deleteByStyleRandomCode(Long randomCode);

    public List<String> getUrlByStyleRandomCode(Long styleRandomCode);

    public List<BigStyleAnalyseMasterPicture>getDataByStyleRandomCode(@Param("styleRandomCode")Long styleRandomCode);

    public void deleteByID(@Param("id") Long id);

}
