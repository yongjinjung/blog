package com.jinblog.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jinblog.api.domain.Post;
import com.jinblog.api.repository.PostRepository;
import com.jinblog.api.request.PostCreate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.snippet.Attributes.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.youngjin.com", uriPort = 443 )
@ExtendWith(RestDocumentationExtension.class)
public class PostControllerDocTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /*@BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }*/

    @Test
    @DisplayName("test")
    void test() throws Exception {

        Post post1 = Post.builder()
                .title("???????????????.")
                .content("???????????????.")
                .build();
        postRepository.save(post1);

        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/posts/{postId}", 1L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("post-inquiry",
                        RequestDocumentation.pathParameters(
                                RequestDocumentation.parameterWithName("postId").description("??? ID")
                        ),
                        PayloadDocumentation.responseFields(
                                PayloadDocumentation.fieldWithPath("id").description("????????? ID"),
                                PayloadDocumentation.fieldWithPath("title").description("??????"),
                                PayloadDocumentation.fieldWithPath("content").description("?????????")
                        )
                        ));
    }

    @Test
    @DisplayName("??? ??????")
    void writer() throws Exception {
        PostCreate postCreate = PostCreate.builder()
                .title("?????????")
                .content("?????????")
                .build();

        String jsonData = objectMapper.writeValueAsString(postCreate);

        this.mockMvc.perform(RestDocumentationRequestBuilders.post("/posts/writer")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonData)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("post-create",
                        PayloadDocumentation.requestFields(
                                PayloadDocumentation.fieldWithPath("title").description("??????")
                                        .attributes(key("constraint").value("????????? ????????? ?????????.")),
                                PayloadDocumentation.fieldWithPath("content").description("??????").optional()
                        )
                ));
    }


    private Post initSaveData(){
        Post post = Post.builder()
                .title("??? ??????")
                .content("??? ??????")
                .build();

       return postRepository.save(post);
    }
}
