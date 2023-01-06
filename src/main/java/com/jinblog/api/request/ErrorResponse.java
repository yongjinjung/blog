package com.jinblog.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ErrorResponse {
    private final int code;
    private final String message;
    private List<ErrorField> errorFields;

    @Builder
    public ErrorResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public void addErrorField(ErrorField errorField) {
        if (errorFields == null) {
            errorFields = new ArrayList<>();
        }
        errorFields.add(errorField);
    }
}
