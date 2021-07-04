package com.ylzs.schedual;

import com.ylzs.common.cache.StaticDataCache;
import com.ylzs.common.util.Const;
import com.ylzs.service.craftstd.ICraftStdService;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author xuwei
 * @create 2019-12-27 13:35
 * 定时从共享盘获取文件层级信息
 */
//@Component
public class GetFilesInfoSchedual {
    private Logger logger = LoggerFactory.getLogger(GetFilesInfoSchedual.class);


    //每半小时执行
    @Async
    @Scheduled(cron = "0 0/3 * * * ? ")
    public void getFilesInfo() throws Exception{
        logger.info("---------开始获取共享盘文件信息-----------------");
        String basePath = StaticDataCache.getInstance().getValue(Const.SHARE_BASE_PATH, "");
        String remotePhotoUrl = "smb://" + basePath; // 10.7.200.135\share
        List<ICraftStdService.ShareFileInfo> shareFileInfos = new ArrayList<ICraftStdService.ShareFileInfo>();
        String shareAcc = StaticDataCache.getInstance().getValue(Const.SHARE_ACC, "");
        String sharePwd = StaticDataCache.getInstance().getValue(Const.SHARE_PWD, "");
        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("", shareAcc, sharePwd);
        SmbFile remoteFile = new SmbFile(remotePhotoUrl, auth);
        try {
            getFileInfo(remoteFile);
        }catch (Exception e){

        }
    }

    private void getFileInfo(SmbFile rootfile) throws Exception {
        if (!rootfile.exists() || rootfile.isFile()) {//不存在或者为空则退出循环
            return;
        }
        //是一个线程安全的List接口的实现
        CopyOnWriteArrayList picFileInfos = new CopyOnWriteArrayList();
        CopyOnWriteArrayList directoryFileInfos = new CopyOnWriteArrayList();
        CopyOnWriteArrayList mp4FileInfos = new CopyOnWriteArrayList();
        SmbFile files[] = rootfile.listFiles();
        if (null == files || files.length == 0) {
            return;
        }
        String rootFileName = rootfile.getName();
        String rootFullPath= rootfile.getPath();
        //开启多线程并行处理
        Arrays.stream(files).parallel().forEach(x -> {
            try {
                String fileName = x.getName();
                ICraftStdService.ShareFileInfo fileInfo = new ICraftStdService.ShareFileInfo();
                fileInfo.setFullPath(x.getPath());
                String fullPathEncode = URLEncoder.encode(x.getPath(), "utf-8").replaceAll("\\+", "%20")
                        .replaceAll("\\!", "%21")
                        .replaceAll("\\'", "%27")
                        .replaceAll("\\(", "%28")
                        .replaceAll("\\)", "%29")
                        .replaceAll("\\~", "%7E");
                fileInfo.setFileName(fileName);
                fileInfo.setFullPathEncode(fullPathEncode);
                if (x.isDirectory()) {
                    fileInfo.setIsFile(false);
                    fileInfo.setChildrens(null);
                    directoryFileInfos.add(fileInfo);
                    getFileInfo(x);//递归查找
                } else if (x.isFile()) {
                    String fileExt = "";
                    int k = fileName.lastIndexOf(".");
                    if(k >= 0) {
                        fileExt = fileName.substring(k);
                    }

                    fileInfo.setIsFile(true);
                    fileInfo.setChildrens(null);
                    if (fileExt.equalsIgnoreCase(".jpg") ||
                            fileExt.equalsIgnoreCase(".jpeg") ||
                            fileExt.equalsIgnoreCase(".bmp") ||
                            fileExt.equalsIgnoreCase(".png")) {
                        picFileInfos.add(fileInfo);
                    } else if (fileExt.equalsIgnoreCase(".mp4")) {
                        mp4FileInfos.add(fileInfo);
                    }
                }
            } catch (Exception e) {

            }
        });
        mp4FileInfos.addAll(directoryFileInfos);
        picFileInfos.addAll(directoryFileInfos);
        StaticDataCache.MP4_FILE_INFO_MAP.put(rootFullPath, mp4FileInfos);
        StaticDataCache.PIC_FILE_INFO_MAP.put(rootFullPath, picFileInfos);
    }

}
