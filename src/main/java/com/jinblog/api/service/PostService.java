package com.jinblog.api.service;

import com.jinblog.api.domain.Post;
import com.jinblog.api.repository.PostRepository;
import com.jinblog.api.request.PostCreate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

}
