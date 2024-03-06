package com.alibou.security.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Log
@Component
@Aspect
public class LoggingAdvice {

    // execution in package any package, any class, any method - (..) <- any arguments
    @Pointcut(value = "execution(* com.alibou.security.auth.*.*(..) )")
    public void myPointCut() {

    }

    @Around("myPointCut()")
    public Object applicationLogger(ProceedingJoinPoint pjp) throws Throwable {

        // method name
        String methodName=pjp.getSignature().getName();

        // class name
        String className=pjp.getTarget().getClass().toString();

        // method array arguments
        Object[] args=pjp.getArgs();

        var mapper = new ObjectMapper();

        log.info("mehtod invoked " + className + "::" + methodName + "() " + "arguments : " + mapper.writeValueAsString(args));
        var object = pjp.proceed();

        log.info(className + "::" + methodName + "() " + "response : " + mapper.writeValueAsString(object));
        return object;

    }
}
