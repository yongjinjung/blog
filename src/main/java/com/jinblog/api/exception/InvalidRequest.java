package com.jinblog.api.exception;

import com.jinblog.api.request.ErrorField;

public class InvalidRequest extends JinBlogException{

    private static final String MESSAGE = "잘못된 요청입니다 .";
    private ErrorField errorField;


    public InvalidRequest() {
        super(MESSAGE);
    }

    public InvalidRequest(ErrorField errorField) {
        super(MESSAGE);
        this.errorField = errorField;
    }

    @Override
    public int getStatusCode() {
        return 400;
    }

    @Override
    public ErrorField getErrorField() {
        return errorField;
    }


}
