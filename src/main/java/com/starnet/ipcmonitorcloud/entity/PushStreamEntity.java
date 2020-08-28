package com.starnet.ipcmonitorcloud.entity;

import lombok.Data;

/**
 * PushStreamInfo
 *
 * @author wzz
 * @date 2020/8/26 10:15
 **/
@Data
public class PushStreamEntity {
    private String pushAddr;
    private String app;
    private String name;
    /**
     * 正在监视这个推流的RTMP流的人数
     */
    private int rtmpObserverNum;
    /**
     * 正在监视这个推流的HTTP hls流的人数
     */
    private int hlsObserverNum;
}
