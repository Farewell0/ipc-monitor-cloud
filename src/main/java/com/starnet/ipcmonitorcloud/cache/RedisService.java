package com.starnet.ipcmonitorcloud.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starnet.ipcmonitorcloud.utils.JsonUtils;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * RedisService
 *
 * @author wzz
 * @date 2020/8/24 19:07
 **/
@Service
public class RedisService {

    @Resource(name = "redisConnectionFactoryForMonitor")
    private RedisConnectionFactory factoryForMonitor;

    public RedisServiceApi getRedisServiceApiForMonitor() {
        return redisServiceApiForMonitor;
    }

    private RedisServiceApi redisServiceApiForMonitor;

    @PostConstruct
    public void initRedisService() {
        redisServiceApiForMonitor = new RedisServiceApiImpl(factoryForMonitor);
    }

    public static class RedisServiceApiImpl implements RedisServiceApi {
        private RedisTemplate redisTemplate;

        public RedisServiceApiImpl(RedisConnectionFactory factory) {
            this.redisTemplate = buildRedisTemplate(factory);
        }

        public RedisTemplate buildRedisTemplate(RedisConnectionFactory factory) {
            RedisTemplate redisTemplate = new RedisTemplate();
            redisTemplate.setConnectionFactory(factory);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);
            serializer.setObjectMapper(objectMapper);
            redisTemplate.setValueSerializer(serializer);
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisTemplate.afterPropertiesSet();
            return redisTemplate;
        }

        @Override
        public boolean set(Object key, Object value) {
            return set(key, value, 24 * 3600L);
        }

        @Override
        public boolean set(Object key, Object value, long expireTime) {
            if (value == null) {
                return false;
            }
            try {
                ValueOperations operations = redisTemplate.opsForValue();
                operations.set(key, JsonUtils.toJson(value));
                if (expireTime > 0) {
                    redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        public <T> T get(Object key, Class<T> clazz) {
            try {
                ValueOperations operations = redisTemplate.opsForValue();
                Object result = operations.get(key);
                return JsonUtils.fromJson((String)result, clazz);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public boolean exist(Object key) {
            try {
                return redisTemplate.hasKey(key);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public boolean remove(Object key) {
            return redisTemplate.delete(key);
        }
    }
}
