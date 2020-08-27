package com.starnet.ipcmonitorcloud.exception;

import com.starnet.ipcmonitorcloud.mq.MqStatus;

/**
 * MqException
 *
 * @author wzz
 * @date 2020/8/21 15:44
 **/
public class MqException extends RuntimeException {
    private MqStatus mqStatus;

    public MqException() {
        super();
    }

    public MqException(MqStatus status) {
        super(status.getMsg());
        this.mqStatus = status;
    }

    public MqException(MqStatus status, String message) {
        super(message);
        this.mqStatus = status;
    }

    public MqException(String message) {
        super(message);
    }

    public MqStatus getMqStatus() {
        return mqStatus;
    }

}
