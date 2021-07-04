package com.ylzs.controller.system;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ylzs.common.util.Const;
import com.ylzs.common.util.MD5Util;
import com.ylzs.common.util.Result;
import com.ylzs.controller.auth.Authentication;
import com.ylzs.controller.auth.IModuleInfo;
import com.ylzs.controller.auth.UserContext;
import com.ylzs.entity.system.User;
import com.ylzs.service.system.IOperationLogService;
import com.ylzs.service.system.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.ylzs.common.util.Assert.*;

/**
 * 说明：用户接口
 *
 * @author lyq
 * 2019-09-30 16:54
 */
@Api(tags = "用户")
@RestController
@RequestMapping(value = "/user")
public class UserController implements IModuleInfo {
    @Resource
    IUserService userService;
    @Resource
    IOperationLogService operationLogService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //yyyy-MM-dd HH:mm:ss
        dateFormat.setLenient(false);//是否严格解析时间 false则严格解析 true宽松解析
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    //    @RequestMapping(value = "/add", method = RequestMethod.POST)
//    @ApiOperation(value = "add", notes = "添加用户")
//    @Authentication(auth= Authentication.AuthType.EDIT, required = true)
//    public Result<Integer> add(
//            @RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
//            @RequestParam(name = "用户代码", required = true) String userCode,
//            @RequestParam(name = "用户名称", required = true) String userName,
//            @RequestParam(name = "密码", required = true) String pwd,
//            @RequestParam(name = "是否是管理员", required = false) Boolean isAdmin,
//            @RequestParam(name = "所属部门", required = false) String department,
//            @RequestParam(name = "角色代码", required = false) String roleCode
//    ) {
//        notBlank(userCode, "用户代码不能为空");
//        notBlank(userName, "用户名称不能为空");
//        isTrue(pwd.length()<8, "密码过长");
//
//        User user = new User();
//        user.setUserCode(userCode);
//        user.setUserName(userName);
//        user.setPwd(MD5Util.getMD5(pwd));
//        user.setIsAdmin(isAdmin);
//        user.setDepartment(department);
//        user.setRoleCode(roleCode);
//        user.setUpdateTime(new Date());
//        user.setUpdateUser(currentUser.getUser());
//
//        Integer ret = userService.add(user);
//        return Result.ok(ret);
//    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "add", notes = "添加用户")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<Integer> add(
            @RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
            @RequestBody User user
    ) {
        notBlank(user.getUserCode(), "用户代码不能为空");
        notBlank(user.getUserName(), "用户名称不能为空");
        notBlank(user.getPassword(), "密码不能为空");
        isTrue(user.getPassword().length() < 8, "密码长度不能超过8");
        isTrue(user.getUserCode().length() <= 30, "用户代码长度不能超过30");
        isTrue(user.getUserName().length() <= 30, "用户名称长度不能超过30");

        user.setPwd(MD5Util.getMD5(user.getPassword()));
        user.setUpdateTime(new Date());
        user.setUpdateUser(currentUser.getUser());

        Integer ret = userService.addUser(user);
        if(ret == null || ret == 0) {
            return Result.build(0, "无法重复添加");
        }
        return Result.ok(ret);
    }

    @RequestMapping(value = "/delete/{userCodes}", method = RequestMethod.DELETE)
    @ApiOperation(value = "delete", notes = "删除用户")
    @Authentication(auth = Authentication.AuthType.DELETE, required = true)
    public Result<Integer> delete(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                  @PathVariable("userCodes") String userCodes) {
        notBlank(userCodes, "用户代码不能为空");
        String[] split = userCodes.split(",");
        Integer ret = 0;
        for (String userCode : split) {
            int res = userService.deleteUser(userCode, currentUser.getUserCode());
            if (res > 0) {
                operationLogService.addLog(getModuleCode(), IOperationLogService.ActionType.DELETE,
                        currentUser.getUser(), "删除用户" + userCode);
            }
            ret += res;
        }
        return Result.ok(ret);
    }


    //    @PostMapping(value = "/update")
