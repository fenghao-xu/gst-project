package com.ylzs.controller.auth;

import com.alibaba.fastjson.JSONObject;
import com.ylzs.common.cache.InitUtil;
import com.ylzs.common.constant.RedisContants;
import com.ylzs.common.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

import static com.ylzs.common.util.Const.REQUEST_KEY_USER;

/**
 * 说明：
 *
 * @author Administrator
 * 2019-10-15 16:06
 */
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationInterceptor.class);

    private static final String HEADER_AUTH = "X-Auth-Token";

    private static final String HEADER_AUTH_SMALL = "x-auth-token";
    private static final String PARAM_AUTH = "_token";

    private static final int STATUS_CODE = 401;
    private static final String STATUS_MESSAGE = "Unauthorized";

    private static final int STATUS_CODE_REPEAT = 301;
    private static final String STATUS_MESSAGE_REPEAT = "该帐户已在%s机器上登录";


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }


        // 对于方法或者类上标注了 Authentication 的请求，需要通过token获取到用户，并核查用户的角色
        String moduleCode = getModuleCode(handler);
        Authentication authentication = getAuthentication(handler);
        //if (authentication != null && moduleCode != null) {
        if (logger.isDebugEnabled()) {
            logger.debug("Required authentication: {}", request.getRequestURI());
        }


        String[] ipArr = {""};
        UserContext user = null;
        try {
            user = getUserContext(request, ipArr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (user == null) {
            if(authentication != null && authentication.required()) {
                outUnauthorized(response);
                return false;
            }

            return true;
        }


//fixme by lyq 2020-04-11 不判断用户权限
//            if (user != null && this.checkRoles(moduleCode, authentication.auth(), user)) {
        if (user != null) {
            request.setAttribute(REQUEST_KEY_USER, user);
            return true;
        }

//屏蔽校验
//        outUnauthorized(response);
//         }

        return true;
    }

    private void outUnauthorized(HttpServletResponse response) {
        response.setStatus(STATUS_CODE);
        Result<Void> r = Result.build(STATUS_CODE, STATUS_MESSAGE);
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.setCharacterEncoding("UTF-8");
        try {
            response.getWriter().println(JsonUtil.obj2string(r));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void outRepeatLogin(HttpServletResponse response, String ip) {
        response.setStatus(STATUS_CODE);
        Result<Void> r = Result.build(STATUS_CODE_REPEAT, String.format(STATUS_MESSAGE_REPEAT, ip));
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.setCharacterEncoding("UTF-8");
        try {
            response.getWriter().println(JsonUtil.obj2string(r));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    private boolean checkRoles(String moduleCode, Authentication.AuthType authType, UserContext user) {
//        if (user.getIsAdmin()) {
//            return true;
//        }
//
//        //后期改成MAP速度会快点
//        Boolean ret = false;
//        RoleAuth roleAuth = null;
//        if (user.getRoleAuthList() != null) {
//            List<RoleAuth> roleAuths = user.getRoleAuthList();
//            for (RoleAuth ra : roleAuths) {
//                if (moduleCode.equals(ra.getModuleCode())) {
//                    roleAuth = ra;
//                    break;
//                }
//            }
//        }
//        if (roleAuth == null) {
//            return ret;
//        }
//
//
//        switch (authType) {
//            case AUTH:
//                ret = roleAuth.getIsAuth();
//                break;
//            case EDIT:
//                ret = roleAuth.getIsEdit();
//                break;
//            case QUERY:
//                ret = roleAuth.getIsQuery();
//                break;
//            case COMMIT:
//                ret = roleAuth.getIsCommit();
//                break;
//            case CANCEL:
//                ret = roleAuth.getIsCancel();
//                break;
//            case DELETE:
//                ret = roleAuth.getIsDelete();
//                break;
//            case EXPORT:
//                ret = roleAuth.getIsExport();
//                break;
//            case FINISH:
//                ret = roleAuth.getIsFinish();
//                break;
//            case IMPORT:
//                ret = roleAuth.getIsImport();
//                break;
//            case INSERT:
//                ret = roleAuth.getIsInsert();
//                break;
//            case UNAUTH:
//                ret = roleAuth.getIsUnauth();
//                break;
//        }
//
//        if (ret == null) {
//            ret = false;
//        }
//
//        return ret.booleanValue();
//    }

    private Authentication getAuthentication(Object handlerMethod) {
        Authentication authentication = null;
        try {
            Method method = ((HandlerMethod) handlerMethod).getMethod();
            authentication = method.getAnnotation(Authentication.class);
            if (authentication == null) {
                authentication = method.getDeclaringClass().getAnnotation(Authentication.class);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return authentication;
    }

    private String getModuleCode(Object handlerMethod) {
        String moduleCode = null;
        try {
            Method method = ((HandlerMethod) handlerMethod).getMethod();
            Object controller = ((HandlerMethod) handlerMethod).getBean();
            if (controller != null) {
                if (controller instanceof IModuleInfo) {
                    moduleCode = ((IModuleInfo) controller).getModuleCode();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return moduleCode;
    }

    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(HEADER_AUTH);
        if (token == null) {
            token = request.getHeader(HEADER_AUTH_SMALL);
        }
        if (token == null) {
            token = request.getParameter(PARAM_AUTH);
        }
        return token;
    }

    private UserContext getUserContext(HttpServletRequest request, String[] ipArr) {
        UserContext userContext = null;
        String headerToken = getToken(request);

        if (headerToken != null && headerToken != "") {
            String tokenUserCode = JwtUtil.getTokenData(headerToken, "u");
            if (tokenUserCode != null && tokenUserCode != "") {
                //UserContext user = (UserContext)StaticDataCache.getInstance().getToken(tokenUserCode);
                RedisUtil redisUtil = (RedisUtil) InitUtil.applicationContext.getBean("redisUtil");
                Object redisVaue = redisUtil.hget(RedisContants.REDIS_LOGIN_KEY_PRE, tokenUserCode);
                UserContext user = null;
                if (null != redisVaue) {
                    user = JSONObject.parseObject((String) redisVaue, UserContext.class);
                }
                if (user != null) {
                    if (headerToken.equals(user.getToken())) {
                        if (JwtUtil.checkToken(headerToken)) {
                            userContext = user;
                        }
                    } else {
                        ipArr[0] = user.getIp();
                    }
                }
            }
        }

        //fixme by lyq 20200414 测试未找到用户
        String ip = ClientUtil.getIpAddress(request);
        if (userContext != null) {
            logger.info(ip + " " + userContext.getUser() + " " + headerToken + " " + request.getRequestURI());
        } else {
            logger.info(ip + " 未取到用户上下文信息 " + headerToken + " " + request.getRequestURI());
        }

        return userContext;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {

    }
}
