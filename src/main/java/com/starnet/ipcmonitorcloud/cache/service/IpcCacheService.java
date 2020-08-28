package com.starnet.ipcmonitorcloud.cache.service;

import com.starnet.ipcmonitorcloud.cache.RedisService;
import com.starnet.ipcmonitorcloud.cache.RedisServiceApi;
import com.starnet.ipcmonitorcloud.entity.IpcEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * IpcCacheService
 *
 * @author wzzfarewell
 * @date 2020/8/28 上午10:33
 **/
@Service
public class IpcCacheService {
    @Autowired
    private RedisService redisService;

    public static final String IPC_LIST_CACHE_HEADER = "ipc-cache:";

    private RedisServiceApi getApi() {
        return redisService.getRedisServiceApiForMonitor();
    }

    public void setIpcCache(IpcEntity ipcCache) {
        getApi().set(getKey(String.valueOf(ipcCache.getId())), ipcCache);
    }

    public IpcEntity getIpcCache(String ipcId) {
        return getApi().get(getKey(ipcId), IpcEntity.class);
    }

    private String getKey(String ipcId) {
        return IPC_LIST_CACHE_HEADER + ipcId;
    }

    public List<IpcEntity> getAllIpcCache() {
        Set<Object> keys = getApi().getKeys(getKey("*"));
        List<IpcEntity> ipcEntityList = new ArrayList<>();
        keys.forEach(key -> ipcEntityList.add(getApi().get(key, IpcEntity.class)));
        return ipcEntityList;
    }
}
