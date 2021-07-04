package com.ylzs.vo.interfaceOutput;

import lombok.Data;

import java.io.Serializable;

/**
 * 新工序输出
 */
@Data
public class NewCraftItemOuput implements Serializable {
    private static final long serialVersionUID = -1595264951206704353L;
    /**
     * 工序编码
     */
    private String gxdm;
    /**
     * 工序名称
     */
    private String gxmc;
    /**
     * 机器类型
     */
    private String jqdm;
    /**
     * 机器名称
     */
    private String jqmc;
    /**
     * 预计处理时间
     */
    private String smv;

    /**
     * 工价
     */
    private String dj;

    /**
     * 生产部件
     */
    private String scbj;
    /**
     * 生产部件名称
     */
    private String scbjmc;

    private String gybj;
    private String gybjmc;


    /**
     * 工段代码
     */
    private String bmdm;

    /**
     * 工段名称
     */
    private String bmmc;

    private String gjdm;
    private String gjmc;
    /**
     * 是否外协
     */
    private String wxbz;

    /**
     * 品质说明
     */
    private String pzsm;

    /**
     * 工艺要求
     */
    private String zgsm;

    /**
     * 图片url
     */
    private String fjPath;
    private String videoURL;
    private String sgzdm;
    private String sgzmc;
    private String cutSign;

    private String sjgxdm;
    private String sjgxmc;

    /**
     * 难度等级
     */
    private String hardLevel;

    /**
     * 工艺品类代码
     */
    private String craftCategoryCode;
    /**
     * 工艺品类名称
     */
    private String craftCategoryName;

    /**
     * 做工类别
     */
    private String zglb;
    /**
     * 做工名称
     */
    private String zgmc;

}
