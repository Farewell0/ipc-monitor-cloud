package com.starnet.ipcmonitorcloud.cache.service;

import com.starnet.ipcmonitorcloud.cache.RedisService;
import com.starnet.ipcmonitorcloud.cache.RedisServiceApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * PushStreamAuthCacheService
 *
 * @author wzz
 * @date 2020/8/24 19:19
 **/
@Service
public class PushStreamAuthCacheService {
    @Autowired
    private RedisService redisService;

    private RedisServiceApi getApi() {
        return redisService.getRedisServiceApiForMonitor();
    }

    private static final String PUSH_STREAM_AUTH_TOKEN_HEADER = "push-stream-auth-token:";

    public void setPushToken(String token) {
        getApi().set(PUSH_STREAM_AUTH_TOKEN_HEADER, token);
    }

    public void setPushToken(String token, long expires) {
        getApi().set(PUSH_STREAM_AUTH_TOKEN_HEADER, token, expires);
    }

    public String getPushToken() {
        return getApi().get(PUSH_STREAM_AUTH_TOKEN_HEADER, String.class);
    }
}
