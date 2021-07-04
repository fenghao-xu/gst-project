package com.ylzs.elasticsearch.workplacecraft.esbo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

/**
 * @author xuwei
 * @create 2020-02-27 17:31
 */
@Document(indexName = "staticdata", type = "workplacecraft")
public class WorkplaceCraftEsBO implements Serializable {

    @Id
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWorkplaceCraftCode() {
        return workplaceCraftCode;
    }

    public void setWorkplaceCraftCode(String workplaceCraftCode) {
        this.workplaceCraftCode = workplaceCraftCode;
    }

    public String getWorkplaceCraftName() {
        return workplaceCraftName;
    }

    public void setWorkplaceCraftName(String workplaceCraftName) {
        this.workplaceCraftName = workplaceCraftName;
    }

    public Long getCraftFlowNum() {
        return craftFlowNum;
    }

    public void setCraftFlowNum(Long craftFlowNum) {
        this.craftFlowNum = craftFlowNum;
    }

    public String getProductionPartCode() {
        return productionPartCode;
    }

    public void setProductionPartCode(String productionPartCode) {
        this.productionPartCode = productionPartCode;
    }

    public String getMainFrameName() {
        return mainFrameName;
    }

    public void setMainFrameName(String mainFrameName) {
        this.mainFrameName = mainFrameName;
    }

    public String getCraftCategoryName() {
        return craftCategoryName;
    }

    public void setCraftCategoryName(String craftCategoryName) {
        this.craftCategoryName = craftCategoryName;
    }

    /**
     * 工位工序代码
     */
    @Field(type = FieldType.Text)
    private String workplaceCraftCode;

    /**
     * 工位工序名称
     */
    private String workplaceCraftName;

    public Integer getDefault() {
        return isDefault;
    }

    public void setDefault(Integer aDefault) {
        isDefault = aDefault;
    }

    public String getMainFrameCode() {
        return mainFrameCode;
    }

    public void setMainFrameCode(String mainFrameCode) {
        this.mainFrameCode = mainFrameCode;
    }

    public String getCraftCategoryCode() {
        return craftCategoryCode;
    }

    public void setCraftCategoryCode(String craftCategoryCode) {
        this.craftCategoryCode = craftCategoryCode;
    }

    /**
     * 工序流（数字，隔100）
     */
    @Field(type = FieldType.Long)
    private Long craftFlowNum;


    /**
     * 生产部件代码
     */
    @Field(type = FieldType.Text)
    private String productionPartCode;

    /**
     * 是否默认工位工序,1--------是，0-------否
     */
    private Integer isDefault;

    public String getProductionPartName() {
        return productionPartName;
    }

    public void setProductionPartName(String productionPartName) {
        this.productionPartName = productionPartName;
    }

    /**
     * 生产部件名称
     */
    @Field(type = FieldType.Text)
    private String productionPartName;

    /**
     * 主框架名称
     */
    @Field(type = FieldType.Text)
    private String mainFrameName;

    /**
     * 主框架编码
     */
    @Field(type = FieldType.Text)
    private String mainFrameCode;

    /**
     * 工艺品类名称
     */
    @Field(type = FieldType.Text)
    private String craftCategoryName;

    /**
     * 工艺品类编码
     */
    @Field(type = FieldType.Text)
    private String craftCategoryCode;

}
