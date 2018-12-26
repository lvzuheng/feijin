package com.data.feijin.utils;

import com.data.feijin.annotation.Cache;
import org.springframework.stereotype.Component;

@Component
public class CacheDemo {
    @Cache
    public void cache(Integer integer,Object... s){
        for (Object ss:s) {
            System.out.println(ss);
        }
        System.out.println(integer);
    }
}
