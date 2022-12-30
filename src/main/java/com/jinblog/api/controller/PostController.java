package com.jinblog.api.controller;


import com.jinblog.api.request.PostCreate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
public class PostController {

    @PostMapping("/posts")
    public String post(@RequestParam String name){

        log.info("name : {}", name);

        return "Hello World";
    }

    @PostMapping("/posts/json")
    @ResponseBody
    public String postJson(@RequestBody Map<String, Object> paramMap){


        paramMap.forEach( (k, v) -> {log.info("key : {}, value : {}", k, paramMap.get(k));});
        return "Hello World";
    }

    @PostMapping("/posts/dto/json")
    @ResponseBody
    public String postDto(@RequestBody PostCreate params) throws Exception {
        log.info("params : {}", params);

        String title = params.getTitle();
        if(!StringUtils.hasText(title)){
            throw new Exception("타이틀값이 없어요!");
        }

        String content = params.getContent();
        if(!StringUtils.hasText(content)){
            throw new Exception("컨텐츠값이 없어요!");
        }
        return "Hello World";
    }
}
