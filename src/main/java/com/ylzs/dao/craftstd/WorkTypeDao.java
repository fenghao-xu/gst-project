package com.ylzs.dao.craftstd;

import com.ylzs.entity.craftstd.WorkType;
import org.apache.ibatis.annotations.MapKey;

import java.util.List;
import java.util.Map;

/**
 * @author xuwei
 * @create 2020-03-16 17:39
 */
public interface WorkTypeDao {
    /**
     * 获取所有的工种，并且每个工种下面的做工类型也取出来
     */
    public List<WorkType> getWorkTypeAndMakeType();

    public List<WorkType> getSewingWorkType();

    @MapKey("workTypeCode")
    Map<String, WorkType> getWorkTypeMap();
}
