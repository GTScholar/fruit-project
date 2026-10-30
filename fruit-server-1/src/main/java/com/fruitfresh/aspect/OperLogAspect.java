package com.fruitfresh.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 接口日志切面：记录每个 Controller 接口的调用耗时
 */
@Slf4j
@Aspect
@Component
public class OperLogAspect {

    /** 切点：Controller 包下所有方法 */
    @Pointcut("execution(* com.fruitfresh.controller..*(..))")
    public void controllerPointcut() {
    }

    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long cost = System.currentTimeMillis() - start;
        String method = joinPoint.getSignature().getDeclaringType().getSimpleName()
                + "." + joinPoint.getSignature().getName();
        log.info("接口调用: {}，耗时: {}ms", method, cost);
        return result;
    }
}
