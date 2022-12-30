package com.jinblog.api.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PostCreate {


    @NotBlank
    private String title;

    @NotBlank
    private String content;

}
