package com.desofme.jwtauth.controller;

import com.desofme.jwtauth.dto.response.ResponseStatus;
import com.desofme.jwtauth.dto.response.SingleStatus;
import com.desofme.jwtauth.exception.CustomException;
import com.desofme.jwtauth.exception.StatusCode;
import com.desofme.jwtauth.exception.StatusMessage;
import io.jsonwebtoken.JwtException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(CustomException.class)
    public SingleStatus<ResponseStatus> handleCustomException(CustomException ex){
        ResponseStatus responseStatus = ResponseStatus.builder()
                .message(ex.getMessage())
                .code(ex.getCode())
                .build();
        return SingleStatus.<ResponseStatus>builder()
                .status(responseStatus)
                .build();
    }

    @ExceptionHandler(Exception.class)
    public SingleStatus<ResponseStatus> handleException(Exception ex){
        ResponseStatus responseStatus = ResponseStatus.builder()
                .message(ex.getMessage())
                .code(StatusCode.INTERNAL_SERVER_ERROR)
                .build();
        return SingleStatus.<ResponseStatus>builder()
                .status(responseStatus)
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public SingleStatus<ResponseStatus> handleValidationException(BindingResult bindingResult){
        List<String> error = bindingResult.getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
        ResponseStatus responseStatus = ResponseStatus.builder()
                .message(error.toString())
                .code(StatusCode.VALIDATION_ERROR)
                .build();
        return SingleStatus.<ResponseStatus>builder()
                .status(responseStatus)
                .build();
    }

    @ExceptionHandler(JwtException.class)
    public SingleStatus<ResponseStatus> handleExpiredJwtException(){
        ResponseStatus responseStatus = ResponseStatus.builder()
                .message(StatusMessage.JWT_HAS_EXPIRED)
                .code(StatusCode.JWT_HAS_EXPIRED)
                .build();
        return SingleStatus.<ResponseStatus>builder()
                .status(responseStatus)
                .build();
    }

    @ExceptionHandler(SocketTimeoutException.class)
    public SingleStatus<ResponseStatus> handleSocketTimeoutException(){
        ResponseStatus responseStatus = ResponseStatus.builder()
                .message(StatusMessage.EMAIL_HAS_NOT_SENT)
                .code(StatusCode.EMAIL_HAS_NOT_SENT)
                .build();
        return SingleStatus.<ResponseStatus>builder()
                .status(responseStatus)
                .build();
    }
}
