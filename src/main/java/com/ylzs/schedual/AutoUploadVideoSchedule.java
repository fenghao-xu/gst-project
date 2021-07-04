package com.ylzs.schedual;


import com.ylzs.common.cache.StaticDataCache;
import com.ylzs.common.util.Const;
import com.ylzs.common.util.StringUtils;
import com.ylzs.entity.craftstd.CraftStd;
import com.ylzs.service.craftstd.ICraftStdService;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.ylzs.common.util.FastDFSUtil.uploadFile;
import static com.ylzs.common.util.StringUtils.RightTrimHide;
import static org.apache.poi.util.IOUtils.toByteArray;

/**
 * 定时自动上传视频
 */
@Component
public class AutoUploadVideoSchedule {
    private Logger logger = LoggerFactory.getLogger(AutoUploadVideoSchedule.class);

    @Value("${spring.profiles.active}")
    private String env;

    @Resource
    ICraftStdService craftStdService;

    @Async
    @Scheduled(cron = "0 0 1 * * ?") //每天凌晨1点执行
    //@Scheduled(fixedDelay = 3600000)
    public void Execute1() {
        checkAndUploadVideo();
    }

    @Async
    @Scheduled(cron = "0 30 12 * * ?") //每天12:30执行
    public void Execute2() {
        checkAndUploadVideo();
    }

    @Async
    @Scheduled(cron = "0 0 3 * * ?") //每天凌晨3点执行
    public void Execute3() {
        checkAndUploadVideo();
    }

    @Async
    @Scheduled(cron = "0 0 5 * * ?") //每天凌晨5点执行
    public void Execute5() {
        checkAndUploadVideo();
    }



    private void checkAndUploadVideo()  {
        //测试环境不启动自动上传视频和图片
        if(!env.equalsIgnoreCase("prod")) {
            return;
        }

        logger.info("---------开始定时自动上传视频-----------------");

        //增加超时设置30分钟
        jcifs.Config.setProperty("jcifs.smb.client.responseTimeout", "1800000");
        jcifs.Config.setProperty("jcifs.smb.client.soTimeout", "1800000");

        int count = craftStdService.getNoVideoCraftStdCount();
        logger.info(String.valueOf(count) + "个文件没有视频");
        if (count == 0) {
            return;
        }

        String basePath = StaticDataCache.getInstance().getValue(Const.SHARE_BASE_PATH, ""); //"10.7.200.135/share/智能工艺技术部/已经剪辑完成视频文件/04 裤子：已剪辑完整视频-------/后幅/";
        //String basePath = "10.7.200.135/share/智能工艺技术部/已经剪辑完成视频文件/04 裤子：已剪辑完整视频-------/后幅/";
        String remotePhotoUrl = "smb://" + basePath; // 10.7.200.135\share
        List<ICraftStdService.ShareFileInfo> shareFileInfos = new ArrayList<ICraftStdService.ShareFileInfo>();
        String shareAcc = StaticDataCache.getInstance().getValue(Const.SHARE_ACC, ""); //"naersi\\capp";//
        String sharePwd = StaticDataCache.getInstance().getValue(Const.SHARE_PWD, ""); //"yingjiaznb";//
        try {
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("", shareAcc, sharePwd);
            SmbFile remoteFile = new SmbFile(remotePhotoUrl, auth);
            enumVideoFile(remoteFile);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("AutoUploadVideoSchedule Execute error:" + e.getMessage());

        }
    }

    public void test() {
        //测试环境不启动自动上传视频和图片
        if(!env.equalsIgnoreCase("prod")) {
            return;
        }

        String basePath = "10.7.200.135/share/智能工艺技术部/已经剪辑完成视频文件/GST/";
        String remotePhotoUrl = "smb://" + basePath; // 10.7.200.135\share
        List<ICraftStdService.ShareFileInfo> shareFileInfos = new ArrayList<ICraftStdService.ShareFileInfo>();
        String shareAcc = "naersi\\capp";
        String sharePwd = "yingjiaznb";
        try {
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("", shareAcc, sharePwd);
            SmbFile remoteFile = new SmbFile(remotePhotoUrl, auth);
            enumVideoFile(remoteFile);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("AutoUploadVideoSchedule Execute error:" + e.getMessage());

        }
    }

