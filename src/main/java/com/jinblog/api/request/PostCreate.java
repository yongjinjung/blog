package com.jinblog.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jinblog.api.exception.InvalidRequest;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PostCreate {

    @Builder
    public PostCreate(String title, String content) {
        this.title = title;
        this.content = content;
    }

    @NotBlank(message = "타이틀을 입력해주세요.")
    private String title;

    @NotBlank(message = "컨텐츠를 입력해주세요.")
    private String content;


    public void validate(){
        if(title.contains("바보")){
            ErrorField field = new ErrorField("title", "제목에 바보를 포함할 수 없습니다.");
            throw new InvalidRequest(field);
        }
    }



}
