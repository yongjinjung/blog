package com.jinblog.api.repository;

import com.jinblog.api.domain.Post;
import com.jinblog.api.request.PostSearch;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> postsPage(PostSearch postSearch);
}