    public List<String> uploadBigStyleImage(String styleCode) {
        String basePath = "smb://10.7.200.135/share/智能工艺技术部/已经剪辑完成视频文件/GST_BigStyle/";
        final String shareAcc = StaticDataCache.getInstance().getValue(Const.SHARE_ACC, ""); //"naersi\\capp";//
        final String sharePwd = StaticDataCache.getInstance().getValue(Const.SHARE_PWD, ""); //"yingjiaznb";//


        ArrayList<String> files = new ArrayList<>();
        files.add(basePath + styleCode + "_1.jpeg");
        files.add(basePath + styleCode + "_1.png");
        files.add(basePath + styleCode + "_2.jpeg");
        files.add(basePath + styleCode + "_2.png");

        ArrayList<String> urls = new ArrayList<>();
        List<String> urlList = Collections.synchronizedList(urls);

        files.parallelStream().forEach(x->{
            try {
                NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("", shareAcc, sharePwd);
                SmbFile remoteFile = new SmbFile(x, auth);
                if (remoteFile.exists() || remoteFile.isFile()) {
                    byte[] buffer = toByteArray(remoteFile.getInputStream());
                    int k = x.lastIndexOf(".");
                    String fileExt = x.substring(k + 1);
                    String url = uploadFile(buffer, fileExt);
                    if(StringUtils.isNotBlank(url)) {
                        if(!url.endsWith("/null")) {
                            url = RightTrimHide(url);
                            urlList.add(url);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("uploadBigStyleImage error:" + e.getMessage());
            }
        });
        return urls;
    }



    private void uploadVideo(SmbFile remoteFile, String videoCode, String videoName, String fileExt) {
        if(videoCode != null) {
            videoCode = videoCode.trim();
        }
        if(videoName != null) {
            videoName = videoName.trim();
        }
        if(fileExt != null) {
            fileExt = fileExt.trim();
        }
        if(StringUtils.isBlank(videoCode) && StringUtils.isBlank(videoName)) {
            return;
        }


        try {
            List<CraftStd> craftStds = craftStdService.getNoVideoCraftStd(videoCode, videoName);
            if (craftStds == null || craftStds.isEmpty()) {
                return;
            }
            logger.info("视频上传中：" + videoCode + " " + videoName);
            byte[] buffer = toByteArray(remoteFile.getInputStream());
            String url = uploadFile(buffer, fileExt);

            if(StringUtils.isNotBlank(url)) {
                if(url.endsWith("/null")) {
                    throw new Exception("文件上传失败，返回null 原文件名：" + remoteFile.getName());
                }
                url = RightTrimHide(url);
            }

            for (CraftStd craftStd : craftStds) {
                craftStdService.addCraftStdVideo(craftStd.getId(), url, remoteFile.getName());
            }
            logger.info("视频上传完成：" + videoCode + " " + videoName);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("uploadVideo error:" + e.getMessage());
        }
    }

    private void uploadImage(SmbFile remoteFile, String code, String fileExt) {
        if(code != null) {
            code = code.trim();
        }
        if(fileExt != null) {
            fileExt = fileExt.trim();
        }
        if(StringUtils.isBlank(code)) {
            return;
        }

        try {
            List<CraftStd> craftStds = craftStdService.getNoHandleImageCraftStd(code);
            if (craftStds == null || craftStds.isEmpty()) {
                return;
            }
            logger.info("图片上传中：" + code);
            byte[] buffer = toByteArray(remoteFile.getInputStream());
            String url = uploadFile(buffer, fileExt);
            if(StringUtils.isNotBlank(url)) {
                if(url.endsWith("/null")) {
                    throw new Exception("文件上传失败，返回null 原文件名：" + remoteFile.getName());
                }
                url = RightTrimHide(url);
            }
            for (CraftStd craftStd : craftStds) {
                craftStdService.addCraftStdHandleImage(craftStd.getId(), url, remoteFile.getName());
            }
            logger.info("图片上传完成：" + code);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("uploadImage error:" + e.getMessage());
        }

    }


    private void enumVideoFile(SmbFile rootfile) throws Exception {
        if (!rootfile.exists() || rootfile.isFile()) {//不存在或者为空则退出循环
            return;
        }

        SmbFile files[] = rootfile.listFiles();
        if (null == files || files.length == 0) {
            return;
        }

        //开启多线程并行处理
        Arrays.stream(files).forEach(x -> { //.parallel()
            try {
                if (x.isFile()) {
                    String fileName = x.getName();
                    String fileExt = "";
                    int k = fileName.lastIndexOf(".");
                    if (k >= 0) {
                        fileExt = fileName.substring(k + 1);
                    }
                    if(fileExt != null) {
                        fileExt = fileExt.trim();
                    }

                    if (fileExt.equalsIgnoreCase("mp4")
                            || fileExt.equalsIgnoreCase("avi")
                            || fileExt.equalsIgnoreCase("wmv")
                            || fileExt.equalsIgnoreCase("rmvb")) {
                        int pos = fileName.indexOf("#");
                        if (pos >= 0) {
                            String videoCode = fileName.substring(0, pos);
                            int rightPos = fileName.lastIndexOf(".");
                            String videoName = fileName.substring(pos + 1, rightPos);
                            uploadVideo(x, videoCode, videoName, fileExt);
                        } else {
                            int rightPos = fileName.lastIndexOf(".");
                            String videoCode = fileName.substring(0, rightPos);
                            uploadVideo(x, videoCode, "", fileExt);
                        }
                    } else
                    if(fileExt.equalsIgnoreCase("jpeg")
                        || fileExt.equalsIgnoreCase("jpg")) {
                        int pos = fileName.indexOf("-");
                        if (pos >= 0) {
                            int rightPos = fileName.lastIndexOf("-");
                            String code = fileName.substring(0, rightPos);
                            uploadImage(x, code, fileExt);
                        }
                    }

                } else if (x.isDirectory()) {
                    enumVideoFile(x);//递归查找
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("AutoUploadVideoSchedule enumVideoFile error:" + e.getMessage());
            }
        });
    }


}
