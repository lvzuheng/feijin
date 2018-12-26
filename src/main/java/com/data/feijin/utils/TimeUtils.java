package com.data.feijin.utils;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TimeUtils {

    private static Map<String,Long> map = new HashMap();

    public static void start(){
       String k =  getInvokingName();
       map.put(k,System.currentTimeMillis());
    }

    public static void end(){
       long start =  map.get(getInvokingName());
       long end = System.currentTimeMillis();
       System.out.println("start:"+start+"<------>"+end+"<------>delay:"+(end - start));
    }

    private static String getInvokingName(){
        StackTraceElement[] es = Thread.currentThread().getStackTrace();
        StackTraceElement log = es[1];
        String tag = null;
        for (int i = 1; i < es.length; i++) {
            StackTraceElement e = es[i];
            if (!e.getClassName().equals(TimeUtils.class.getName())) {
                tag = e.getClassName() + "." + e.getMethodName();
                break;
            }
        }
        if (tag == null) {
            tag = log.getClassName() + "." + log.getMethodName();
        }
        return tag;
    }
}
