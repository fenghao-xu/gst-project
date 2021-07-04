package com.ylzs.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.ylzs.common.util.DateUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @className MetaHandler
 * @Description mybatis 自动处理日期字段
 * @Author sky
 * @create 2020-03-02 17:27:20
 */
@Component
public class MetaHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        // 看实体类中是否有这个属性，有的话就执行。没有就不执行
        setFieldValByName("createTime", new Date(), metaObject);
        setFieldValByName("updateTime", new Date(), metaObject);
        setFieldValByName("version", DateUtils.formatDate(DateUtils.yyyyMMdd_HHmmssSSS), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 看实体类中是否有这个属性，有的话就执行。没有就不执行
            setFieldValByName("updateTime",new Date(), metaObject);

    }
}
