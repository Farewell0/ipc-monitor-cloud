package com.starnet.ipcmonitorcloud.web.service.impl;

import com.google.common.collect.Lists;
import com.starnet.ipcmonitorcloud.cache.model.PushStreamInfo;
import com.starnet.ipcmonitorcloud.cache.model.StreamInfoCache;
import com.starnet.ipcmonitorcloud.cache.service.PushStreamAuthCacheService;
import com.starnet.ipcmonitorcloud.cache.service.StreamInfoCacheService;
import com.starnet.ipcmonitorcloud.config.MyConfigProperties;
import com.starnet.ipcmonitorcloud.database.mapper.PushStreamAuthMapper;
import com.starnet.ipcmonitorcloud.database.model.PushStreamAuthEntity;
import com.starnet.ipcmonitorcloud.exception.MqException;
import com.starnet.ipcmonitorcloud.mq.MqResponse;
import com.starnet.ipcmonitorcloud.mq.MqProducer;
import com.starnet.ipcmonitorcloud.mq.MqStatus;
import com.starnet.ipcmonitorcloud.web.service.MonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * MonitorServiceImpl
 *
 * @author wzz
 * @date 2020/8/24 14:40
 **/
@Slf4j
@Service
public class MonitorServiceImpl implements MonitorService {

    @Autowired
    private MqProducer mqProducer;

    @Autowired
    private MyConfigProperties myConfigProperties;

    @Autowired
    private PushStreamAuthMapper pushStreamAuthMapper;

    @Autowired
    private PushStreamAuthCacheService pushStreamAuthCacheService;

    @Autowired
    private StreamInfoCacheService streamInfoCacheService;

    private AtomicInteger streamId = new AtomicInteger();

    private Map<String, List<String>> streamMap = new ConcurrentHashMap<>(16);


    @Override
    public MqResponse startMonitor(String ipcAddr) {
        if (streamMap.containsKey(ipcAddr)) {
            return new MqResponse(MqStatus.OK, "This ipc stream is already push");
        }

        StartMonitorReq startMonitorReq = new StartMonitorReq();
        startMonitorReq.setIpcAddr(ipcAddr);
        String pushAddr = myConfigProperties.getPushToNginxStreamPrefix() + incrementStreamId();
        startMonitorReq.setPushAddr(pushAddr);
        MqResponse response = null;
        try {
            response = mqProducer.sendAndReceive(startMonitorReq, MqResponse.class);
        } catch (MqException e) {
            log.error(e.getMessage());
            decrementStreamId();
            return new MqResponse(e.getMqStatus());
        }
        if (response.getStatus() == MqStatus.OK.getCode()) {
            streamMap.put(ipcAddr, Lists.newArrayList(pushAddr));
            createStreamInfoCache(ipcAddr, pushAddr);
        } else {
            decrementStreamId();
        }
        return response;
    }

    @Override
    public boolean stopMonitor(String ipcAddr) {
        // 只有判断这个ipc拉流的所有推流都没人在看，才能停止推流
        if (!streamMap.containsKey(ipcAddr) || !streamInfoCacheService.existStreamInfo(ipcAddr)) {
            return false;
        }
        StreamInfoCache streamInfoCache = streamInfoCacheService.getStreamInfo(ipcAddr);
        for(Map.Entry<String, PushStreamInfo> entry : streamInfoCache.getPushStreamInfoMap().entrySet()) {
            if (entry.getValue().getObserverNum() > 0) {
                log.info("this stream is be used");
                return false;
            }
        }
        // TODO 前面只判断了rtmp流的观看数量，还要判断网页端，即使用http hls播放的数量

        // 命令本地端停止推流
        StopMonitorReq stopMonitorReq = new StopMonitorReq();
        stopMonitorReq.setIpcAddr(ipcAddr);
        MqResponse response = mqProducer.sendAndReceive(stopMonitorReq, MqResponse.class);
        // 删除推流信息缓存
        streamInfoCacheService.removeStreamInfo(ipcAddr);
        streamMap.remove(ipcAddr);
        log.info("stop monitor response: {}", response);
        if (response.getStatus() == MqStatus.OK.getCode()) {
            return true;
        }
        return false;
    }

    //TODO 定时检测某个流是否没有人在播放，如果没有直接停止推流

    @Override
    public boolean validatePushStreamToken(String token) {
        return token.equals(this.getPushStreamToken());
    }

