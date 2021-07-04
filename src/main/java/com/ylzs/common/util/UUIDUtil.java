package com.ylzs.common.util;

import java.util.UUID;

/**
 * @author xuwei
 * @create 2020-02-25 14:46
 */
public class UUIDUtil {
    /**
     * 生成不带横杠的32位UUID
     */
    public static String uuid32() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
