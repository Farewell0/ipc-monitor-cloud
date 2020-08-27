package com.starnet.ipcmonitorcloud.cache.service;

import com.starnet.ipcmonitorcloud.cache.RedisService;
import com.starnet.ipcmonitorcloud.cache.RedisServiceApi;
import com.starnet.ipcmonitorcloud.cache.model.StreamInfoCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * StreamInfoCacheService
 *
 * @author wzz
 * @date 2020/8/26 9:52
 **/
@Slf4j
@Service
public class StreamInfoCacheService {
    @Autowired
    private RedisService redisService;

    private RedisServiceApi getApi() {
        return redisService.getRedisServiceApiForMonitor();
    }

    public void setStreamInfo(StreamInfoCache streamInfo) {
        setStreamInfo(streamInfo, 24 * 3600L);
    }

    public void setStreamInfo(StreamInfoCache streamInfo, long expires) {
        getApi().set(streamInfo.getKey(), streamInfo, expires);
    }

    public StreamInfoCache getStreamInfo(String key) {
        return getApi().get(key, StreamInfoCache.class);
    }

    public boolean existStreamInfo(String key) {
        return getApi().exist(key);
    }

    public void removeStreamInfo(String key) {
        getApi().remove(key);
    }
}
