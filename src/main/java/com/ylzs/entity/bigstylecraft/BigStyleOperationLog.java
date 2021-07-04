package com.ylzs.entity.bigstylecraft;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author xuwei
 * @create 2020-08-05 8:37
 */
@Data
public class BigStyleOperationLog {
    private Long id;
    private String updateType;
    private String receiveData;
    private String code;
    private String userCode;
    @TableField(fill = FieldFill.INSERT, value = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
