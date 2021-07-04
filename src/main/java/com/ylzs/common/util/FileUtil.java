package com.ylzs.common.util;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;


/**
 * 说明：文件上传工具类
 *
 * @author lyq
 * 2019-10-08 15:36
 */
public class FileUtil {
    private static int iCount;

    public static void uploadFile(byte[] file, String filePath, String fileName) throws Exception {
        File targetFile = new File(filePath);
        if(!targetFile.exists()) {
            targetFile.mkdir();
        }
        FileOutputStream out = new FileOutputStream(filePath + fileName);
        out.write(file);
        out.flush();
        out.close();
    }


}
