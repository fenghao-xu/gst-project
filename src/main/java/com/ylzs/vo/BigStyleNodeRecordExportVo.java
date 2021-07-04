package com.ylzs.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class BigStyleNodeRecordExportVo implements Serializable {
    private static final long serialVersionUID = -5498758456774859301L;

    /**
     * 款式编码
     */

    private String styleCode;
    /**
     * 服装品类名称
     */

    private String clothesCategoryName;

    private String styleDesc;
    /**
     * 样衣第一次接收时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")

    private Date sampleReceiveTime1;

    /**
     * 预估缝制工时（小时）
     */

    private BigDecimal estimateSewingTime;
    /**
     * 发送FMS时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")

    private Date sendFmsTime;
    /**
     * 样衣第二次接收时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")

    private Date sampleReceiveTime2;

    /**
     * 生产组别名称
     */

    private String productGroupName;

    /**
     * 预排产开始日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date scheduleTime;

    /**
     * 预排产交货日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deliveryTime;

    /**
     * 分配人员名称
     */

    private String assignToUserName;

    /**
     * 分科开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date branchBeginTime;

    /**
     * 分科结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date branchEndTime;

    /**
     * 分科花费（小时）
     */

    private String branchSpend;

    /**
     * 样衣停留时间
     */
    private String sampleStaySpend;
    /**
     * 备注
     */

    private String remark;

    /**
     * 系统接收时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")

    private Date receiveTime;

    /**
     * 维护人
     */
    private String updateUser;
    /**
     * 维护时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
