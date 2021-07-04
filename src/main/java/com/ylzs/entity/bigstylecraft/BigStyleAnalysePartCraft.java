package com.ylzs.entity.bigstylecraft;

import com.ylzs.core.model.SuperEntity;
import com.ylzs.vo.partCraft.PartCraftMasterPictureVo;
import lombok.Data;

import java.util.List;

/**
 * @author xuwei
 * @create 2020-03-24 9:25
 * 大货款式工艺 的 部件工艺
 */
@Data
public class BigStyleAnalysePartCraft extends SuperEntity {

    /**
     * 款的随机码
     */
    private Long styleRandomCode;

    /**
     * 款的码
     */
    private String ctStyleCode;
    /**
     * 部件工艺编码
     */
    private String partCraftMainCode;
    /**
     * 部件工艺名称
     */
    private String partCraftMainName;
    /**
     * 结构部件
     */
    private String partCode;
    /**
     * 结构部件
     */
    private String partName;
    /**
     * 条格类型
     */
    private String patternSymmetry;

    /**
     * 条格类型
     */
    private String patternSymmetryName;


    /**
     * 部件下面的工序明细
     */
    private transient List<BigStyleAnalysePartCraftDetail> partCraftDetailList;

    /**
     * 部件下面的图片
     */
    private transient List<PartCraftMasterPictureVo> pictures;

    /**
     * 部件工艺描述
     */
    private String remark;

    /**
     * 部件工艺导入顺序
     */
    private Integer importOrder;
}
