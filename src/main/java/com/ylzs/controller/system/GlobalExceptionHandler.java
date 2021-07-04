package com.ylzs.controller.system;

import com.ylzs.common.util.AssertException;
import com.ylzs.common.util.Result;
import org.apache.shiro.dao.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 说明：
 *
 * @author Administrator
 * 2019-10-11 10:00
 */

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(value = {AssertException.class})
    @ResponseBody
    public Result<Void> handleAssertException(Exception e) {
        logger.info("assert error: {}", e.getMessage());
        return Result.build(0, e.getMessage());
    }

    @ExceptionHandler(value = {DataAccessException.class})
    @ResponseBody
    public Result<Void> handDataAccessException(DataAccessException e) {
        logger.info("data access Error: {}", e.getMessage());
        return Result.build(0, "数据库异常,详情可查看日志");
    }


    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public Result<Void> runtimeException(RuntimeException e) {
        logger.error("runtime error: {}", e);
        return Result.build(0, e.getMessage());
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseBody
    public Result<Void> handleException(Exception e) {
        logger.error("error: {}", e);
        return Result.build(0, e.getMessage());
    }

}
