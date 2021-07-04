package com.ylzs.vo.system;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 13:54 2020/3/7
 */
public class DataParent<T> {
    private String interfaceType;

    @JsonFormat(pattern = "yyyyMMddHHmmss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyyMMddHHmmss")
    @JSONField(format="yyyyMMddHHmmss")
    private Date synTime;

    private String count;

    private List<DataChild<T>> items;

    public String getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(String interfaceType) {
        this.interfaceType = interfaceType;
    }

    public Date getSynTime() {
        return synTime;
    }

    public void setSynTime(Date synTime) {
        this.synTime = synTime;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public List<DataChild<T>> getItems() {
        return items;
    }

    public void setItems(List<DataChild<T>> items) {
        this.items = items;
    }
}
