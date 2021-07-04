package com.ylzs.entity.fms;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @className OrderItem
 * @Description  订单行项目
 * @Author sky
 * @create 2020-04-24 09:24:21
 */
@Data
public class OrderItem implements Serializable {

    private static final long serialVersionUID = -773379357165764622L;
    /**尺码代码*/
    private Integer atwrt;
    /**数量**/
    private BigDecimal gsmng;
    /**订单行号*/
    private String zmtmitm;
}
