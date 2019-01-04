package com.data.feijin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class WebController {

    @RequestMapping("index")
    private String index(){
        return "home.html";
    }
}
