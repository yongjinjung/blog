package com.jinblog.api.service;

import com.jinblog.api.domain.Post;
import com.jinblog.api.exception.PostNotFound;
import com.jinblog.api.repository.PostRepository;
import com.jinblog.api.request.PostCreate;
import com.jinblog.api.request.PostEdit;
import com.jinblog.api.request.PostSearch;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Slf4j
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void clean() {
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
    void get() throws Exception {
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

    @Test
    @DisplayName("글 리스트 조회")
    void postList() {
        Post post1 = Post.builder()
                .title("titl1")
                .content("cont1")
                .build();
        postRepository.save(post1);
        Post post2 = Post.builder()
                .title("titl2")
                .content("cont2")
                .build();
        postRepository.save(post2);

        List<Post> list = postService.postsList();


        assertThat(list.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("글 조회")
    void postList2() {

        //given
        List<Post> postList = IntStream.range(0, 30)
                .mapToObj(i -> Post.builder()
                        .title(" 제목 -" + i)
                        .content("내용 - " + i)
                        .build())
                .collect(Collectors.toList());

        postRepository.saveAll(postList);

        Pageable page = PageRequest.of(0, 5, Sort.Direction.DESC, "id");
        //when
        List<Post> list = postService.postsPage(page);

        //then
        assertThat(list.size()).isEqualTo(5);


    }


    @Test
    @DisplayName("QueryDSL 를 이용한다")
    void queryDslPostPage() {
        //given
        List<Post> postList = IntStream.range(0, 20)
                .mapToObj(i -> Post.builder()
                        .title(" 제목 -" + i)
                        .content("내용 - " + i)
                        .build())
                .collect(Collectors.toList());

        postRepository.saveAll(postList);

        PostSearch search = PostSearch.builder()
                .page(1)
                .build();

        //when
        List<Post> list = postService.queryDslPostPage(search);

        //then
        log.info("list : {}", list);

        assertThat(list.size()).isEqualTo(20);

    }


    @Test
    @DisplayName("내용을 수정한다.")
    void update() {
        //given

        Post post = new Post("제목입니다.", "내용입니다.");

        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("제목 수정입니다.")
                .content("내용 수정입니다.")
                .build();

        //when
        postService.edit(post.getId(), postEdit);
        Post post1 = postService.get(post.getId());

        //then
        assertThat(post1.getContent()).isEqualTo(postEdit.getContent());

    }


    @Test
    @DisplayName("컨텐츠 내용을 삭제한다.")
    void delete() {
        //given
        Post post = new Post("제목입니다.", "내용입니다.");
        postRepository.save(post);

        //when
        postService.delete(post.getId());

        //then
        assertThat(postRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("Exception Custom")
    void findByIdException(){
        //given
        Post post = new Post("제목입니다.", "내용입니다.");
        postRepository.save(post);

        //when
        assertThatThrownBy(()->{ postService.get(2L); })
                .isInstanceOf(PostNotFound.class)
                .hasMessage("존재하지 않는 글입니다.");
    }

    @Test
    @DisplayName("Exception Custom2 BDD 스타일")
    void findByIdException2(){
        //given
        Post post = new Post("제목입니다.", "내용입니다.");
        postRepository.save(post);

        //when
        Throwable thrown = catchThrowable(()->{ postService.get(2L); });

        //then
        assertThat(thrown)
                .isInstanceOf(PostNotFound.class)
                .hasMessage("존재하지 않는 글입니다.");
    }

}