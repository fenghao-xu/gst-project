package com.ylzs.vo.system;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 13:55 2020/3/7
 */
public class DataChild<T> {
    private String itemId;

    private String action;

    private T item;

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
