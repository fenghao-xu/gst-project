package com.ylzs.entity.plm;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 17:24 2020/3/18
 */
@Data
public class PICustomOrderPart {
    private Integer id;
    private String orderId;//订单号
    private String OrderLineId;//订单行号
    private Long randomCode;//随机码
    private String unitCode;//部件编码
    private String unitPositionCode;//部件位置（来源于ums下的 positionCode、mtmPositionCode）
    private List<PICustomOrderPartMaterial> ums;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date receiveTime;//接收时间
}
