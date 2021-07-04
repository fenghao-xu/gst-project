package com.ylzs.entity.elasticsearch;

import lombok.Data;

import java.io.Serializable;

/**
 * @className FileBeanQuery
 * @Description
 * @Author sky
 * @create 2020-02-22 19:44:43
 */
@Data
public class FileBeanQuery implements Serializable {

    /** 文件名称 */
    private String name;
    /** 作者名称 */
    private String author;
    /** 文件内容 */
    private String content;
    /** 文件路径 */
    private String filePath;
}
