package com.starnet.ipcmonitorcloud.web.response;

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

    private int code;

    private String message;

    private T data;

    public HttpResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public HttpResponse(HttpStatus httpStatus) {
        this.code = httpStatus.getCode();
        this.message = httpStatus.getMessage();
    }

    public HttpResponse(HttpStatus httpStatus, String message) {
        this.code = httpStatus.getCode();
        this.message = message;
    }

    public HttpResponse(T data) {
        this.code = HttpStatus.OK.getCode();
        this.message = HttpStatus.OK.getMessage();
        this.data = data;
    }

}
