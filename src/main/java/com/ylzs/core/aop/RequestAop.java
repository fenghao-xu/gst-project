package com.ylzs.core.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.Enumeration;
/**
 * @className RequestAop
 * @Description
 * @Author sky
 * @create 2020-04-14 11:37:06
 */
@Aspect
@Component
public class RequestAop {

    private static final Logger logger = LoggerFactory.getLogger(RequestAop.class);

    private static ThreadLocal<Long> startTime = new ThreadLocal<Long>();
    private static ThreadLocal<String> key = new ThreadLocal<String>();
    private static ObjectMapper objectMapper = new ObjectMapper();

    @Pointcut("@annotation(io.swagger.annotations.ApiOperation)")
    public void controllerMethodPointcut(){}

    @Before("controllerMethodPointcut()")
    public void before(JoinPoint joinPoint) throws UnsupportedEncodingException {



        //从切面织入点处通过反射机制获取织入点处的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取切入点所在的方法
        Method method = signature.getMethod();

        //获取操作
        ApiOperation myLog = method.getAnnotation(ApiOperation.class);
        if (myLog != null) {
            String value = myLog.value();
            myLog.notes();
        }

        //获取请求的类名
        String className = joinPoint.getTarget().getClass().getName();
        //获取请求的方法名
        String methodName = method.getName();


        //请求的参数
        Object[] args = joinPoint.getArgs();
        //将参数所在的数组转换成json


        // 请求开始时间
        startTime.set(System.currentTimeMillis());
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        // 如果有session则返回session如果没有则返回null(避免创建过多的session浪费内存)
        HttpSession session = request.getSession(false);
        // 获取请求头
        Enumeration<String> enumeration = request.getHeaderNames();
        StringBuffer headers = new StringBuffer();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            headers.append(name + ":" + value).append(",");
        }
//        String uri = UUID.randomUUID() + "|" + request.getRequestURI();
//
//        String method = request.getMethod();
//        StringBuffer params = new StringBuffer();
//        if (HttpMethod.GET.toString().equals(method)) {// get请求
//            String queryString = request.getQueryString();
//            if (StringUtils.isNotBlank(queryString)) {
//                params.append(URLEncoder.encode(queryString, "UTF-8"));
//            }
//        } else {//其他请求
//            Object[] paramsArray = joinPoint.getArgs();
//            if (paramsArray != null && paramsArray.length > 0) {
//                for (int i = 0; i < paramsArray.length; i++) {
//                    if (paramsArray[i] instanceof Serializable) {
//                        params.append(paramsArray[i].toString()).append(",");
//                    } else {
//                        //使用json系列化 反射等等方法 反系列化会影响请求性能建议重写tostring方法实现系列化接口
//                        try {
//                            String param= objectMapper.writeValueAsString(paramsArray[i]);
//                            if(StringUtils.isNotBlank(param))
//                                params.append(param).append(",");
//                        } catch (JsonProcessingException e) {
//                            logger.error("doBefore obj to json exception obj={},msg={}",paramsArray[i],e);
//                        }
//                    }
//                }
//            }
//        }
//        key.set(uri);
//        logger.info("request params>>>>>>uri={},method={},params={},headers={}", uri, method, params, headers);
    }

    /**
     * 在方法执行后打印返回内容
     *
     * @param obj
     */
    @AfterReturning(returning = "obj", pointcut = "controllerMethodPointcut()")
    public void doAfterReturing(Object obj) {
        long costTime = System.currentTimeMillis() - startTime.get();
        String uri = key.get();
        startTime.remove();
        key.remove();
        String result= null;
        if(obj instanceof Serializable){
            result =  obj.toString();
        }else {
            if(obj != null) {
                try {
                    result = objectMapper.writeValueAsString(obj);
                } catch (JsonProcessingException e) {
                    logger.error("doAfterReturing obj to json exception obj={},msg={}",obj,e);
                }
            }
        }
        logger.info("response result<<<<<<uri={},result={},costTime={}ms", uri, result, costTime);
    }
}


