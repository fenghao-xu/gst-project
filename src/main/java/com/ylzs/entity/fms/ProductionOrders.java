package com.ylzs.entity.fms;

import lombok.Data;

import java.util.List;

/**
 * @className ProductionOrders
 * @Description
 * @Author sky
 * @create 2020-04-24 09:22:28
 */
@Data
public class ProductionOrders {

    private String interfaceType;
    private String synTime;
    private String count;
    private List<Items> items;
}
