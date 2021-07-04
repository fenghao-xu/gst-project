package com.ylzs.common.util;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * @className ListUtils
 * @Description
 * @Author sky
 * @create 2020-05-13 14:45:32
 */
public class ListUtils {

    private ListUtils(){}

    /**
     * 从List<A> copy到List<B>
     *
     * @param list  List<B>
     * @param clazz B
     * @return List<B>
     */
    public static <T> List<T> copyList(List<?> list, Class<T> clazz) {
        String oldOb = JSON.toJSONString(list);
        return JSON.parseArray(oldOb, clazz);
    }
}
