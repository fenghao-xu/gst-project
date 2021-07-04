package com.ylzs.entity.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 说明：用户权限
 *
 * @author lyq
 * 2019-09-24 11:41
 */
@Data
public class RoleAuth {
    /**
     * 角色代码
     */
    private Integer roleId;
    /**
     * 模块代码
     */
    private Integer moduleId;
    /**
     * 查询权限
     */
    private Boolean isQuery;
    /**
     * 插入权限
     */
    private Boolean isInsert;
    /**
     * 编辑权限
     */
    private Boolean isEdit;
    /**
     * 删除权限
     */
    private Boolean isDelete;
    /**
     * 审核权限
     */
    private Boolean isAuth;
    /**
     * 反审核权限
     */
    private Boolean isUnauth;
    /**
     * 导入权限
     */
    private Boolean isImport;
    /**
     * 导出权限
     */
    private Boolean isExport;
    /**
     * 结案权限
     */
    private Boolean isFinish; //结案权限
    /**
     * 撤销权限
     */
    private Boolean isCancel; //撤销权限
    /**
     * 提交权限
     */
    private Boolean isCommit;
    /**
     * 上传权限
     */
    private Boolean isUpload;

    /**
     * 更新用户
     */
    private String updateUser;
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String updateTime;
    /**
     * 模块代码
     */
    private transient String moduleCode;

    /**
     * 模块名称
     */
    private transient String moduleName;

    /**
     * 角色代码
     */
    private transient String roleCode;
    /**
     * 角色名称
     */
    private transient String roleName;

}
