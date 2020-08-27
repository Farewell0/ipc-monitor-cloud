package com.starnet.ipcmonitorcloud.mq;

import lombok.Data;

/**
 * MqResponse
 *
 * @author wzz
 * @date 2020/8/24 13:42
 **/
@Data
public class MqResponse {
    private int status;
    private String message;

    public MqResponse() {

    }

    public MqResponse(MqStatus mqStatus) {
        this.status = mqStatus.getCode();
        this.message = mqStatus.getMsg();
    }

    public MqResponse(MqStatus mqStatus, String message) {
        this.status = mqStatus.getCode();
        this.message = message;
    }
}
