package com.ylzs.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.ylzs.core.model.SuperEntityVo;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

/**
 * @className PartPositionVo
 * @Description 部件位置vo
 * @Author sky
 * @create 2020-03-04 16:14:19
 */
@Data
public class PartPositionVo extends SuperEntityVo {


    /**
     * 部件类型
         *  sew缝边 embroider绣花 decoration装饰
     */
    private String partType;
    /**
     * 部件类型名称
     */
    private String partTypeName;
    /**
     * 部件位置编码
     */
    private String partPositionCode;
    /**
     * 部件位置名称
     */
    private String partPositionName;
    /**
     * 款式类型---服装品类
     */
    private String clothingCategoryCode;
    /**
     * 服装品类中文名
     */
    private String clothingCategoryName;

}
