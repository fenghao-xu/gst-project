package com.ylzs.service.craftstd.impl;

import com.ylzs.common.cache.StaticDataCache;
import com.ylzs.common.util.BaseException;
import com.ylzs.common.util.Const;
import com.ylzs.service.craftstd.ICopyFileCallback;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.UUID;


/**
 * 把文件从共享服务复制到本地的线程
 */
public class CopyFileThread  extends Thread  {
    private String shareFile;
    private String destFile;
    private ICopyFileCallback copyFileCallback;
    private Integer resourceType;
    //关联路径
    private String relativePath;
    private Long craftFileId;

    public String getRelativePath() {
        return this.relativePath;
    }

    public void setCraftFileId(Long craftFileId) {
        this.craftFileId = craftFileId;
    }

    public CopyFileThread(Integer resourceType, String destFile, String shareFile, ICopyFileCallback copyFileCallback, String relativePath) {
        super(destFile);
        this.resourceType = resourceType;
        this.shareFile = shareFile;
        this.destFile = destFile;
        this.copyFileCallback = copyFileCallback;
        this.relativePath = relativePath;
    }

    public String getShareAcc() {
        String shareAcc = StaticDataCache.getInstance().getValue(Const.SHARE_ACC, "");
        return shareAcc;
    }

    public String getSharePwd() {
        String sharePwd = StaticDataCache.getInstance().getValue(Const.SHARE_PWD, "");
        return sharePwd;
    }

    @Override
    public void run() {
        try {
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("", getShareAcc(), getSharePwd());
            SmbFile remoteFile = new SmbFile(shareFile, auth);
            if (!remoteFile.exists()) {
                throw new BaseException("文件不存在！" + shareFile);
            }
            if (!remoteFile.isFile()) {
                throw new BaseException("该路径不是文件！" + shareFile);
            }


            File dest = new File(destFile);
            if (!dest.getParentFile().exists()) {
                if (!dest.getParentFile().mkdir()) {
                    throw new BaseException("创建目录失败" + destFile);
                }
            }

            FileUtils.copyInputStreamToFile(remoteFile.getInputStream(), dest);

        } catch (Exception e) {
            e.printStackTrace();
            copyFileCallback.clearResource(craftFileId, resourceType, e.getMessage());
        }
    }


    public static CopyFileThread shareFileUpload(ICopyFileCallback copyFileCallback, Integer resourceType, String sharePath, String basePath, String destPath) {

        String oldSharePath = sharePath;
        if (basePath != "" && !basePath.endsWith(File.separator)) {
            basePath = basePath + File.separator;
        }
        if (!destPath.endsWith(File.separator)) {
            destPath = destPath + File.separator;
        }

        //去掉win的盘符
//        if (sharePath.startsWith("\\\\")) {
//            //去掉\\
//            sharePath = sharePath.substring(2, sharePath.length());
//            sharePath = sharePath.replace("\\", "/");
//            sharePath = "smb://" + sharePath;
//        } else {
//            //去掉盘符 Y:\
//            sharePath = sharePath.substring(3, sharePath.length());
//            sharePath = sharePath.replace("\\", "/");
//            sharePath = "smb://" + shareBasePath + sharePath;
//        }

        String fileExt = oldSharePath.substring(oldSharePath.lastIndexOf("."), oldSharePath.length());
        String fileName = UUID.randomUUID().toString().replaceAll("-", "") + fileExt;

        CopyFileThread copyFile = new CopyFileThread(resourceType, basePath + destPath + fileName, sharePath, copyFileCallback, destPath + fileName);
        return copyFile;
    }
}
