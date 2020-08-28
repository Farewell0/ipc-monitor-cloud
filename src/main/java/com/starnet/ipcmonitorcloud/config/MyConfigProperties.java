package com.starnet.ipcmonitorcloud.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * ActiveMqProperties
 *
 * @author wzz
 * @date 2020/8/21 17:52
 **/
@Data
@Component
@ConfigurationProperties(prefix = "myconfig")
public class MyConfigProperties {
    private String commandQueueName;
    private String pushToNginxStreamPrefix;
    private String nginxAccessLogPath;
    private String loginToken;
}
