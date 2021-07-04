package com.ylzs.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @className DateUtils
 * @Description
 * @Author sky
 * @create 2020-02-27 11:46:30
 */
public class DateUtils {

    public static final String yyyyMMdd_HHmmssSSS = "yyyyMMdd:HH:mm:ss:sss";


    public static String formatDate(Date date,String forDate){
        SimpleDateFormat sdf = new SimpleDateFormat(forDate);
        return  sdf.format(date);
    }
    public static String formatDate(String forDate){
        SimpleDateFormat sdf = new SimpleDateFormat(forDate);
        return  sdf.format(new Date());
    }

    /**
     * 日期比较 两个日期转换成 yyyy-MM-dd 进行对比 startDate 和endDate 为空时默认取系统当前日期
     * 输入时间和当前时间一样； 返回值0
     * 输入时间大于当前时间；  返回值1
     * 输入时间小于当前时间； 返回值-1
     * @param startDate
     * @param endDate
     * @return
     * @throws ParseException
     */
    public static Integer compareTo(Date startDate,Date endDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(startDate == null){
            startDate = new Date();
        }
        if(endDate==null){
            endDate = new Date();
        }

        startDate = sdf.parse(sdf.format(startDate));
        endDate = sdf.parse(sdf.format(endDate));
        return startDate.compareTo(endDate);
    }
}
