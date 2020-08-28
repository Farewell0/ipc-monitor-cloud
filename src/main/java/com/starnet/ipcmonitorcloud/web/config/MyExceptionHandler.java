package com.starnet.ipcmonitorcloud.web.config;

import com.starnet.ipcmonitorcloud.exception.AuthException;
import com.starnet.ipcmonitorcloud.web.response.HttpResponse;
import com.starnet.ipcmonitorcloud.web.response.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * MyExceptionHandler
 *
 * @author wzz
 * @date 2020/8/25 13:52
 **/
@Slf4j
@RestControllerAdvice
public class MyExceptionHandler {

    @ExceptionHandler(Exception.class)
    public HttpResponse apiExceptionHandler(Exception e) {
        if (e instanceof AuthException) {
            return new HttpResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
        return new HttpResponse();
    }
}
