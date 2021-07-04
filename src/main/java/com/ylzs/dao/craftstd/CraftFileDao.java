package com.ylzs.dao.craftstd;


import com.ylzs.entity.craftstd.CraftFile;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 说明：
 *
 * @author lyq
 * 2019-09-29 16:14
 */
public interface CraftFileDao {
    Integer addCraftFile(CraftFile craftFile);

    Integer deleteCraftFile(long id);

    Integer updateCraftFile(CraftFile craftFile);

    List<CraftFile> getCraftFileById(@Param("ids") long[] ids);

    List<CraftFile> getCraftFileByPage(@Param("keyId") Long keyId,
                                       @Param("keyStr") String keyStr,
                                       @Param("resourceType") Integer resourceType,
                                       @Param("fileUrl") String fileUrl);

    Integer deleteByKeyId(@Param("keyId") long keyId, @Param("updateUser") String updateUser, @Param("updateTime") Date updateTime);

    Integer deleteByKeyStr(@Param("keyStr") String keyStr, @Param("updateUser") String updateUser, @Param("updateTime") Date updateTime);

    Integer deleteByKeyAndFileUrl(@Param("keyId") Long keyId,
                        @Param("keyStr") String keyStr,
                        @Param("resourceType") Integer resourceType,
                        @Param("fileUrl") String fileUrl);


    Boolean isCraftFileExist(@Param("fileUrl") String fileUrl);
    Boolean isCraftResourceExist(@Param("keyId") Long keyId, @Param("resourceType") Integer resourceType);

    List<CraftFile> getByKeyIdAndResourceType(@Param("keyId") long keyId, @Param("resourceType") int resourceType);

    public  List<CraftFile> getByKeyId(@Param("keyId") long keyId);

    public String getPicUrlOne(Long randomCode);

    List<CraftFile> getDuplicateFile();
}
