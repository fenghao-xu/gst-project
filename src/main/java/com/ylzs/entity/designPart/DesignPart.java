package com.ylzs.entity.designPart;


import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylzs.core.model.SuperEntity;
import com.ylzs.entity.plm.DesignPartMasterDataBom;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * 设计部件
 * 
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-29 11:53:47
 */
@Data
public class DesignPart extends SuperEntity {
	private static final long serialVersionUID = 1L;

	private String designCode;//设计部件编码

	private String designName;//设计部件名称

	private String designImage;//设计部件图案 PI对应imageUrl

	private String clothingCategory;//服装品类 PI对应grandCategory

	private String partMiddleCode;//部件中类，关联部件中类表part_middle_code字段 PI对应midCategory

	private String partPosition;//部件位置 PI对应mtmPositionCode

	private String patternType;//图案类型 PI对应patternTechnology

	private String patternMode;//图案方式 PI对应embroideryStyle

	private String remark;//备注

	private String smallCategory;//部件小类

	//private String imageUrl;//线稿图

	private String styleType;//款式智库类型

	private String styleCode;//生产通知单款号

	private Float seamLine1;//止口1

	private Float seamLine2;//止口2

	private String stitchMidType;//明线中类

	private String stitchSmallType;//明线小类

	//private String updateDate;//更新时间

	private String imageUrlMtm;//设计图MTM

	private String imageDurlMtm;//部件位置图MTM

	//private String embroideryStyle;//绣花方式

	//private String mtmPositionCode;//mtm位置

	private Float warpOffset;//横偏移量(CM）

	private Float weftOffset;//竖偏移量（CM）

	private Float obliqueOffset;//斜偏移量（CM）

	private Boolean active;//部件是否可用

	private String applicablePosition;//缝边位置 部件位置1

	private Float stitchNumber;//缝边调整

	private String wStitch;//具体外观线迹

	private String gongYiExplain;//工艺说明

	private String patternTechnologyD;//图案工艺编码

	private String patternTechnology;//图案工艺

	private String patternMsg;//图案线色说明

	private Boolean affectCraft;//是否影响工艺

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JSONField(format="yyyy-MM-dd HH:mm:ss")
	private Date receiveTime;//接收时间

	private transient List<DesignPartMasterDataBom> bom;//GST 不在这里接EBOM



}
