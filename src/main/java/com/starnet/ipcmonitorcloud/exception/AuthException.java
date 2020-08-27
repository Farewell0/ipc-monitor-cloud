package com.starnet.ipcmonitorcloud.exception;

/**
 * AuthException
 *
 * @author wzz
 * @date 2020/8/25 13:54
 **/
public class AuthException extends RuntimeException{
    public AuthException() {
        super();
    }

    public AuthException(String message) {
        super(message);
    }
}
