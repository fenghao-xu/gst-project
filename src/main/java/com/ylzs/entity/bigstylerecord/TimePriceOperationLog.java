package com.ylzs.entity.bigstylerecord;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author xuwei
 * @create 2020-09-16 11:12
 */
@Data
public class TimePriceOperationLog {
    /**
     * 主键自增
     */
    @Id
    @Field(type = FieldType.Long)
    @TableId
    private Long id;
    /**
     * 维护人
     */
    private String updateUser;

    /**
     * 编码
     */
    private String code;

    private BigDecimal standardTime;//标准时间

    private BigDecimal standardPrice;//标准单价

    /**
     * 维护人
     */
    private String updateUserName;

    /**
     * 业务类型  10(款式工艺），20（工单工艺）
     */
    private Integer businessType;
    /**
     * 维护时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE,value = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
