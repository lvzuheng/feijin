package com.data.feijin.utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Aspect
public class AspectInterceptor {


    //    @Pointcut("execution(* com.data.feijin.controller..*.*(..))")
//    public void pointcut(){}
    @Pointcut(value = "@annotation(com.data.feijin.annotation.Cache)")//指向自定义注解路径
    public void pointcut(){}

//    @After("pointcut()")
//    public void afterMethod(JoinPoint joinPoint){
//        System.err.println("方法后执行:");
//        System.out.println(joinPoint.getSignature().getName());
//    }
    @Around(value = "@annotation(com.data.feijin.annotation.Cache)")
    public Object aroundMethod(ProceedingJoinPoint joinPoint){
        try {
            Object[] args = joinPoint.getArgs();
//            for (Object io:args) {
//                for (Object i:(Object[])io){
//                    System.err.println("i:"+i+","+i.getClass().getTypeName());
//                }
//                System.err.println(args.getClass().getTypeName());
//            }
            args[0] = 12;
            args[1] = new String[]{"sasasa","ssasas"};
            MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
            Method targetMethod = methodSignature.getMethod();
            return joinPoint.proceed(args);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

}
