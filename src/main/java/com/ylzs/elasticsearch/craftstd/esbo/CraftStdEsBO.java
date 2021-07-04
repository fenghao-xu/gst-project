package com.ylzs.elasticsearch.craftstd.esbo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author xuwei
 * @create 2020-03-03 10:04
 */
@Document(indexName = "craftstd", type = "craftstd")
public class CraftStdEsBO {
    @Id
    private Long id;
    /**
     * 工艺标准代码
     */
    @Field(type = FieldType.Text)
    private String craftStdCode;
    /**
     * 工艺标准名称
     */
    @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer = "ik_max_word")
    private String craftStdName;

    /**
     * 品质要求
     */
    @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer = "ik_max_word")
    private String requireQuality;

    /**
     * 做工说明
     */
    @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer = "ik_max_word")
    private String makeDesc;

    /**
     * 线稿图的图片
     */
    @Field(type = FieldType.Text,index = false)
    private String pictureUrl;
    /**
     * 视频URL
     */
    @Field(type = FieldType.Text,index = false)
    private String vedioUrl;
    /**
     * 机器名称
     */
    @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer = "ik_max_word")
    private String machineName;

    /**
     * 辅助工具名称
     */
    @Field(type = FieldType.Text)
    private String helpToolCode;
    /**
     * 工艺品类编码
     */
    @Field(type = FieldType.Text)
    private String craftCategoryCode;
    /**
     * 工艺品类名称
     */
    @Field(type = FieldType.Text)
    private String craftCategoryName;
    /**
     * 工艺部件编码
     */
    @Field(type = FieldType.Text)
    private String craftPartCode;
    /**
     * 工艺部件名称
     */
    @Field(type = FieldType.Text)
    private String craftPartName;
}
