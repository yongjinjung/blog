package com.jinblog.api.domain;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.Lob;


@Getter
public class PostEditor {

    private String title;
    private String content;

    @Builder
    public PostEditor(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
