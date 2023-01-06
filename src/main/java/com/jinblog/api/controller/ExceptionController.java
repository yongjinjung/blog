package com.jinblog.api.controller;

import com.jinblog.api.request.ErrorField;
import com.jinblog.api.request.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class ExceptionController {


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> exceptionHandler(Exception e){
        log.error("exceptionHandler ");

        return ResponseEntity.badRequest().body(new ErrorResponse(400, e.getMessage()));
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> methodArgumentNotValidException(MethodArgumentNotValidException e){

        ErrorResponse errorResponse = new ErrorResponse(400, "잘못된 요청입니다.");

        if(e.hasErrors()){
            e.getFieldErrors();
            for (FieldError fieldError : e.getFieldErrors()) {
                errorResponse.addErrorField(new ErrorField(fieldError.getField(), fieldError.getDefaultMessage()));
            }
        }

     /*   FieldError fieldError = e.getFieldError();
        String field = fieldError.getField();
        String message = fieldError.getDefaultMessage();*/

//        Map<String, Object> response = new HashMap<>();
//        response.put("fieldId", field);
//        response.put("message", message);
        return ResponseEntity.badRequest().body(errorResponse);
    }
}
