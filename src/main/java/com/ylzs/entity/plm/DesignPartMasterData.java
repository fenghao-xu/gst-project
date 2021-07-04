package com.ylzs.entity.plm;

import com.ylzs.entity.designPart.DesignPart;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
* @Author:tt
* @Description：PI传入部件主数据
* @Date: Created in 2020/3/11
*/
@Data
public class DesignPartMasterData extends DesignPart implements Serializable {

    private static final long serialVersionUID = -8531791058175416114L;


    private String componentName;//部件名称

    private String componentCode;//部件编码

    private String grandCategory;//部件大类

    private String midCategory;//部件中类

    private String smallCategory;//部件小类

    private String imageUrl;//线稿图

    private String styleType;//款式智库类型

    private String styleCode;//生产通知单款号

    private Float seamLine1;//止口1

    private Float seamLine2;//止口2

    private String stitchMidType;//明线中类

    private String stitchSmallType;//明线小类

    private String updateDate;//更新时间

    private String imageUrlMtm;//设计图MTM

    private String imageDurlMtm;//部件位置图MTM

    private String embroideryStyle;//绣花方式

    private String mtmPositionCode;//mtm位置

    private Float warpOffset;//横偏移量(CM）

    private Float weftOffset;//竖偏移量（CM）

    private Float obliqueOffset;//斜偏移量（CM）

    private Boolean active;//部件是否可用

    private String applicablePosition;//线迹适用位置

    private Float stitchNumber;//缝边调整

    private String wStitch;//具体外观线迹

    private String gongYiExplain;//工艺说明

    private String patternTechnologyD;//图案工艺编码

    private String patternTechnology;//图案工艺

    private Boolean affectCraft;//

    private String patternMsg;//

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date receiveTime;//接收时间

    private List<DesignPartMasterDataBom> bom;//用于级联

}
