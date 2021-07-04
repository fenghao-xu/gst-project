package com.ylzs.entity.plm;

import lombok.Data;

import java.io.Serializable;

/**
* @Author:tt
* @Description： 大货主数据 -WXProcedures 子表
* @Date: Created in 2020/3/14
*/
@Data
public class BigStyleMasterDataWXProcedures implements Serializable {
    private static final long serialVersionUID = 5925398748011738623L;
    private int id;
    private String procedureID    ;  //外协数据行id
    private String procedureCode    ;  //工序code
    private String procedureType    ;  //外协类型
    private String procedureName    ;  //工序名称
    private Float procedurePrice    ;  //工序价格
    private String wXSupplierName    ;  //外协供应商名称
    private String wXSupplierAddress    ;  //外协供应商地址
    private String wXSupplierContacts    ;  //外协供应商联系人
    private String wXSupplierTelephone    ;  //外协供应商联系电话
    private String ctStyleCode    ;  //物料编码(款号)

}