//    @ApiOperation(value = "update", notes = "更新用户")
//    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
//    public Result<Integer> update(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
//                                  @RequestParam(name = "用户代码", required = true) String userCode,
//                                  @RequestParam(name = "用户名称", required = true) String userName,
//                                  @RequestParam(name = "密码（MD5)", required = true) String pwd,
//                                  @RequestParam(name = "是否是管理员", required = false) Boolean isAdmin,
//                                  @RequestParam(name = "所属部门", required = false) String department,
//                                  @RequestParam(name = "角色代码", required = false) String roleCode) {
//        User user = userService.getOne(userCode);
//        notNull(user, "用户名不存在");
//
//        user.setUserName(userName);
//        user.setPwd(MD5Util.getMD5(pwd));
//        user.setIsAdmin(isAdmin);
//        user.setDepartment(department);
//        user.setRoleCode(roleCode);
//        user.setUpdateUser(currentUser.getUser());
//        user.setUpdateTime(new Date());
//        Integer ret = userService.update(user);
//        return Result.ok(ret);
//    }

    @PostMapping(value = "/update")
    @ApiOperation(value = "update", notes = "更新用户")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<Integer> update(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                  @RequestBody User user) {
        notBlank(user.getUserCode(), "用户代码不能为空");
        notBlank(user.getUserName(), "用户名称不能为空");
        notBlank(user.getPassword(), "密码不能为空");
        isTrue(user.getPassword().length() < 8, "密码长度不能超过8");
        isTrue(user.getUserCode().length() <= 30, "用户代码长度不能超过30");
        isTrue(user.getUserName().length() <= 30, "用户名称长度不能超过30");

        if (user.getPassword() == null && user.getPassword() != "") {
            user.setPwd(MD5Util.getMD5(user.getPwd()));
        }
        user.setUpdateUser(currentUser.getUser());
        user.setUpdateTime(new Date());
        Integer ret = userService.updateUser(user);
        return Result.ok(ret);
    }

    @RequestMapping(value = "/getOne/{userCode}", method = RequestMethod.GET)
    @ApiOperation(value = "getOne", notes = "查询用户")
    @Authentication(auth = Authentication.AuthType.QUERY, required = true)
    public Result<List<User>> getUserByCode(@PathVariable("userCode") String userCode) {
        notBlank(userCode, "用户代码不能为空");
        String[] userCodes = new String[]{userCode};
        List<User> users = userService.getUserByCode(userCodes);
        return Result.ok(users);
    }

    @RequestMapping(value = "/getUserAndRole", method = RequestMethod.GET)
    @ApiOperation(value = "getUserAndRole", notes = "查询用户及角色")
    @Authentication(auth = Authentication.AuthType.QUERY, required = true)
    public Result<List<User>> getUserAndRole(@RequestParam(name = "userCode", required = true) String userCode) {
        notBlank(userCode, "用户代码不能为空");
        String[] userCodes = new String[]{userCode};
        List<User> users = userService.getUserAndRole(userCodes);
        return Result.ok(users);
    }



    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "getAll", notes = "查询全部用户")
    @Authentication(auth = Authentication.AuthType.QUERY, required = true)
    public Result<List<User>> getAll(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                     @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows,
                                     @RequestParam(name = "keywords", required = false) String keywords,
                                     @RequestParam(name = "beginDate", required = false) Date beginDate,
                                     @RequestParam(name = "endDate", required = false) Date endDate,
                                     @RequestParam(name = "roleId", required = false) Integer roleId) {
        PageHelper.startPage(page, rows);
        List<User> users = userService.getUserByPage(keywords, beginDate, endDate, roleId);
        PageInfo<User> pageInfo = new PageInfo<>(users);
        return Result.ok(users, pageInfo.getTotal());
    }

    @Override
    public String getModuleCode() {
        return "user";
    }

}
