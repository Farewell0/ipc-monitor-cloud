package com.starnet.ipcmonitorcloud.web.model;

/**
 * HttpStatus
 *
 * @author wzz
 * @date 2020/8/25 9:33
 **/
public enum HttpStatus{
    /**
     * 200状态码
     */
    OK(200, "OK"),
    UNAUTHORIZED(401, "Unauthorized! You have no permission to access"),
    REQUEST_PARAMS_ERROR(402, "Request params error"),
    NOT_FOUND(404, "Not found"),
    SERVER_ERROR(500, "Server error");

    private int code;
    private String message;
    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
