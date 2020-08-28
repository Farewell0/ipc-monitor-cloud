package com.starnet.ipcmonitorcloud.web.response;

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
    SERVER_ERROR(500, "Server error"),
    PUSH_STREAM_FAIL(501, "Push stream fail"),
    GET_IPC_LIST_FAIL(502, "Get ipc list fail"),
    GET_PUSH_STREAM_LIST_FAIL(503, "Get push stream list fail");

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
