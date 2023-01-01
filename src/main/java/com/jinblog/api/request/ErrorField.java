package com.jinblog.api.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ErrorField {

    private final String fieldName;
    private final String validMsg;
}
