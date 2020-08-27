package com.starnet.ipcmonitorcloud.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;

import java.time.Duration;

/**
 * RedisConfig
 *
 * @author wzz
 * @date 2020/8/24 18:50
 **/
@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.lettuce.pool.max-active:8}")
    private int maxActive;

    @Value("${spring.redis.lettuce.pool.max-wait:-1ms}")
    private Duration maxWait;

    @Value("${spring.redis.lettuce.shutdown-timeout:100ms}")
    private Duration shutdownTimeout;

    @Value("${spring.redis.lettuce.pool.max-idle:8}")
    private int maxIdle;

    @Value("${spring.redis.lettuce.pool.min-idle:0}")
    private int minIdle;

    @Value("${spring.redis.database.monitor:0}")
    private int databaseForMonitor;

    @Bean("redisConnectionFactoryForMonitor")
    public RedisConnectionFactory redisConnectionFactoryForMonitor() {
        return buildRedisConnectionFactory(databaseForMonitor);
    }

    private RedisConnectionFactory buildRedisConnectionFactory(int database) {
        GenericObjectPoolConfig poolConfig = buildPoolConfig();
        LettucePoolingClientConfiguration clientConfiguration =
                LettucePoolingClientConfiguration.builder().poolConfig(poolConfig).build();
        RedisStandaloneConfiguration configuration =
                new RedisStandaloneConfiguration(host, port);
        configuration.setPassword(RedisPassword.of(password));
        configuration.setDatabase(database);
        return new LettuceConnectionFactory(configuration, clientConfiguration);
    }

    private GenericObjectPoolConfig buildPoolConfig() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(maxActive);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        if (maxWait != null) {
            config.setMaxWaitMillis(maxWait.toMillis());
        }
        return config;
    }


}
