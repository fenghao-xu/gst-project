package com.ylzs.entity.craftstd;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ylzs.core.model.SuperEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 说明：线号
 *
 * @author lyq
 * 2019-09-24 10:35
 */
@Data
public class Dictionary implements Serializable {

    private static final long serialVersionUID = -5598544060753998846L;

    /**
     * 序号
     */

    private int id;

    /**
     * 字典值
     */
    private String dicValue;

    /**
     * 值描述
     */
    private String valueDesc;


    /**
     * 次序号
     */
    private Integer seqNum;

    /**
     * 失效标志
     */
    @JsonIgnore
    private boolean isInvalid;


    /**
     * 更新用户
     */
    private String updateUser;


    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private String createUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 父级字典值列表(多个值用英文逗号分隔)
     */
    private Integer parentId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 中文首字母
     */
    private String pyHeadChar;

    /**
     * 字典值类型code
     */
    private String dictionaryTypeCode;

    /**
     * 父类code
     */
    private String parentCode;


    /**
     * 子模块
     */
    private transient List<Dictionary> childrens;

    private transient String parentDicValue;
    private transient String parentValueDesc;

    /**
     * B.半裙  存储这种格式
     */
    private transient String dicValueDesc;
    private transient String mapKey;

}
