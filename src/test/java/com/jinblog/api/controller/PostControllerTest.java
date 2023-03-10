package com.jinblog.api.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jinblog.api.domain.Post;
import com.jinblog.api.repository.PostRepository;
import com.jinblog.api.request.PostCreate;
import com.jinblog.api.request.PostEdit;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest
@Slf4j
@AutoConfigureMockMvc
@SpringBootTest
class PostControllerTest {



    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void clean() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("/posts ????????? Hello World ??? ????????????.")
    void post() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.POST, "/posts").param("name", "?????????"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello World"))
                .andDo(print());

    }

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilter(new CharacterEncodingFilter(StandardCharsets.UTF_8.name(), true))
                .alwaysDo(print())
                .build();
    }

    @Test
    @DisplayName("/posts/json ????????????")
    void postJson() throws Exception {
        MockHttpServletRequestBuilder content = MockMvcRequestBuilders.post("/posts/json")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"\",\"content\":\"????????? ???????????? ?????????34?????? 23 ??????????????? ????????? 102??? 2204???\"}");


        mockMvc.perform(content)
                .andExpect(status().isOk())
                .andExpect(content().string("Hello World"))
                .andDo(print());
    }

    @Test
    @DisplayName("request ????????? ?????? ????????????.")
    void validTest() throws Exception {
        MockHttpServletRequestBuilder content = MockMvcRequestBuilders.post("/posts/dto/json")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"\", \"content\":\"????????? ???????????? ?????????34?????? 23 ??????????????? ????????? 102??? 2204???\"}");

        mockMvc.perform(content)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("??????????????? ?????????!"))
                .andDo(print());
    }

    @Test
    @DisplayName("validTest2 test")
    void validTest2() throws Exception {
        MockHttpServletRequestBuilder content = MockMvcRequestBuilders.post("/posts/valid/json")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"\", \"content\":\"????????? ???????????? ?????????34?????? 23 ??????????????? ????????? 102??? 2204???\"}");

        mockMvc.perform(content)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorMessage").value("???????????? ??????????????????."))
                .andDo(print());

    }


    @Test
    @DisplayName("validTest3 test")
    void validTest3() throws Exception {
        MockHttpServletRequestBuilder content = MockMvcRequestBuilders.post("/posts/valid/v2/json")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"\", \"content\":\"\"}");

        mockMvc.perform(content)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields[0].validMsg").isNotEmpty())
                .andDo(print());

    }

    @Test
    @DisplayName("validTest4 test")
    void validTest4() throws Exception {
        MockHttpServletRequestBuilder content = MockMvcRequestBuilders.post("/posts/valid/v2/json")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"\", \"content\":\"\"}");

        mockMvc.perform(content)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("????????? ???????????????."))
                //.andExpect(jsonPath("$.errorFields[0].fieldName").value("title"))
                //.andExpect(jsonPath("$.errorFields[1].fieldName").value("content")) //dot ?????????
                //.andExpect(jsonPath("$['errorFields'][1]['fieldName']").value("content")) //bracket ?????????
                //.andExpect(jsonPath("$.errorFields[1].fieldName").value("content"))
                .andDo(print());

    }

    @Test
    @DisplayName("???????????? ???????????? ???????????? ????????????.")
    void postWriter() throws Exception {
        //given
        MockHttpServletRequestBuilder content = MockMvcRequestBuilders.post("/posts/writer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"??????\", \"content\":\"?????????\"}");

        //when
        mockMvc.perform(content)
                .andExpect(status().isOk())
                .andExpect(content().string("ok"))
                .andDo(print());

        //then
        //Expected ?????????
        //Actual ?????????
        assertThat(postRepository.count()).isEqualTo(1);

    }

    @Test
    @DisplayName("?????? ????????? ????????????.")
    void postFindAll() throws Exception {
        //given
        MockHttpServletRequestBuilder content = MockMvcRequestBuilders.post("/posts/writer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"??????\", \"content\":\"?????????\"}");

        //when
        mockMvc.perform(content)
                .andExpect(status().isOk())
                .andExpect(content().string("ok"))
                .andDo(print());

        List<Post> list = postRepository.findAll();
        Post post = list.get(0);

        log.info("post.title :  {}", post.getTitle());

        //then
        assertThat(post.getTitle()).isEqualTo("??????");
    }

    @Test
    @DisplayName("object ???????????? json ????????? ????????? ????????????.")
    void postDataToJson() throws Exception {
        //given
        ObjectMapper mapper = new ObjectMapper();
        String jsonData = mapper.writeValueAsString(new PostCreate("???????????????.", "???????????????."));

        MockHttpServletRequestBuilder content = MockMvcRequestBuilders
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
    @DisplayName("object ???????????? json ????????? ????????? ????????????.")
    void postDataBuild() throws Exception {
        //given

        PostCreate postCreate = PostCreate
                .builder()
                .title("???????????????.")
                .content("???????????????.")
                .build();
        String jsonData = objectMapper.writeValueAsString(postCreate);

        MockHttpServletRequestBuilder content = MockMvcRequestBuilders
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
    void postsList() throws Exception {

        Post post1 = Post.builder()
                .title("titl11")
                .content("cont11")
                .build();
        //postRepository.save(post1);
        Post post2 = Post.builder()
                .title("titl22")
                .content("cont22")
                .build();
        //postRepository.save(post2);
        postRepository.saveAll(Arrays.asList(post1, post2));

        MockHttpServletRequestBuilder content = MockMvcRequestBuilders.get("/posts/list")
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(content)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.is(2)))
                .andExpect(jsonPath("$.[0].title").value("titl11"))
                .andDo(print())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();

        List<Post> list = objectMapper.readValue(contentAsString, new TypeReference<List<Post>>() {
        });
        for (Post post : list) {
            log.info("tit : {}", post.getTitle());
            log.info("content : {}", post.getContent());
        }
        assertThat(list.size()).isEqualTo(2);
    }

    @Test
    void postsPage() throws Exception {

        //given
        List<Post> postList = IntStream.range(0, 30)
                .mapToObj(i -> Post.builder()
                        .title(" ?????? -" + i)
                        .content("?????? - " + i)
                        .build())
                .collect(Collectors.toList());

        postRepository.saveAll(postList);

        MockHttpServletRequestBuilder content = MockMvcRequestBuilders.get("/posts/list/{pageNum}", 1)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(content)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.is(10)))
                .andExpect(jsonPath("$.[0].title").value(" ?????? -0"))
                .andDo(print())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();

        List<Post> list = objectMapper.readValue(contentAsString, new TypeReference<List<Post>>() {
        });
        for (Post post : list) {
            log.info("tit : {}", post.getTitle());
            log.info("content : {}", post.getContent());
        }
        assertThat(list.size()).isEqualTo(10);
    }

    @Test
    void query_dsl() throws Exception{
        //given
        List<Post> postList = IntStream.range(0, 1000)
                .mapToObj(i -> Post.builder()
                        .title("?????? -" + i)
                        .content("?????? - " + i)
                        .build())
                .collect(Collectors.toList());

        postRepository.saveAll(postList);

        MockHttpServletRequestBuilder content = MockMvcRequestBuilders.get("/posts/query_dsl?page=1&size=500" )
                .contentType(MediaType.APPLICATION_JSON);



        MvcResult result = mockMvc.perform(content)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.is(500)))
                .andExpect(jsonPath("$.[0].title").value(postList.get(postList.size()-1).getTitle()))
                .andDo(print())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();

        List<Post> list = objectMapper.readValue(contentAsString, new TypeReference<List<Post>>() {
        });
        for (Post post : list) {
            log.info("tit : {}", post.getTitle());
            log.info("content : {}", post.getContent());
        }
        assertThat(list.size()).isEqualTo(500);
    }


    @Test
    @DisplayName("????????? ????????? ????????????.")
    void edit() throws Exception{

        Post post1 = Post.builder()
                .title("???????????????.")
                .content("???????????????.")
                .build();
        postRepository.save(post1);

        //given
        PostEdit edit = PostEdit.builder()
                .title("?????? ???????????????.")
                .content("?????? ???????????????.")
                .build();

        String jsonData = objectMapper.writeValueAsString(edit);

        //when
        MockHttpServletRequestBuilder content = MockMvcRequestBuilders.post("/posts/edit/{postId}", post1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonData);

        //then
        mockMvc.perform(content)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(edit.getTitle()))
                .andExpect(jsonPath("$.content").value(edit.getContent()))
                .andDo(print());
    }

    @Test
    @DisplayName("????????? ????????? ????????????.")
    void delete() throws Exception{
        Post post = Post.builder().title("?????????").content("???????????????.")
                .build();

        postRepository.save(post);
        MockHttpServletRequestBuilder content = MockMvcRequestBuilders.delete("/posts/delete/{postId}", post.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(content)
                .andExpect(status().isOk() )
                .andExpect(content().string("ok"))
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ???????????? ?????? ??????.")
    void findById() throws Exception {

        //given
        Post post = Post.builder().title("?????????").content("???????????????.")
                .build();
        postRepository.save(post);

        //when
        MockHttpServletRequestBuilder content = MockMvcRequestBuilders.get("/posts/{postId}", post.getId())
                .contentType(MediaType.APPLICATION_JSON);

        //then
        mockMvc.perform(content)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(post.getTitle()))
                .andExpect(jsonPath("$.content").value(post.getContent()))
                .andDo(print());

    }

    @Test
    @DisplayName("?????? ???????????? ?????? ??????.")
    void findByIdException() throws Exception {

        //given
        Post post = Post.builder().title("?????????").content("???????????????.")
                .build();
        postRepository.save(post);

        //when
        MockHttpServletRequestBuilder content = MockMvcRequestBuilders.get("/posts/{postId}", 2L)
                .contentType(MediaType.APPLICATION_JSON);

        //then
        mockMvc.perform(content)
                .andExpect(status().isNotFound())
                .andDo(print());

    }

    @Test
    @DisplayName("????????? ?????? ?????? ?????????")
    void invalidException() throws Exception {

        //given
        PostCreate post = PostCreate.builder()
                .title("??????")
                .content("???????????????.")
                .build();

        String s = objectMapper.writeValueAsString(post);

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/posts/writer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(s))
                .andExpect(status().isBadRequest() )
                .andDo(print());
        //then
    }
}