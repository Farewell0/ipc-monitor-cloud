package com.starnet.ipcmonitorcloud.cache.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * StreamInfoCache
 *
 * @author wzz
 * @date 2020/8/25 17:57
 **/
@Data
public class StreamInfoCache {
    private String pullAddr;
    private Map<String, PushStreamInfo> pushStreamInfoMap = new HashMap<>();

    public String getKey() {
        return this.getPullAddr();
    }
}
