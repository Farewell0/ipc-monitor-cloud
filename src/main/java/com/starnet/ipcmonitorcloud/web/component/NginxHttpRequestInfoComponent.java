package com.starnet.ipcmonitorcloud.web.component;

import com.google.common.collect.Lists;
import com.starnet.ipcmonitorcloud.cache.service.StreamInfoCacheService;
import com.starnet.ipcmonitorcloud.config.MyConfigProperties;
import com.starnet.ipcmonitorcloud.entity.StreamInfoEntity;
import com.starnet.ipcmonitorcloud.utils.FileUtils;
import com.starnet.ipcmonitorcloud.entity.NginxHttpRequestInfoEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * NginxLogComponent
 *
 * @author wzzfarewell
 * @date 2020/8/27 上午10:47
 **/
@Slf4j
@Component
public class NginxHttpRequestInfoComponent {
    @Autowired
    private MyConfigProperties myConfigProperties;
    @Autowired
    private StreamInfoCacheService streamInfoCacheService;

    @Scheduled(initialDelay = 5000, fixedDelayString = "60000")
    public void syncHttpRequestInfo() {
        Map<String, NginxHttpRequestInfoEntity> requestMap = getRequestFromLog();
        List<StreamInfoEntity> streamInfoEntities = streamInfoCacheService.getAllStreamCache();
        for(StreamInfoEntity streamInfoEntity : streamInfoEntities) {
            // 先将之前的数量清空
            streamInfoEntity.getPushStreamEntity().setHlsObserverNum(0);
            String requestMapKey = "/" + streamInfoEntity.getPushStreamEntity().getApp() + "/" +
                    streamInfoEntity.getPushStreamEntity().getName();
            requestMap.forEach((k, v) -> {
                if (k.contains(requestMapKey)) {
                    int pre = streamInfoEntity.getPushStreamEntity().getHlsObserverNum();
                    streamInfoEntity.getPushStreamEntity().setHlsObserverNum(pre + 1);
                }
            });
        }
        log.info("sync http request info : {}", streamInfoEntities);
        // 更新缓存
        streamInfoEntities.forEach(streamInfoEntity -> streamInfoCacheService.setStreamInfo(streamInfoEntity));
    }
    
    public Map<String, NginxHttpRequestInfoEntity> getRequestFromLog() {
        Map<String, NginxHttpRequestInfoEntity> infoSet = new HashMap<>(16);
        String path = myConfigProperties.getNginxAccessLogPath();
        if (FileUtils.exist(path)) {
            try {
                String text = IOUtils.toString(FileUtils.getFileInputStream(path), StandardCharsets.UTF_8);
                if (StringUtils.isEmpty(text)) {
                    return infoSet;
                }
                List<String> logInfoList = Lists.newArrayList(text.split("\n"));
                logInfoList.forEach(line -> {
                    NginxHttpRequestInfoEntity info = parseRequestInfo(line);
                    if (null != info && null != info.getRequestUri()) {
                        infoSet.put(info.getRequestUri() + ";" + info.getRequestIp(), info);
                    }
                });
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return infoSet;
    }

    public NginxHttpRequestInfoEntity parseRequestInfo(String line) {
        String[] arr = line.split("]");
//        log.warn("{}", Lists.newArrayList(arr));
        if (arr.length < 4) {
            return null;
        }
        NginxHttpRequestInfoEntity info = new NginxHttpRequestInfoEntity();
        String ip = parseLogItem(arr[0]);
        String time = parseLogItem(arr[1]);
        String request = parseLogItem(arr[2]).split(" ")[1];
        String userAgent = parseLogItem(arr[3]);
        String requestUri;
        if (request.contains("-")) {
            requestUri = request.split("-")[0];
        } else {
            requestUri = request.split("\\.")[0];
        }
        info.setRequestIp(ip);
        info.setRequestTime(time);
        info.setRequestUri(requestUri);
        info.setUserAgent(userAgent);
        return info;
    }

    private String parseLogItem(String item) {
        return item.substring(item.indexOf('[') + 1);
    }

}
