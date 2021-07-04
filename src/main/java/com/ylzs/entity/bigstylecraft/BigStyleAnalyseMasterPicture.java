package com.ylzs.entity.bigstylecraft;

import lombok.Data;

/**
 * @author xuwei
 * @create 2020-03-24 15:25
 * 大货款式分析 款的图片
 */
@Data
public class BigStyleAnalyseMasterPicture {
    private Long id;
    private String picUrl;  //SKC款式图片（外网）
    private Long styleRandomCode;
    private String ctStyleCode;  //物料编码(款号)  用于级联
}
