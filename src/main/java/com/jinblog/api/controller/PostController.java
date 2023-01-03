package com.jinblog.api.controller;


import com.jinblog.api.domain.Post;
import com.jinblog.api.request.PostCreate;
import com.jinblog.api.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

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


    @PostMapping("/posts/valid/json")
    @ResponseBody
    public Map<String, Object> postValid(@RequestBody @Validated PostCreate params, BindingResult result){

        if(result.hasErrors()) {

            List<FieldError> fieldErrors = result.getFieldErrors();
            FieldError firstFieldError = fieldErrors.get(0);
            String fieldName = firstFieldError.getField(); //property name
            String errorMessage = firstFieldError.getDefaultMessage(); //error message

            Map<String, Object> error = new HashMap<>();
            error.put("fieldName", fieldName);
            error.put("errorMessage", errorMessage);
            return error;
        }

        return Map.of();
    }


    @PostMapping("/posts/valid/v2/json")
    @ResponseBody
    public Map<String, Object> postValidV2(@RequestBody @Validated PostCreate params){

        return Map.of();
    }


    @PostMapping("/posts/writer")
    @ResponseBody
    public ResponseEntity<Object> postWriter(@RequestBody @Validated PostCreate params){
        postService.write(params);
        return ResponseEntity.ok("ok");
    }

    /**
     * /posts -> 글 전체 조회(검색 + 페이징)
     * /posts/{postId} -> 글 한개만 조회
     */
    @ResponseBody
    @GetMapping("/posts/{postId}")
    public ResponseEntity<Object> get(@PathVariable(name = "postId") Long id){
        Post post = postService.get(id);

        return ResponseEntity.ok(post);
    }


}
