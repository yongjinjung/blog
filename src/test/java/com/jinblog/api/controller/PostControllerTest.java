package com.jinblog.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jinblog.api.domain.Post;
import com.jinblog.api.repository.PostRepository;
import com.jinblog.api.request.PostCreate;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest
@Slf4j
@AutoConfigureMockMvc
@SpringBootTest
class PostControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;


    @Test
    @DisplayName("/posts 요청시 Hello World 를 출력한다.")
    void post() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.POST, "/posts").param("name", "정용진"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello World"))
                .andDo(print());

    }

    @Test
    @DisplayName("/posts/json 형태요청")
    void postJson() throws Exception {
        MockHttpServletRequestBuilder content = MockMvcRequestBuilders.post("/posts/json")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"\",\"content\":\"경기도 의정부시 가금로34번길 23 힘스테이트 녹양역 102동 2204호\"}");


        mockMvc.perform(content)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("타이틀을 입력해주세요."))
                .andDo(print());
    }

    @Test
    @DisplayName("request 요청시 값을 검증한다.")
    void validTest() throws Exception {
        MockHttpServletRequestBuilder content = MockMvcRequestBuilders.post("/posts/dto/json")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"\", \"content\":\"경기도 의정부시 가금로34번길 23 힘스테이트 녹양역 102동 2204호\"}");

        mockMvc.perform(content)
                .andExpect(status().isOk())
                .andExpect(content().string("Hello World"))
                .andDo(print());
    }

    @Test
    @DisplayName("validTest2 test")
    void validTest2() throws Exception{
        MockHttpServletRequestBuilder content = MockMvcRequestBuilders.post("/posts/valid/json")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"\", \"content\":\"경기도 의정부시 가금로34번길 23 힘스테이트 녹양역 102동 2204호\"}");

        mockMvc.perform(content)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorMessage").value("타이틀을 입력해주세요."))
                .andDo(print());

    }


    @Test
    @DisplayName("validTest3 test")
    void validTest3() throws Exception{
        MockHttpServletRequestBuilder content = MockMvcRequestBuilders.post("/posts/valid/v2/json")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"\", \"content\":\"\"}");

        mockMvc.perform(content)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorMessage").value("타이틀을 입력해주세요."))
                .andDo(print());

    }

    @Test
    @DisplayName("validTest4 test")
    void validTest4() throws Exception{
        MockHttpServletRequestBuilder content = MockMvcRequestBuilders.post("/posts/valid/v2/json")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"\", \"content\":\"\"}");

        mockMvc.perform(content)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.errorFields[0].fieldName").value("title"))
                .andExpect(jsonPath("$.errorFields[1].fieldName").value("content")) //dot 표현식
                .andExpect(jsonPath("$['errorFields'][1]['fieldName']").value("content")) //bracket 표현식
                .andExpect(jsonPath("$.errorFields[1].fieldName").value("content"))
                .andDo(print());

    }

    @Test
    @DisplayName("컨텐츠를 작성하여 데이터를 저장한다.")
    void postWriter() throws Exception{
        //given
        MockHttpServletRequestBuilder content = MockMvcRequestBuilders.post("/posts/writer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"제목\", \"content\":\"컨텐츠\"}");

        //when
        mockMvc.perform(content)
                .andExpect(status().isOk())
                .andExpect(content().string("ok"))
                .andDo(print());

        //then
        //Expected 기대값
        //Actual 실제값
        assertThat(postRepository.count()).isEqualTo(2);

    }

    @Test
    @DisplayName("디비 내용을 조회한다.")
    void postFindAll() throws Exception{
        //given
        MockHttpServletRequestBuilder content = MockMvcRequestBuilders.post("/posts/writer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"제목\", \"content\":\"컨텐츠\"}");

        //when
        mockMvc.perform(content)
                .andExpect(status().isOk())
                .andExpect(content().string("ok"))
                .andDo(print());

        List<Post> list = postRepository.findAll();
        Post post = list.get(0);

        log.info("post.title :  {}", post.getTitle());

        //then
        assertThat(post.getTitle()).isEqualTo("제목");
    }

    @Test
    @DisplayName("object 데이터를 json 형태로 만들어 전송한다.")
    void postDataToJson() throws Exception{
        //given
        ObjectMapper mapper = new ObjectMapper();
        String jsonData = mapper.writeValueAsString(new PostCreate("제목입니다.","내용입니다."));

        MockHttpServletRequestBuilder content =  MockMvcRequestBuilders
                        .post("/posts/writer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonData);
        //when
        MvcResult ok = mockMvc.perform(content)
                .andExpect(status().isOk())
                .andExpect(content().string("ok"))
                .andDo(print())
                .andReturn();


        //then
        String result = ok.getResponse().getContentAsString();
        assertThat(result).isEqualTo("ok");


    }

    @Test
    @DisplayName("object 데이터를 json 형태로 만들어 전송한다.")
    void postDataBuild() throws Exception{
        //given

        PostCreate postCreate = PostCreate
                .builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();
        String jsonData = objectMapper.writeValueAsString(postCreate);

        MockHttpServletRequestBuilder content =  MockMvcRequestBuilders
                        .post("/posts/writer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonData);
        //when
        MvcResult ok = mockMvc.perform(content)
                .andExpect(status().isOk())
                .andExpect(content().string("ok"))
                .andDo(print())
                .andReturn();


        //then
        String result = ok.getResponse().getContentAsString();
        assertThat(result).isEqualTo("ok");


    }
}