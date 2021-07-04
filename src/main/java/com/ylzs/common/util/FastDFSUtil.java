package com.ylzs.common.util;

import com.alibaba.druid.util.StringUtils;
import com.ylzs.common.cache.StaticDataCache;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;

/**
 * @author xuwei
 * @create 2020-02-18 21:48
 */
public class FastDFSUtil {
    private static TrackerClient trackerClient = null;
    private static TrackerServer trackerServer = null;
    private static StorageServer storageServer = null;
    private static StorageClient1 client = null;
    private static final String FASTDFS_PRE_URL = "fastdfs_pre_url";
    private static final String CONF_NAME = "/fastdfs_client.conf";
    private static final Logger logger = LoggerFactory.getLogger(FastDFSUtil.class);

    static {
        try {
            //配置文件必须指定全路径
            String confName = FastDFSUtil.class.getResource(CONF_NAME).getPath();
            //当路径中包含中文的时候，必须进行utf-8转码
            confName = URLDecoder.decode(confName, "utf-8");
            ClientGlobal.init(confName);
            trackerClient = new TrackerClient();
            trackerServer = trackerClient.getConnection();
            storageServer = null;
            client = new StorageClient1(trackerServer, storageServer);
            logger.info("正常初始化完成");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传文件方法
     * <p>Title: uploadFile</p>
     * <p>Description: </p>
     *
     * @param fileName 指的是文件的全路径（包含文件名本身）
     * @param extName  文件扩展名，不包含（.）
     * @param metas    文件扩展信息
     * @return
     * @throws Exception
     */
    public static String uploadFile(String fileName, String extName, NameValuePair[] metas) throws Exception {
        String fileId = client.upload_file1(fileName, extName, metas);
        logger.info("----upload file successfully！！----fileId：" + fileId);
        return StaticDataCache.getInstance().getValue(FASTDFS_PRE_URL) + fileId;
    }

    /**
     * @param fileName 指的是文件的全路径（包含文件名本身）
     * @return
     * @throws Exception
     */
    public static String uploadFile(String fileName) throws Exception {
        return uploadFile(fileName, null, null);
    }

    public static byte[] downFile(String fileId) throws Exception {
        return client.download_file1(fileId);
    }

    public static String uploadFile(String fileName, String extName) throws Exception {
        return uploadFile(fileName, extName, null);
    }

    /**
     * 上传文件方法
     * <p>Title: uploadFile</p>
     * <p>Description: </p>
     *
     * @param fileContent 文件的内容，字节数组
     * @param extName     文件扩展名
     * @param metas       文件扩展信息
     * @return
     * @throws Exception
     */
    public static String uploadFile(byte[] fileContent, String extName, NameValuePair[] metas) throws Exception {

        String fileId = client.upload_file1(fileContent, extName, metas);
        return StaticDataCache.getInstance().getValue(FASTDFS_PRE_URL) + fileId;
    }

    public static String uploadFile(byte[] fileContent) throws Exception {
        return uploadFile(fileContent, null, null);
    }

    public static String uploadFile(byte[] fileContent, String extName) throws Exception {
        return uploadFile(fileContent, extName, null);
    }

    /**
     * 通过文件的url删除文件
     * picUrl 文件的url地址
     * 例如http://10.7.121.45/group1/M00/00/00/Cgd5LV5E_hKAATOCAA_4J2MSdsI892.jpg
     * 返回值 true 表示删除成功
     */
    public static boolean deleteFileByUrl(String picUrl) {
        if (StringUtils.isEmpty(picUrl)) {
            return false;
        }
        String baseUrl = StaticDataCache.getInstance().getValue(FASTDFS_PRE_URL);
        if (StringUtils.isEmpty(baseUrl)) {
            return false;
        }
        int length = baseUrl.length();
        try {
            logger.info("----ready to delete file ---picUrl：" + picUrl);
            String fileId = picUrl.substring(length);
            logger.info("----ready to delete file ---fileId：" + fileId);
            if (0 == client.delete_file1(fileId)) {
                logger.info("----delete file successfully！！----fileId：" + fileId);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
