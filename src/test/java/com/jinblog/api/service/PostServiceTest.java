package com.jinblog.api.service;

import com.jinblog.api.domain.Post;
import com.jinblog.api.repository.PostRepository;
import com.jinblog.api.request.PostCreate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void clean(){
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("글 작성")
    void write() {

        //given
        PostCreate postCreate = PostCreate.builder().title("제목입니다.").content("내용입니다.").build();

        //when
        PostService postService = new PostService(postRepository);
        postService.write(postCreate);

        //then
        List<Post> list = postRepository.findAll();
        Post post = list.get(0);
        assertThat(post.getTitle()).isEqualTo("제목입니다.");
        assertThat(post.getContent()).isEqualTo("내용입니다.");

    }

    @Test
    @DisplayName("특정 글 목록을 조회한다.")
    void get() throws Exception{
        //given
        Post post = Post.builder()
                .title("foot")
                .content("bar")
                .build();

        Long id = 1L;
        postRepository.save(post);

        //when
        Post resultPost = postService.get(id);

        //then
        assertThat(resultPost).isNotNull();
    }
}