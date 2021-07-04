package com.ylzs.common.aspect;

/**
 * @Classname LogAspect
 * @Description 全局日志注解配置类
 * @Date 2019/8/22 13:57
 * @Created by Jacky-Liang
 */

public class LogAspect {
//public class LogAspect {
//
//    private final Logger logger = LoggerFactory.getLogger(LogAspect.class);
//
//    @Pointcut("execution( * com.ylzs..*.*(..))")
//    public void log(){}
//    @Before("log()")
//    public void before(JoinPoint joinPoint) {
//        String className = joinPoint.getTarget().getClass().getName();
//        String methodName = joinPoint.getSignature().getName();
//        StringBuilder log = new StringBuilder();
//        log.append("before: ").append(className).append("@").append(methodName).append(" , params: ");
//        Object[] args = joinPoint.getArgs();
//        for (Object arg : args) {
//            if (arg != null) {
//                log.append(arg + ", ");
//            }
//        }
//        logger.info(log.toString());
//    }
//
//    @AfterReturning(returning = "result",pointcut = "log()")
//    public void afterReturn(Object result) {
//        if (result != null) {
//     //       String resultJson = JSON.toJSONString(result);
//            logger.info("afterReturning: " + result);
//        }
//    }
//
//    @AfterThrowing(throwing = "ex",pointcut = "log()")
//    public void afterThrowing(Throwable ex) {
//        logger.error("afterThrowing: " + ex.getMessage(), ex);
//    }
//
//    @Around("log()")
//    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
//        Long begin = System.currentTimeMillis();
//        Signature sig = proceedingJoinPoint.getSignature();
//        MethodSignature msig = null;
//
//        StringBuilder log = new StringBuilder();
//
//        if (sig instanceof MethodSignature) {
//            msig = (MethodSignature) sig;
//            Object target = proceedingJoinPoint.getTarget();
//
//            Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
//            log.append("currentMethod: " + currentMethod.getName() + ", ");
//        }
//
//        log.append("around: ");
//
//        Object result = null;
//        try {
//            result = proceedingJoinPoint.proceed();
//        } catch (Exception e) {
//            logger.error(log + e.getMessage(), e);
//            throw e;
//        }
//        Long end = System.currentTimeMillis();
//        log.append(" 执行时间: ").append(end - begin).append("ms");
//
//        logger.info(log.toString());
//
//        return result;
//    }

}
