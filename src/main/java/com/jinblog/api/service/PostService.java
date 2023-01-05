package com.jinblog.api.service;

import com.jinblog.api.domain.Post;
import com.jinblog.api.domain.PostEditor;
import com.jinblog.api.repository.PostRepository;
import com.jinblog.api.request.PostCreate;
import com.jinblog.api.request.PostEdit;
import com.jinblog.api.request.PostSearch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public void write(PostCreate postCreate){

        Post post = new Post(postCreate.getTitle(), postCreate.getContent());
        postRepository.save(post);
    }

    public Post get(Long id){
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 글입니다."));
        return post;
    }

    public List<Post> postsList(){
        return postRepository.findAll();
    }

    public List<Post> postsPage(Pageable of){
        return postRepository.findAll(of).stream().map( post -> Post.builder().title(post.getTitle()).content(post.getContent()).build()).collect(Collectors.toList());
    }

    public List<Post> queryDslPostPage(PostSearch postSearch){
       return postRepository.postsPage(postSearch)
               .stream()
               .map(Post::new)
               .collect(Collectors.toList());
    }


    @Transactional
    public void edit(Long id, PostEdit postEdit){
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 글입니다."));
        PostEditor.PostEditorBuilder postEditorBuilder = post.toEditor();

        PostEditor postEditor = postEditorBuilder
                .title(postEdit.getTitle())
                .content(postEdit.getContent())
                .build();

        post.edit(postEditor);
    }

}
