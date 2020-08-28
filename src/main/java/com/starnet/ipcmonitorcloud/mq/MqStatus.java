package com.starnet.ipcmonitorcloud.mq;

/**
 * MqStatus
 *
 * @author wzz
 * @date 2020/8/24 15:02
 **/
public enum MqStatus {
    /**
     * MQ返回的正常回应状态
     */
    OK(200, "OK"),
    LOCAL_RESPONSE_ERROR(501, "Receive local server response error"),
    LOCAL_NO_RESPONSE(502, "Receive local server response null"),
    GET_IPC_LIST_FAIL(502, "Get ipc list fail"),
    /**
     * 未知错误
     */
    ERROR_UNKNOWN(100000, "Unknown Error");

    private int code;
    private String msg;
    MqStatus(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "MqStatus{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }

    public static MqStatus valueOf(int code) {
        for(MqStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return ERROR_UNKNOWN;
    }
}
