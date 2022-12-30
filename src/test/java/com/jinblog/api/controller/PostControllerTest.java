package com.jinblog.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    @DisplayName("/posts 요청시 Hello World를 출력한다.")
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
                .content("{\"name\":\"정용진\",\"age\":40, \"add\":\"경기도 의정부시 가금로34번길 23 힘스테이트 녹양역 102동 2204호\"}");


        mockMvc.perform(content)
                .andExpect(status().isOk())
                .andExpect(content().string("Hello World"))
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
}