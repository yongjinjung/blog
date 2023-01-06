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

    @Test
    @DisplayName("/posts 요청시 Hello World 를 출력한다.")
    void post() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.POST, "/posts").param("name", "정용진"))
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
    void validTest2() throws Exception {
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
    void validTest3() throws Exception {
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
    void validTest4() throws Exception {
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
    void postWriter() throws Exception {
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
    void postFindAll() throws Exception {
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
    void postDataToJson() throws Exception {
        //given
        ObjectMapper mapper = new ObjectMapper();
        String jsonData = mapper.writeValueAsString(new PostCreate("제목입니다.", "내용입니다."));

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
    @DisplayName("object 데이터를 json 형태로 만들어 전송한다.")
    void postDataBuild() throws Exception {
        //given

        PostCreate postCreate = PostCreate
                .builder()
                .title("제목입니다.")
                .content("내용입니다.")
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
    @DisplayName("글 1개 조회")
    void get() throws Exception {
        //given
        PostCreate requestData = PostCreate.builder().title("제목입니다.").content("내용입니다.").build();
        String s = objectMapper.writeValueAsString(requestData);
        MockHttpServletRequestBuilder content = MockMvcRequestBuilders.post("/posts/writer").contentType(MediaType.APPLICATION_JSON).content(s);
        mockMvc.perform(content)
                .andExpect(status().isOk())
                .andDo(print());

        MockHttpServletRequestBuilder findById = MockMvcRequestBuilders.get("/posts/{postId}", 1L).contentType(MediaType.APPLICATION_JSON);
        //when
        MvcResult mvcResult = mockMvc.perform(findById)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("제목입니다."))
                .andDo(print())
                .andReturn();


        String jsonData = mvcResult.getResponse().getContentAsString();

        Post post = objectMapper.readValue(jsonData, Post.class);

        //then
        assertThat(post.getContent()).isEqualTo("내용입니다.");
    }


    @Test
    @DisplayName("제목을 10글자만 가지고 온다")
    void testSubstr() throws Exception {
        //given
        PostCreate saveData = PostCreate.builder()
                .title("123456789010000")
                .content("숫자 놀이입니다.")
                .build();
        String s = objectMapper.writeValueAsString(saveData);

        MockHttpServletRequestBuilder content = null;
        content = MockMvcRequestBuilders.post("/posts/writer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(s);

        mockMvc.perform(content)
                .andDo(print());

        content = MockMvcRequestBuilders.get("/posts/{postId}", 1L)
                .contentType(MediaType.APPLICATION_JSON);

        //when
        MvcResult result = mockMvc.perform(content).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String resultData = result.getResponse().getContentAsString();
        Post post = objectMapper.readValue(resultData, Post.class);
        //then
        assertThat(post.getTitle()).isEqualTo("1234567890");


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
                        .title(" 제목 -" + i)
                        .content("내용 - " + i)
                        .build())
                .collect(Collectors.toList());

        postRepository.saveAll(postList);

        MockHttpServletRequestBuilder content = MockMvcRequestBuilders.get("/posts/list/{pageNum}", 1)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(content)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.is(5)))
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
    void query_dsl() throws Exception{
        //given
        List<Post> postList = IntStream.range(0, 1000)
                .mapToObj(i -> Post.builder()
                        .title("제목 -" + i)
                        .content("내용 - " + i)
                        .build())
                .collect(Collectors.toList());

        postRepository.saveAll(postList);

        MockHttpServletRequestBuilder content = MockMvcRequestBuilders.get("/posts/query_dsl?page=1&size=2000" )
                .contentType(MediaType.APPLICATION_JSON);



        MvcResult result = mockMvc.perform(content)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.is(500)))
                .andExpect(jsonPath("$.[0].title").value("제목 -499"))
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
    @DisplayName("컨텐츠 내용을 수정한다.")
    void edit() throws Exception{

        Post post1 = Post.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();
       postRepository.save(post1);

        //given
        PostEdit edit = PostEdit.builder()
                .title("제목 수정입니다.")
                .content("내용 수정입니다.")
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
    @DisplayName("컨텐츠 내용을 수정한다.")
    void delete() throws Exception{
        Post post = Post.builder().title("용진짱").content("내용입니다.")
                .build();

        postRepository.save(post);
        MockHttpServletRequestBuilder content = MockMvcRequestBuilders.delete("/posts/delete/{postId}", 1L)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(content)
                .andExpect(status().isOk() )
                .andExpect(content().string("ok"))
                .andDo(print());
    }

    @Test
    @DisplayName("특정 게시글을 조회 한다.")
    void findById() throws Exception {

        //given
        Post post = Post.builder().title("용진짱").content("내용입니다.")
                .build();
        postRepository.save(post);

        //when
        MockHttpServletRequestBuilder content = MockMvcRequestBuilders.get("/posts/{postId}", 1L)
                .contentType(MediaType.APPLICATION_JSON);

        //then
        mockMvc.perform(content)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(post.getTitle()))
                .andExpect(jsonPath("$.content").value(post.getContent()))
                .andDo(print());

    }

    @Test
    @DisplayName("특정 게시글을 조회 한다.")
    void findByIdException() throws Exception {

        //given
        Post post = Post.builder().title("용진짱").content("내용입니다.")
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
    @DisplayName("정책에 의한 단어 필터링")
    void invalidException() throws Exception {
        
        //given
        PostCreate post = PostCreate.builder()
                .title("바보")
                .content("내용입니다.")
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