package com.data.feijin.controller;

import com.data.feijin.utils.FileStreamReader;
import com.data.feijin.utils.Pair;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataController {

    private String url = "/home/lzh/develop/data/";

    @GetMapping(value = "jifen.aspx")
    private String jifen(){
        String data = FileStreamReader.getFileReader().read(url+"jifen");
        return  data;
    }
    @GetMapping(value = "Player_XML.aspx")
    private String player_XML(){
        String data = FileStreamReader.getFileReader().read(url+"Player_XML");
        return data;
    }
    @GetMapping(value = "Team_XML.aspx")
    private String team_XML(){
        String data = FileStreamReader.getFileReader().read(url+"Team_XML");
        return data;
    }
}
