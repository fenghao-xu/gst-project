package com.ylzs.controller.auth;

import com.alibaba.fastjson.JSONObject;
import com.ylzs.common.constant.MessageConstant;
import com.ylzs.common.constant.RedisContants;
import com.ylzs.common.util.*;
import com.ylzs.entity.auth.CappRole;
import com.ylzs.entity.auth.resp.CappUserRoleToMenuResp;
import com.ylzs.entity.system.RoleAuth;
import com.ylzs.entity.system.User;
import com.ylzs.service.auth.CappRoleService;
import com.ylzs.service.craftstd.IDictionaryService;
import com.ylzs.service.system.IOperationLogService;
import com.ylzs.service.system.IRoleAuthService;
import com.ylzs.service.system.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

import static com.ylzs.common.util.Assert.*;

/**
 * 说明：
 *
 * @author Administrator
 * 2019-10-15 17:52
 */
@Api(tags = "用户授权")
@RestController
@RequestMapping(value = "/auth")
public class AuthenticationController implements IModuleInfo {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Resource
    private IUserService userService;
    @Resource
    private IRoleAuthService roleAuthService;
    @Resource
    private IDictionaryService dictionaryService;
    @Resource
    IOperationLogService operationLogService;

    @Autowired
    private RedisUtil redisUtil;

    @Resource
    private CappRoleService roleService;


    @ApiOperation(value = "登录")
    @RequestMapping(value = "/login", method = RequestMethod.PUT)
    public Result<LoginResponse> login(HttpServletRequest request, @RequestBody String jsonString) {
        JSONObject jsonData = JSONObject.parseObject(jsonString);
        String userCode = jsonData.getString("userCode");
        String password = jsonData.getString("password");
        notBlank(userCode, "用户代码不能为空");
        notBlank(password, "密码不能为空");
        String[] userCodes = new String[]{userCode};
        List<User> users = userService.getUserByCode(userCodes);
        notNull(users, "用户名不存在");
        isTrue(users.size() > 0, "用户名不存在");

        User user = users.get(0);
        isTrue(password.equals(user.getPwd()), "密码不正确");
        String ip = ClientUtil.getIpAddress(request);
        userService.updateUser(user);

        List<CappUserRoleToMenuResp> roleAuths = roleService.getRoleByUserCode(user.getUserCode());
        String token = JwtUtil.buildToken("u", user.getUserCode());
        UserContext userContext = new UserContext(user.getUserCode(), user.getUserName(), roleAuths, token, user.getIsAdmin(), ip);
        //fixme by lyq 20200414 测试未找到用户
        logger.info(ip, userContext.getUser(), token);
        //有效期一天
        redisUtil.hset(RedisContants.REDIS_LOGIN_KEY_PRE, user.getUserCode(), JSONObject.toJSONString(userContext), RedisContants.LOGIN_EXPIRE_TIME);
        /*UserContext uc = (UserContext)StaticDataCache.TOKEN_MAP.putIfAbsent(user.getUserCode(), userContext);
        while (uc != null) {
            StaticDataCache.TOKEN_MAP.remove(user.getUserCode());
            uc = (UserContext)StaticDataCache.TOKEN_MAP.putIfAbsent(user.getUserCode(), userContext);
        }*/

        operationLogService.addLog(getModuleCode(), IOperationLogService.ActionType.LOGIN,
                user.getUserCode() + " " + user.getUserName(), "用户登录" + user.getUserCode() + ' ' + user.getUserName(), userContext.getIp());
        return Result.ok(new LoginResponse(user, token, roleAuths));
    }


    @ApiOperation(value = "注销")
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public Result<String> logout(@RequestParam(name = "userCode", required = true) String userCode) {
        if (StringUtils.isEmpty(userCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "userCode参数为空");
        }
        redisUtil.hdel(RedisContants.REDIS_LOGIN_KEY_PRE, userCode);
        operationLogService.addLog(getModuleCode(), IOperationLogService.ActionType.LOGOUT,
                userCode + " " + "用户登出" + userCode, null);
        return Result.ok(MessageConstant.SUCCESS, "注销登录成功");
    }

    @ApiOperation(value = "修改密码")
    @RequestMapping(value = "/change-pwd", method = RequestMethod.PUT)
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<Void> changePwd(
            @RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
            @RequestBody String jsonString
    ) {
        JSONObject jsonData = JSONObject.parseObject(jsonString);
        String oldPwd = jsonData.getString("oldPwd");
        String newPwd = jsonData.getString("newPwd");

        notBlank(oldPwd, "旧密码不能为空");
        notBlank(newPwd, "新密码不能为空");
        isFalse(oldPwd.equals(newPwd), "新旧密码不能相同");
        isTrue(newPwd.length() <= 8, "新密码不能超过8位");
        String[] userCodes = new String[]{currentUser.getUserCode()};

        List<User> users = userService.getUserByCode(userCodes);
        notNull(users, "用户名不存在");
        isTrue(users.size() > 0, "用户名不存在");
        User user = users.get(0);
        String oldPwdMd5 = MD5Util.getMD5(oldPwd);
        isTrue(oldPwdMd5.equals(user.getPwd()), "原密码不一致");
        String newPwdMd5 = MD5Util.getMD5(newPwd);
        user.setPwd(newPwdMd5);
        user.setUpdateTime(new Date());
        user.setUpdateUser(currentUser.getUser());
        userService.updateUser(user);

        //清空缓存中用户的token
        //StaticDataCache.TOKEN_MAP.remove(user.getUserCode());
        redisUtil.hdel(RedisContants.REDIS_LOGIN_KEY_PRE, user.getUserCode());
        return Result.ok();
    }

    static class LoginResponse {
        public final User user;
        public final String token;
        public final List<CappUserRoleToMenuResp> roleAuths;

        LoginResponse(User user, String token, List<CappUserRoleToMenuResp> roleAuths) {
            super();
            this.user = user;
            this.token = token;
            this.roleAuths = roleAuths;
        }
    }

    @Override
    public String getModuleCode() {
        return "auth";
    }
}
