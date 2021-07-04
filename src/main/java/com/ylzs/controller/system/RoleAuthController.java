package com.ylzs.controller.system;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ylzs.common.util.Const;
import com.ylzs.common.util.Result;
import com.ylzs.controller.auth.Authentication;
import com.ylzs.controller.auth.IModuleInfo;
import com.ylzs.controller.auth.UserContext;
import com.ylzs.entity.system.RoleAuth;
import com.ylzs.service.system.IOperationLogService;
import com.ylzs.service.system.IRoleAuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.ylzs.common.util.Assert.notNull;

/**
 * 说明：用户管理接口
 *
 * @author lyq
 * 2019-09-30 16:54
 */
@Api(tags = "角色权限")
@RestController
@RequestMapping(value = "/role-auth")
public class RoleAuthController implements IModuleInfo {
    @Resource
    IRoleAuthService roleAuthService;
    @Resource
    IOperationLogService operationLogService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //yyyy-MM-dd HH:mm:ss
        dateFormat.setLenient(false);//是否严格解析时间 false则严格解析 true宽松解析
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

//    @PostMapping(value = "/add")
//    @ApiOperation(value = "add", notes = "添加角色权限")
//    @Authentication(auth= Authentication.AuthType.EDIT, required = true)
//    public Result<Integer> add(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
//                               @RequestParam(name = "角色代码", required = true) String roleCode,
//                               @RequestParam(name = "模块代码", required = true) String moduleCode,
//                               @RequestParam(name = "查询权限", required = false) Boolean isQuery,
//                               @RequestParam(name = "插入权限", required = false) Boolean isInsert,
//                               @RequestParam(name = "编辑权限", required = false) Boolean isEdit,
//                               @RequestParam(name = "删除权限", required = false) Boolean isDelete,
//                               @RequestParam(name = "审核权限", required = false) Boolean isAuth,
//                               @RequestParam(name = "反审核权限", required = false) Boolean isUnauth,
//                               @RequestParam(name = "导入权限", required = false) Boolean isImport,
//                               @RequestParam(name = "导出权限", required = false) Boolean isExport,
//                               @RequestParam(name = "结案权限", required = false) Boolean isFinish,
//                               @RequestParam(name = "撤销权限", required = false) Boolean isCancel,
//                               @RequestParam(name = "提交权限", required = false) Boolean isCommit) {
//        notBlank(roleCode, "角色代码不能为空");
//        notBlank(moduleCode, "模块代码不能为空");
//        RoleAuth roleAuth = new RoleAuth();
//        roleAuth.setRoleCode(roleCode);
//        roleAuth.setModuleCode(moduleCode);
//        roleAuth.setIsQuery(isQuery);
//        roleAuth.setIsInsert(isInsert);
//        roleAuth.setIsEdit(isEdit);
//        roleAuth.setIsDelete(isDelete);
//        roleAuth.setIsAuth(isAuth);
//        roleAuth.setIsUnauth(isUnauth);
//        roleAuth.setIsImport(isImport);
//        roleAuth.setIsExport(isExport);
//        roleAuth.setIsFinish(isFinish);
//        roleAuth.setIsCancel(isCancel);
//        roleAuth.setIsCommit(isCommit);
//        Integer ret = roleAuthService.add(roleAuth);
//        return Result.ok(ret);
//
//    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "add", notes = "添加角色权限")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<Integer> add(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                               @RequestBody RoleAuth roleAuth) {
        notNull(roleAuth.getRoleId(), "角色id不能为空");
        notNull(roleAuth.getModuleId(), "模块id不能为空");
        Integer ret = roleAuthService.addRoleAuth(roleAuth);
        if(ret == null || ret == 0) {
            return Result.build(0, "无法重复添加");
        }
        return Result.ok(ret);

    }


    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ApiOperation(value = "delete", notes = "删除角色权限")
    @Authentication(auth = Authentication.AuthType.DELETE, required = true)
    public Result<Integer> delete(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                  @RequestParam(name = "roleId", required = true) Integer roleId,
                                  @RequestParam(name = "moduleId", required = true) Integer moduleId) {
        notNull(roleId, "角色id不能为空");
        notNull(moduleId, "模块id不能为空");
        Integer ret = roleAuthService.deleteRoleAuth(roleId, moduleId);
        if (ret != null && ret > 0) {
            operationLogService.addLog(getModuleCode(), IOperationLogService.ActionType.DELETE,
                    currentUser.getUser(), "删除角色权限" + roleId.toString() + "模块代码" + moduleId.toString());
        }
        return Result.ok(ret);
    }

    //    @PostMapping(value = "/update")
