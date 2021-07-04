package com.ylzs.entity.plm;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: watermelon.xzx
 * @Description: PI接收款式下部件表
 * @Date: Created in 10:15 2020/3/12
 */
@Data
@TableName(value = "capp_pi_master_data_component")
public class PlmMasterDataComponent implements Serializable {
    private static final long serialVersionUID = -6790544725935383164L;
    private Integer id;
    private String styleCode;//款式编码
    private String componentCode;//部件编码
    private String componentName;//部件名称
    private Boolean isDefaultComponent;//是否默认部件
    private String positionName;//部件位置名称
    private String positionCode;//部件位置编码
    private String positionType;//部件位置类型
    private String parentComponentCode;//上级部件编码
    private String topPosition;//所属顶层部件
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date receiveTime;//接收时间


}
