package com.data.feijin.utils;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;


public class SpringMethodInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        System.err.println(invocation.getMethod().getName());
        return invocation.proceed();
    }

    @Before("@annotation(test)")
    public void before(){
        System.err.println("bbbb");
    }
}