//    @ApiOperation(value = "update", notes = "更新角色权限")
//    @Authentication(auth= Authentication.AuthType.EDIT, required = true)
//    public Result<Integer> update(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
//                                  @RequestParam(name = "角色代码", required = true) String roleCode,
//                                  @RequestParam(name = "模块代码", required = true) String moduleCode,
//                                  @RequestParam(name = "查询权限", required = false) Boolean isQuery,
//                                  @RequestParam(name = "插入权限", required = false) Boolean isInsert,
//                                  @RequestParam(name = "编辑权限", required = false) Boolean isEdit,
//                                  @RequestParam(name = "删除权限", required = false) Boolean isDelete,
//                                  @RequestParam(name = "审核权限", required = false) Boolean isAuth,
//                                  @RequestParam(name = "反审核权限", required = false) Boolean isUnauth,
//                                  @RequestParam(name = "导入权限", required = false) Boolean isImport,
//                                  @RequestParam(name = "导出权限", required = false) Boolean isExport,
//                                  @RequestParam(name = "结案权限", required = false) Boolean isFinish,
//                                  @RequestParam(name = "撤销权限", required = false) Boolean isCancel,
//                                  @RequestParam(name = "提交权限", required = false) Boolean isCommit) {
//        notBlank(roleCode, "角色代码不能为空");
//        notBlank(moduleCode, "模块代码不能为空");
//        RoleAuth roleAuth = new RoleAuth();
//        roleAuth.setIsQuery(isQuery);
//        roleAuth.setIsInsert(isInsert);
//        roleAuth.setIsEdit(isEdit);
//        roleAuth.setIsDelete(isDelete);
//        roleAuth.setIsAuth(isAuth);
//        roleAuth.setIsUnauth(isUnauth);
//        roleAuth.setIsImport(isImport);
//        roleAuth.setIsExport(isExport);
//        roleAuth.setIsFinish(isFinish);
//        roleAuth.setIsCancel(isCancel);
//        roleAuth.setIsCommit(isCommit);
//        Integer ret = roleAuthService.update(roleAuth);
//        return Result.ok(ret);
//    }
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @ApiOperation(value = "update", notes = "更新角色权限")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<Integer> update(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                  @RequestBody RoleAuth roleAuth) {
        notNull(roleAuth.getRoleId(), "角色id不能为空");
        notNull(roleAuth.getModuleId(), "模块id不能为空");
        Integer ret = roleAuthService.updateRoleAuth(roleAuth);
        return Result.ok(ret);
    }


    @RequestMapping(value = "/getOne", method = RequestMethod.GET)
    @ApiOperation(value = "getOne", notes = "查询角色权限")
    @Authentication(auth = Authentication.AuthType.QUERY, required = true)
    public Result<List<RoleAuth>> getOne(@RequestParam(name = "roleId", required = true) Integer roleId,
                                   @RequestParam(name = "moduleId", required = true) Integer moduleId) {
        notNull(roleId, "角色代码不能为空");
        notNull(moduleId, "模块代码不能为空");
        List<RoleAuth> roleAuths = roleAuthService.getRoleAuthById(roleId, moduleId);
        return Result.ok(roleAuths);
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "getAll", notes = "查询全部角色权限")
    @Authentication(auth = Authentication.AuthType.QUERY, required = true)
    public Result<List<RoleAuth>> getAll(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                         @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows,
                                         @RequestParam(name = "keywords", required = false) String keywords,
                                         @RequestParam(name = "beginDate", required = false) Date beginDate,
                                         @RequestParam(name = "endDate", required = false) Date endDate,
                                         @RequestParam(name = "roleId", required = false) Integer roleId) {
        PageHelper.startPage(page, rows);
        List<RoleAuth> roleAuths = roleAuthService.getRoleAuthByPage(keywords, beginDate, endDate, roleId);
        PageInfo<RoleAuth> pageInfo = new PageInfo<>(roleAuths);
        return Result.ok(roleAuths, pageInfo.getTotal());
    }

    @Override
    public String getModuleCode() {
        return "role-auth";
    }


}
