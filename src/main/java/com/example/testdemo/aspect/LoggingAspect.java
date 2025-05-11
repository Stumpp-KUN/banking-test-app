package com.example.testdemo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* com.example.testdemo.controller..*(..))")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        log.info(">> {}.{}() - args: {}", className, methodName, Arrays.toString(joinPoint.getArgs()));

        try {
            Object result = joinPoint.proceed();
            log.info("<< {}.{}() - result: {}", className, methodName, result);
            return result;
        } catch (Exception e) {
            log.error("!! {}.{}() - exception: {}", className, methodName, e.getMessage());
            throw e;
        }
    }

    @Around("execution(* com.example.testdemo.service..*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        log.debug("--> {}.{}()", className, methodName);

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long elapsedTime = System.currentTimeMillis() - startTime;
            log.debug("<-- {}.{}() - execution time: {} ms", className, methodName, elapsedTime);
            return result;
        } catch (Exception e) {
            log.error("<X- {}.{}() - failed after {} ms: {}",
                    className, methodName,
                    System.currentTimeMillis() - startTime,
                    e.getMessage());
            throw e;
        }
    }
}
