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
    @DisplayName("게시글을 등록한다.")
    void write(){
        //given
        PostCreate postCreate = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        //when
        Post post = postService.write(postCreate);
        Post result = postService.get(post.getId());

        //then 동등성비교
        assertThat(post).usingRecursiveComparison().isEqualTo(result);


    }

    @Test

    @DisplayName("게시글을 조회한다.")
    void get() throws Exception{

        //given
        Post post = Post.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();
        postRepository.save(post);

        //when
        Post result = postService.get(post.getId());

        //then 동등성비교
        assertThat(result).usingRecursiveComparison().isEqualTo(post);
    }

    @Test
    @DisplayName("게시글을 리스트를 조회한다.")
    void postsList() throws Exception{

        //given
        List<Post> posts = IntStream.range(0, 30)
                .mapToObj(
                        i -> Post.builder()
                                .title("title-" + i)
                                .content("content-"+i)
                                .build()
                ).collect(Collectors.toList());

        postRepository.saveAll(posts);
        //when
        List<Post> list = postService.postsList();

        //then
        assertThat(list.size()).isEqualTo(30);
    }

    @Test
    @DisplayName("게시글을 Pageable 를 이용한 페이징 처리한다.")
    void postsPage(){
        //given
        List<Post> posts = IntStream.range(0, 500)
                .mapToObj(
                        i -> Post.builder()
                                .title("title-" + i)
                                .content("content-" + i)
                                .build()
                ).collect(Collectors.toList());
        postRepository.saveAll(posts);
        Pageable page  = PageRequest.of(0, 20, Sort.Direction.DESC, "id");
        //when
        List<Post> list = postService.postsPage(page);

        //then
        assertThat(list.get(0).getTitle()).isEqualTo(posts.get(posts.size()-1).getTitle());
        assertThat(list.size()).isEqualTo(20);
        assertThat(postRepository.count()).isEqualTo(500);

    }

    @Test
    @DisplayName("게시글을 쿼리디에스엘로 페이징 처리한다.")
    void queryDslPostPage(){
        //given
        List<Post> posts = IntStream.range(0, 500)
                .mapToObj(
                        i -> Post.builder()
                                .title("title-" + i)
                                .content("content-" + i)
                                .build()
                ).collect(Collectors.toList());
        postRepository.saveAll(posts);
        PostSearch page = PostSearch.builder()
                .page(1)
                .size(400)
                .build();
        //when
        List<Post> list = postService.queryDslPostPage(page);

        //then
        log.info("list : {}", list);
        assertThat(list.get(0).getTitle()).isEqualTo(posts.get(posts.size()-1).getTitle());
        assertThat(list.size()).isEqualTo(400);
        assertThat(postRepository.count()).isEqualTo(500);

    }

    @Test
    @DisplayName("게시글을 수정한다.")
    void edit()throws Exception{
        //given
        Post post = Post.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("제목 수정입니다.")
                .content("내용 수정입니다.")
                .build();

        //when
        postService.edit(post.getId(), postEdit);
        Post result = postService.get(post.getId());

        //then 동등성비교
        assertThat(result.getTitle()).isEqualTo(postEdit.getTitle());
        assertThat(result.getContent()).isEqualTo(postEdit.getContent());
    }

    @Test
    @DisplayName("게시글을 삭제한다.")
    void delete(){
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