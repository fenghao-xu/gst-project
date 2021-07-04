package com.ylzs.service.craftstd;


import com.ylzs.entity.craftstd.CraftFile;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 说明：
 *
 * @author lyq
 * 2019-09-30 10:45
 */
public interface ICraftFileService {
    Integer addCraftFile(CraftFile craftFile);

    Integer deleteCraftFile(long id);

    Integer updateCraftFile(CraftFile craftFile);

    List<CraftFile> getCraftFileById(long[] ids);

    List<CraftFile> getCraftFileByPage(Long keyId, String keyStr, Integer resourceType, String fileUrl);

    public List<CraftFile> getByKeyId(@Param("keyId") long keyId);

    public String getPicUrlOne(Long randomCode);

    List<CraftFile> getDuplicateFile();

    Integer deleteCraftFile(CraftFile craftFile);
}
