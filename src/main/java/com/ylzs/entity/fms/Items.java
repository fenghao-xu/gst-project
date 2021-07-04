package com.ylzs.entity.fms;

import lombok.Data;

import java.io.Serializable;

/**
 * @className Items
 * @Description
 * @Author sky
 * @create 2020-04-24 09:22:54
 */
@Data
public class Items implements Serializable {

    private String  itemId;
    private String action;
    private Item item;

}
