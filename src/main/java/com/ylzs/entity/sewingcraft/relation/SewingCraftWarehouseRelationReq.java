package com.ylzs.entity.sewingcraft.relation;

import lombok.Data;

import java.io.Serializable;

/**
 * @author weikang
 * @Description
 * @Date 2021/1/22 10:35
 */
@Data
public class SewingCraftWarehouseRelationReq implements Serializable {

    private static final long serialVersionUID = 8780151197455181510L;

    private String userName;

    private String craftRandomCode;

    private String craftRandomCodes;
}
