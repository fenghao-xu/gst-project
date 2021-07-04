package com.ylzs.common.util;

import com.google.common.base.CharMatcher;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.math.BigDecimal;

/**
 * @className StringUtils
 * @Description
 * @Author sky
 * @create 2020-02-27 11:43:55
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    /**
     * 列名转换成Java属性名
     */
    public static String columnToJava(String columnName) {
        return WordUtils.capitalizeFully(columnName, new char[]{'_'}).replace("_", "");
    }


    //首字母小写
    public static String lowerCaseCaptureName(String name) {

        char[] cs = name.toCharArray();
        cs[0] += 32;
        return String.valueOf(cs);

    }

    //首字母大写
    public static String uperCaseCaptureName(String name) {
        char[] cs = name.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);

    }

    public static Integer trimNull2Integer(String val) {
        return (null != val && !val.isEmpty() && !"null".equals(val)) ? Integer.parseInt(val) : null;
    }

    public static Float trimNull2Float(String val) {
        return (null != val && !val.isEmpty() && !"null".equals(val)) ? Float.parseFloat(val) : null;
    }

    public static String trimNull(String val) {
        return "null".equals(val)?null:val;
    }

    public static String replaceSpecial(String dataStr) {
        if (StringUtils.isNotEmpty(dataStr)) {
            dataStr = dataStr.replace("\\t", "");
            dataStr = dataStr.replace("\\r", "");
            //dataStr = dataStr.replace("\"", " ");
            dataStr = dataStr.replace("<", "");
            dataStr = dataStr.replace(">", "");
            dataStr = dataStr.replace("\\n", "");
            dataStr = dataStr.replace("\\u0000", "");
            dataStr = dataStr.replace("\\u0000d", "");
            dataStr = dataStr.replace("\\u0016", "");
        }
        return dataStr;
    }

    /**
     * 注：\n 回车(\u000a)
     * \t 水平制表符(\u0009)
     * \s 空格(\u0008)
     * \r 换行(\u000d)
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            // Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            //Matcher m = p.matcher(str);
            //dest = m.replaceAll("");
            dest = CharMatcher.anyOf("\r\n\t \u00A0　").trimFrom(str);
        }
        return dest;
    }

    public static Boolean String2Boolean(String val) {
        return (null != val && !val.isEmpty() && !"null".equals(val)) ? Boolean.parseBoolean(val) : null;
    }

    public static BigDecimal String2Decimal(String val) {
        return (null != val && !val.isEmpty() && !"null".equals(val)) ? (new BigDecimal(val)) : null;
    }

    public static String NotNullString(String val) {
        return (null != val && !val.isEmpty() && !"null".equals(val)) ? val : null;

    }

    //除去右侧不可见字符
    public static String RightTrimHide(String sourceText) {
        String str = sourceText;
        if (str == null) {
            return str;
        }

        int n = str.length();
        while (n > 0 && !CharUtils.isAsciiPrintable(str.charAt(n - 1))) {
            n--;
        }
        return str.substring(0, n);
    }
}
