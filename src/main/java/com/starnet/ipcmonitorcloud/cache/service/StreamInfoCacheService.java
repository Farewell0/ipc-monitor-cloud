package com.starnet.ipcmonitorcloud.cache.service;

import com.starnet.ipcmonitorcloud.cache.RedisService;
import com.starnet.ipcmonitorcloud.cache.RedisServiceApi;
import com.starnet.ipcmonitorcloud.entity.StreamInfoEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    public static final String STREAM_CACHE_HEADER = "stream-cache:";

    private RedisServiceApi getApi() {
        return redisService.getRedisServiceApiForMonitor();
    }

    private String getKey(String id) {
        return STREAM_CACHE_HEADER + id;
    }

    public void setStreamInfo(StreamInfoEntity streamInfo) {
        setStreamInfo(streamInfo, 24 * 3600L);
    }

    public void setStreamInfo(StreamInfoEntity streamInfo, long expires) {
        getApi().set(getKey(String.valueOf(streamInfo.getIpcEntity().getId())), streamInfo, expires);
    }

    public StreamInfoEntity getStreamInfo(String ipcId) {
        return getApi().get(getKey(ipcId), StreamInfoEntity.class);
    }

    public boolean existStreamInfo(String ipcId) {
        return getApi().exist(getKey(ipcId));
    }

    public void removeStreamInfo(String ipcId) {
        getApi().remove(getKey(ipcId));
    }

    public List<StreamInfoEntity> getAllStreamCache() {
        Set<Object> keys = getApi().getKeys(getKey("*"));
        List<StreamInfoEntity> streamInfoCaches = new ArrayList<>();
        keys.forEach(key -> streamInfoCaches.add(getApi().get(key, StreamInfoEntity.class)));
        return streamInfoCaches;
    }
}
