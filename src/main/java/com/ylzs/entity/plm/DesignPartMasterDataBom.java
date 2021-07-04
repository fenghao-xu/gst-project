package com.ylzs.entity.plm;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
* @Author:tt
* @Description：PI传入部件主数据 子表实体
* @Date: Created in 2020/3/11
*/
@Data
public class DesignPartMasterDataBom implements Serializable {

    private static final long serialVersionUID = 8319957613129219500L;

    private int id;//主键

    private String designCode;//bom用于级联父表bom字段

    private String componentType;//位置

    private String isComponent;//是否部件

    private String componentCode;//默认部件编号

    private String optionalComponentsCode;//可选部件编号

    private String paternCode;//裁片编号

    private String embroideryPosition;//喜好位置

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date receiveTime;//接收时间

}
