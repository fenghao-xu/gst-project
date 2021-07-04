package com.ylzs.vo.bigstylereport;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylzs.entity.sewingcraft.SewingCraftAction;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author xuwei
 * @create 2020-05-29 9:19
 */
@Data
public class BigStyleVO {
    private Long styleRandomCode;
    private Integer status;
    private String type;
    private Long orderRandomCode;
    private String orderNo;
    private String customOrderNo;
    private String orderLineId;
    /**
     * 适宜性编码
     */
    private String adaptCode;

}
