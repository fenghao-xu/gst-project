package com.ylzs.entity.excel;

import lombok.Data;

/**
 * @className ExcelHead
 * @Description
 * @Author sky
 * @create 2020-02-27 11:13:25
 */
@Data
public class ExcelHead {

    private String excelName;             //Excel名
    private String entityName;            //实体类属性名
    private boolean required=false;      //值必填

}
