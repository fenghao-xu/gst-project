package com.ylzs.entity.plm;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 17:17 2020/3/18
 */
@Data
public class PICustomOrder {
    private Integer id;
    private String orderId;//订单号
    private String orderLineId;//订单行号
    private String orderType;//订单类型
    private String styleCode;//智库类型
    private String custStyleCode;//定制款款号
    private String custStyleName;//定制款款名
    private String quantity;//订单数量
    private String customerName;//顾客姓名
    private String salesOutletsName;//店名
    private String employeePhone;//导购手机
    private String employeeName;//导购名称
    private String otherRequest;//订单备注
    private String fabricDirection;//布面倒顺
    private String dischargeMode;//面料排料方式
    private String mainMaterialCode;//主面料编码
    private String patternSymmetry;//条纹格栅
    private List<PICustomOrderPart> units;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date receiveTime;//接收时间
    private String styleSKCCode;



}
