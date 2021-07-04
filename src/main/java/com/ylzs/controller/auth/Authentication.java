package com.ylzs.controller.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 说明：授权自定义注解
 *
 * @author lyq
 * 2019-10-15 15:43
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Authentication {
    public enum AuthType{QUERY,INSERT,EDIT,DELETE,AUTH,UNAUTH,IMPORT,EXPORT,FINISH,CANCEL,COMMIT};
    AuthType auth() default AuthType.QUERY;
    boolean required() default true;
}
