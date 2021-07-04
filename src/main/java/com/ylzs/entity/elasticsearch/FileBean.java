package com.ylzs.entity.elasticsearch;

import lombok.Data;

import java.io.Serializable;

/**
 * @className FileBean
 * @Description
 * @Author sky
 * @create 2020-02-22 19:42:40
 */
@Data
public class FileBean implements Serializable {

    private static final long serialVersionUID = -6275609204361533209L;

    //text支持分词搜索的字段有：name，author，content，filePath
    //keyword支持不分词搜索的字段有：name，author
    //suggest支持自动补全搜索的字段有：name，author
    /** 主键id */
    private String id;
    /** 文件名称 */
    private String name;
    /** 作者名称 */
    private String author;
    /** 文件内容 */
    private String content;
    /** 文件路径 */
    private String filePath;

    //不分词搜索
    public String getKeywordName() {
        return this.name;
    }
    public String getKeywordAuthor() {
        return this.author;
    }
    //自动补全
    public String getSuggestName() {
        return this.name;
    }
    public String getSuggestAuthor() {
        return this.author;
    }

}
