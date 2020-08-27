package com.starnet.ipcmonitorcloud.cache;

/**
 * RedisServiceApi
 *
 * @author wzz
 * @date 2020/8/24 19:04
 **/
public interface RedisServiceApi {

    boolean set(Object key, Object value);

    boolean set(Object key, Object value, long expireTime);

    <T> T get(Object key, Class<T> clazz);

    boolean exist(Object key);

    boolean remove(Object key);
}
