package com.ylzs.entity.staticdata;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author xuwei
 * @create 2020-02-27 10:52
 * 工序等级
 */
@Data
public class CraftGrade extends SuperEntity {


    private static final long serialVersionUID = 5770594812069366677L;
    /**
     * 工厂编码
     */
    private String factoryCode;

    /**
     * * 工厂名称
     */
    private String factoryName;

    /**
     * 月工资(天)
     */
    private BigDecimal monthWage;

    /**
     * 月工作日(天)
     */
    private BigDecimal workingDayMonth;

    /**
     * 日工资(天)
     */
    private BigDecimal dayWage;

    /**
     * 日工作时(时)
     */
    private BigDecimal workingHourDay;

    /**
     * 小时工资(元/时)
     */
    private BigDecimal hourlyWage;

    /**
     * 分钟工资（元/分钟)
     */
    private BigDecimal minuteWage;

    /**
     * 备注
     */
    private String remark;

    private  List<CraftGradeEquipment> craftGradeEquipmentList;

}
