package com.ylzs.entity.materialCraft.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author weikang
 * @Description 材料工艺清单返回数据
 * @Date 2020/3/9
 */
@Data
public class QueryMaterialCraftDataResp implements Serializable {
    private static final long serialVersionUID = -6631161692967361942L;

    private Long id;
    /**
     * 随机码
     */
    private Long randomCode;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 材料工艺编码
     */
    private String materialCraftCode;

    /**
     * 材料工艺名称
     */
    private String materialCraftName;

    /**
     * 材料类型名称
     */
    private String materialCraftKindName;

    /**
     * 材料工艺描述
     */
    private String materialCraftDesc;

    /**
     * 状态1000.草稿 1010.已审核 1020.已发布(生效) 1030.失效 1040.未生效 1090 删除
     */
    private Integer status;

    /**
     * 材料工艺版本号
     */
    private String materialVersion;

    /**
     * 版本说明
     */
    private String materialVersionDesc;

    /**
     * 材料工艺对应属性名称
     */
    private String fabricPropertyNames;

}
