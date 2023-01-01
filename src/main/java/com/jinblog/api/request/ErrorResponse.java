package com.jinblog.api.request;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class ErrorResponse {
    private final int code;
    private final String message;

    private List<ErrorField> errorFields;

    public void addErrorField(ErrorField errorField){
        if(errorFields == null){
            errorFields = new ArrayList<>();
        }
        errorFields.add(errorField);
    }
}
