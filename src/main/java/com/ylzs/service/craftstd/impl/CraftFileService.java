package com.ylzs.service.craftstd.impl;

import com.ylzs.dao.craftstd.CraftFileDao;
import com.ylzs.entity.craftstd.CraftFile;
import com.ylzs.service.craftstd.ICraftFileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static com.ylzs.common.util.FastDFSUtil.deleteFileByUrl;

/**
 * 说明：
 *
 * @author lyq
 * 2019-09-30 10:46
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CraftFileService implements ICraftFileService {
    @Resource
    private CraftFileDao craftFileDao;


    @Override
    public Integer addCraftFile(CraftFile craftFile) {
        return craftFileDao.addCraftFile(craftFile);
    }

    @Override
    public List<CraftFile> getByKeyId(long keyId) {
        return craftFileDao.getByKeyId(keyId);
    }

    @Override
    public String getPicUrlOne(Long randomCode) {
        return craftFileDao.getPicUrlOne(randomCode);
    }

    @Override
    public List<CraftFile> getDuplicateFile() {
        return craftFileDao.getDuplicateFile();
    }

    @Override
    public Integer deleteCraftFile(CraftFile craftFile) {
        deleteFileByUrl(craftFile.getFileUrl());
        return craftFileDao.deleteCraftFile(craftFile.getId());
    }

    @Override
    public Integer deleteCraftFile(long id) {
        long[] ids = new long[]{id};
        List<CraftFile> craftFiles = craftFileDao.getCraftFileById(ids);
        int ret = 0;
        if (craftFiles != null && craftFiles.size() > 0) {
            CraftFile craftFile = craftFiles.get(0);
//            Integer count = craftFileDao.getCountByFileUploadId(craftFile.getFileUploadId());
//            //没有被多次引用
//            if (count != null && count.intValue() == 1) {
//                fileUploadService.deleteFileUpload(craftFile.getFileUploadId());
//            }
            ret = craftFileDao.deleteCraftFile(craftFile.getId());
        }
        return ret;
    }

    @Override
    public Integer updateCraftFile(CraftFile craftFile) {
        return craftFileDao.updateCraftFile(craftFile);
    }

    @Override
    public List<CraftFile> getCraftFileById(long[] ids) {
        return craftFileDao.getCraftFileById(ids);
    }

    @Override
    public List<CraftFile> getCraftFileByPage(Long keyId, String keyStr, Integer resourceType, String fileUrl) {
        return craftFileDao.getCraftFileByPage(keyId, keyStr, resourceType, fileUrl);
    }


}
