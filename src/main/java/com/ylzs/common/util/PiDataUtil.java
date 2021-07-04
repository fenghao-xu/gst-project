package com.ylzs.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ylzs.vo.system.DataChild;
import com.ylzs.vo.system.DataParent;
import org.apache.poi.ss.formula.functions.T;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 14:11 2020/3/7
 */
public class PiDataUtil<T>{
    public List<T> getData(DataParent<T> parent, Class classzz) {
        List<DataChild<T>> children = parent.getItems();
        List<T> result = new ArrayList<T>(0);
        if (null != parent && children != null && children.size() > 0) {
            for (DataChild<T> child : children) {
                String itemStr = JSON.toJSONString(child.getItem());
                T data1 = (T) JSONObject.parseObject(itemStr, classzz);
                result.add(data1);
            }
        }
        return result;
    }

    public List<DataChild<T>> getChildData(DataParent<T> parent, Class classzz) {
        List<DataChild<T>> children = parent.getItems();
        if (null != parent && children != null && children.size() > 0) {
            for (DataChild<T> child : children) {
                T data1 = (T) JSONObject.parseObject(JSON.toJSONString(child.getItem()), classzz);
                child.setItem(data1);
            }
        }
        return children;
    }

    public List<T> getDataByAction(DataParent<T> parent, Class classzz, String action) {
        List<DataChild<T>> children = parent.getItems();
        List<T> result = new ArrayList<T>(0);
        if (null != parent && children != null && children.size() > 0) {
            for (DataChild<T> child : children) {
                if (action != null && !"".equals(action)) {
                    if (action.equals(child.getAction())) {
                        T data1 = (T) JSONObject.parseObject(JSON.toJSONString(child.getItem()), classzz);
                        result.add(data1);
                    }
                } else {
                    // 没有取所有
                    T data1 = (T) JSONObject.parseObject(JSON.toJSONString(child.getItem()), classzz);
                    result.add(data1);
                }
            }
        }
        return result;
    }
}
