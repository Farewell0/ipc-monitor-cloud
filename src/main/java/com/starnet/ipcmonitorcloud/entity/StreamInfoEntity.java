package com.starnet.ipcmonitorcloud.entity;

import lombok.Data;

/**
 * StreamInfoCache
 *
 * @author wzz
 * @date 2020/8/25 17:57
 **/
@Data
public class StreamInfoEntity {
    private IpcEntity ipcEntity;
    private PushStreamEntity pushStreamEntity;
    /**
     * key为推流的 "app:name", 因为一个拉流可以推到不同的流，虽然我们的做法是一次拉流只推一次
     * 但是保留这个map
     */
//    private Map<String, PushStreamInfo> pushStreamInfoMap = new HashMap<>();

}
