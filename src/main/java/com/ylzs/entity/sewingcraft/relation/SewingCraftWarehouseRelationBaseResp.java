package com.ylzs.entity.sewingcraft.relation;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author weikang
 * @Description 用于展示工序关联
 * @Date 2021/1/19 16:57
 */
@Data
public class SewingCraftWarehouseRelationBaseResp implements Serializable {

    private static final long serialVersionUID = 2975878409163987745L;

    private SewingCraftWarehouseRelationResp mainRelationResp;

    private List<SewingCraftWarehouseRelationResp> relationCraftRespList;

    /**
     * 工序关联sewing_craft_warehouse_relation随机码
     */
    private Long randomCode;

    /**
     * 创建人
     */
    private String createUser;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 维护人
     */
    private String updateUser;
    /**
     * 维护时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
