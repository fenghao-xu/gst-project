package com.ylzs.common.sort;

import com.ylzs.entity.custom.CustomStyleCraftCourse;

import java.util.Comparator;

/**
 * @author xuwei
 * @create 2020-06-27 11:20
 */
public class CustomStyleCraftCourseCompare implements Comparator<CustomStyleCraftCourse> {
    @Override
    public int compare(CustomStyleCraftCourse v1, CustomStyleCraftCourse v2) {
        if (null == v1 || v2 == null || v1.getCreateTime() == null || v2.getCreateTime() == null) {
            return 0;
        }
        return v2.getCreateTime().compareTo(v1.getCreateTime());
    }
}