    @Override
    public String getPushStreamToken() {
        String tokenCache = pushStreamAuthCacheService.getPushToken();
        if (null == tokenCache) {
            synchronized (this) {
                tokenCache = pushStreamAuthCacheService.getPushToken();
                if (null == tokenCache) {
                    List<PushStreamAuthEntity> entities = pushStreamAuthMapper.selectAll();
                    if (null != entities && !entities.isEmpty()) {
                        PushStreamAuthEntity entity = entities.get(0);
                        tokenCache = entity.getPushAuthToken();
                        pushStreamAuthCacheService.setPushToken(tokenCache);
                    }
                }
            }
        }
        return tokenCache;
    }

    @Override
    public void createStreamInfoCache(String pullAddr, String pushAddr) {
        PushStreamInfo pushStreamInfo = generatePushStreamInfo(pushAddr);
        String pushStreamInfoKey = generatePushStreamInfoKey(pushStreamInfo.getApp(), pushStreamInfo.getName());
        if (streamInfoCacheService.existStreamInfo(pullAddr)) {
            // 拉流已经存在
            StreamInfoCache streamInfoCache = streamInfoCacheService.getStreamInfo(pullAddr);
            if (!streamInfoCache.getPushStreamInfoMap().containsKey(pushStreamInfoKey)) {
                // 不存在这条推流时才创建
                streamInfoCache.getPushStreamInfoMap().put(pushStreamInfoKey, pushStreamInfo);
                log.info("{}", streamInfoCache);
                streamInfoCacheService.setStreamInfo(streamInfoCache);
            }
        } else {
            // 拉流不存在
            StreamInfoCache streamInfoCache = new StreamInfoCache();
            streamInfoCache.setPullAddr(pullAddr);
            Map<String, PushStreamInfo> pushStreamInfoMap = new HashMap<>();
            pushStreamInfoMap.put(pushStreamInfoKey, pushStreamInfo);
            streamInfoCache.setPushStreamInfoMap(pushStreamInfoMap);
            log.info("{}", streamInfoCache);
            streamInfoCacheService.setStreamInfo(streamInfoCache);
        }

    }

    private String generatePushStreamInfoKey(String app, String name) {
        return app + ":" + name;
    }

    @Override
    public void streamOnPlay(String app, String name) {
        streamOnPlay(app, name, true);
    }

    @Override
    public void streamOnPlayDone(String app, String name) {
        streamOnPlay(app, name, false);
    }

    private void streamOnPlay(String app, String name, boolean onPlay) {
        String redisKey = findPushStream(app, name);
        if (!StringUtils.isEmpty(redisKey)) {
            StreamInfoCache streamInfoCache = streamInfoCacheService.getStreamInfo(redisKey);
            PushStreamInfo pushStreamInfo = streamInfoCache.
                    getPushStreamInfoMap().get(generatePushStreamInfoKey(app, name));
            if (onPlay) {
                pushStreamInfo.setObserverNum(pushStreamInfo.getObserverNum() + 1);
            } else {
                pushStreamInfo.setObserverNum(pushStreamInfo.getObserverNum() - 1);
            }
            log.info("{}", streamInfoCache);
            streamInfoCacheService.setStreamInfo(streamInfoCache);
        }
    }

    private String findPushStream(String app, String name) {
        for(Map.Entry<String, List<String>> entry : streamMap.entrySet()) {
            log.info("streamMap: [{}]:[{}]", entry.getKey(), entry.getValue());
            for (String pushAddr : entry.getValue()) {
                if (pushAddr.contains(app + "/" + name)) {
                    return entry.getKey();
                }
            }
        }
        return "";
    }

    private PushStreamInfo generatePushStreamInfo(String pushAddr) {
        PushStreamInfo pushStreamInfo = new PushStreamInfo();
        pushStreamInfo.setPushAddr(pushAddr);
        String[] arr = pushAddr.split("/");
        if (null != arr && arr.length > 1) {
            pushStreamInfo.setApp(arr[arr.length - 2]);
            pushStreamInfo.setName(arr[arr.length - 1]);
        }
        return pushStreamInfo;
    }

    public static void main(String[] args) {
        Lists.newArrayList("rtmp://192.168.11.73:9135/hls/1".split("/")).forEach(System.out::println);
    }

    public int incrementStreamId() {
        return streamId.incrementAndGet();
    }

    public int decrementStreamId() {
        return streamId.decrementAndGet();
    }
}
