package com.data.feijin;

import com.data.feijin.utils.CheckUtils;
import com.data.feijin.utils.TimeUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DataServer {


    public static void main(String[] args) {
//        SpringApplication.run(DataServer.class, args);
        CheckUtils.match2();
//        TimeUtils timeUtils = new TimeUtils();
//        timeUtils.checkTime(null);
    }
}
