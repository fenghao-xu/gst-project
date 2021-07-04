package com.ylzs.common.util;

import org.springframework.util.DigestUtils;

/**
 * @author xuwei
 * @create 2019-08-16 16:29
 */
public class MD5Util {
    public static String getMD5(String str) {
        return  DigestUtils.md5DigestAsHex(str.getBytes());
    }

}
