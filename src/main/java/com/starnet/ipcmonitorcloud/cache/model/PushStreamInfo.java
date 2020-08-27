package com.starnet.ipcmonitorcloud.cache.model;

import lombok.Data;

/**
 * PushStreamInfo
 *
 * @author wzz
 * @date 2020/8/26 10:15
 **/
@Data
public class PushStreamInfo {
    private String pushAddr;
    private String app;
    private String name;
    /**
     * 正在监视这个推流的人数
     */
    private int observerNum;
}
