package com.ylzs.vo.interfaceOutput;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 工段工时
 */
@Data
public class SectionSMV implements Serializable {
    private static final long serialVersionUID = -352233150891989977L;
    /**
     * 工厂
     */
    private String site;
    /**
     * 订单编号（带行号)
     */
    private String ddbh;

    /**
     * 序号
     */
    private String xh;
    /**
     * 款式编号
     */
    private String ksbh;
    /**
     * 款式描述
     */
    private String ksms;

    /**
     * 发送日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date fsrq;

    /**
     * 1 准确工时 2 预估工时
     */
    private int type;

    /**
     * 工单号
     */
    private String orderId;




    List<SectionSMVDetail> details;
}
