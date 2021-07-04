package com.ylzs.entity.plm;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @className PiCutParameterMarkerinfoHems
 * @Description
 * @Author sky
 * @create 2020-03-20 10:00:43
 */
@Data
@TableName(value = "capp_pi_cut_parameter_markerinfo_hems")
public class PiCutParameterMarkerinfoHems {

    /**id*/
    private Integer id;
    /**订单号*/
    private String orderId;
    /**订单行id*/
    private String orderLineId;
    /**缝边位置*/
    private String hemsCode;
    /**缝边位置长度**/
    private BigDecimal value;
}
