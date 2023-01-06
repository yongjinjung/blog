package com.jinblog.api.exception;

import com.jinblog.api.request.ErrorField;

public abstract class JinBlogException extends RuntimeException{

    public JinBlogException(String message) {
        super(message);
    }

    public JinBlogException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract int getStatusCode();

    public abstract ErrorField getErrorField();
}
