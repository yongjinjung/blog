package com.jinblog.api.exception;

import com.jinblog.api.request.ErrorField;

public class PostNotFound extends JinBlogException{

    private static final String MESSAGE = "존재하지 않는 글입니다.";

    public PostNotFound() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }

    @Override
    public ErrorField getErrorField() {
        return null;
    }
}
