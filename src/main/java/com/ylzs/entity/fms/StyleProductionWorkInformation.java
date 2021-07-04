package com.ylzs.entity.fms;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;

/**
 * @className StyleProductionWorkInformation
 * @Description 款式生产工单请求信息实体类
 * @Author sky
 * @create 2020-04-24 10:13:44
 */
@Data
public class StyleProductionWorkInformation extends SuperEntity {
    private static final long serialVersionUID = -7623182326889573344L;
    private String requestParam;
}
