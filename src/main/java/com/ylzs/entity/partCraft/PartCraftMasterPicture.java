package com.ylzs.entity.partCraft;


import com.ylzs.core.model.SuperEntity;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * 部件工艺主数据图片
 * 
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-29 16:39:26
 */
@Data
public class PartCraftMasterPicture extends SuperEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 图片地址
	 */
	private String pictureUrl;
	/**
	 * 描述信息
	 */
	private String remark;

	private Long partCraftMainRandomCode;

}
