package com.starnet.ipcmonitorcloud.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.starnet.ipcmonitorcloud.config.MyConfigProperties;
import com.starnet.ipcmonitorcloud.exception.MqException;
import com.starnet.ipcmonitorcloud.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Producer
 *
 * @author wzz
 * @date 2020/8/24 13:41
 **/
@Slf4j
@Component
public class MqProducer {
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    MyConfigProperties myConfigProperties;

    public <T> T sendAndReceive(MqRequest mqRequest, Class<T> clazz) {
        String queueName = myConfigProperties.getCommandQueueName();
        String messageJson = null;
        try {
            messageJson = JsonUtils.toJson(mqRequest);
            String reply = sendAndReceive(queueName, messageJson);
            log.info("Receive local server reply: {}", reply);
            if (null == reply) {
                throw new MqException(MqStatus.LOCAL_NO_RESPONSE);
            }
            MqResponse response = JsonUtils.fromJson(reply, MqResponse.class);
            if (null == response) {
                throw new MqException(MqStatus.LOCAL_RESPONSE_ERROR);
            }
            return JsonUtils.fromJson(reply, clazz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String sendAndReceive(String queueName, String message) {
        // TODO 添加ActiveMQ超时设置
        return jmsMessagingTemplate.convertSendAndReceive(queueName, message, String.class);
    }

}
