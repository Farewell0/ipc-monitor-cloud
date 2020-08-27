package com.starnet.ipcmonitorcloud.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * HttpResponse
 *
 * @author wzz
 * @date 2020/8/24 17:29
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HttpResponse<T> {

    private int status;

    private String message;

    private T data;

    public HttpResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpResponse(HttpStatus httpStatus) {
        this.status = httpStatus.getCode();
        this.message = httpStatus.getMessage();
    }

    public HttpResponse(HttpStatus httpStatus, String message) {
        this.status = httpStatus.getCode();
        this.message = message;
    }

    public HttpResponse(T data) {
        this.status = HttpStatus.OK.getCode();
        this.message = HttpStatus.OK.getMessage();
        this.data = data;
    }

}
