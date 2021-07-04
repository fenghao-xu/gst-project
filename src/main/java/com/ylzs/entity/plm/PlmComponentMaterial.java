package com.ylzs.entity.plm;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Author: watermelon.xzx
 * @Description: PI接收部件下材料清单表
 * @Date: Created in 16:27 2020/3/13
 */
@Data
@TableName(value = "capp_pi_component_material")
public class PlmComponentMaterial {
    private Integer id;
    private String styleCode;//款式编码
    private String componentCode;//所属部件编码
    private String materialLargeType;//材料大类
    private String materialMidType;//材料中类
    private String defaultMaterial;//默认材料编码
    private String belongToComponentCode;//顶级部件编码
    private String isMainComponentOrMaterial;//是否主部件/材料
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date receiveTime;//接收时间
}
