package com.jinblog.api.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PostSearch {

    @Builder.Default
    private Integer page = 1;
    @Builder.Default
    private Integer size = 10;

    public long getOffset(){
        return (Math.max(page, 1) -1) * Math.min(size, 500);
    }

}



