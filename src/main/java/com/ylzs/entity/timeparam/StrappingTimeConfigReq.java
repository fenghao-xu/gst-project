package com.ylzs.entity.timeparam;

import lombok.Data;

import java.util.Date;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 15:50 2020/5/13
 */
@Data
public class StrappingTimeConfigReq {
    private String strappingCode;
    private String strappingName;
    private Integer time;
    private String remark;
    private String status;
    private String updateUser;
    private Date updateTime;
}
