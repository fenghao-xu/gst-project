package com.ylzs.dao.timeparam;

import com.ylzs.entity.timeparam.MotionCodeConfig;
import com.ylzs.vo.timeparam.MotionTypeVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xuwei
 * @create 2020-03-04 18:05
 */
public interface MotionCodeConfigDao {
    /**
     * 获取所有的动作定义代码
     */
    public List<MotionCodeConfig> getAllMotionConfigs(@Param("motionType") String motionType);

    /**
     * 根据编码查询动作
     */
    public List<MotionCodeConfig> getMotionByCode(@Param("motionCode") String motionCode,@Param("motionType") String motionType);

    /**
     * 获取动作类型
     */
    public List<MotionTypeVo>getMotionType();

}
