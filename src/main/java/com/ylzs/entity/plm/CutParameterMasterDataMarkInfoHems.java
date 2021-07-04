package com.ylzs.entity.plm;

import lombok.Data;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 17:32 2020/4/30
 */
@Data
public class CutParameterMasterDataMarkInfoHems {
    private Integer id;
    private String orderId;
    private String orderLineId;
    private String hemsCode;
    private String value;
}
