package com.starnet.ipcmonitorcloud.web.service.impl;

import com.starnet.ipcmonitorcloud.cache.service.IpcCacheService;
import com.starnet.ipcmonitorcloud.entity.IpcEntity;
import com.starnet.ipcmonitorcloud.entity.PushStreamEntity;
import com.starnet.ipcmonitorcloud.entity.StreamInfoEntity;
import com.starnet.ipcmonitorcloud.cache.service.PushStreamAuthCacheService;
import com.starnet.ipcmonitorcloud.cache.service.StreamInfoCacheService;
import com.starnet.ipcmonitorcloud.config.MyConfigProperties;
import com.starnet.ipcmonitorcloud.database.mapper.PushStreamAuthMapper;
import com.starnet.ipcmonitorcloud.database.model.PushStreamAuthEntity;
import com.starnet.ipcmonitorcloud.exception.MqException;
import com.starnet.ipcmonitorcloud.mq.MqResponse;
import com.starnet.ipcmonitorcloud.mq.MqProducer;
import com.starnet.ipcmonitorcloud.mq.MqStatus;
import com.starnet.ipcmonitorcloud.mq.component.MqIpcComponent;
import com.starnet.ipcmonitorcloud.web.response.HttpResponse;
import com.starnet.ipcmonitorcloud.web.response.HttpStatus;
import com.starnet.ipcmonitorcloud.web.service.MonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
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

    @Autowired
    private IpcCacheService ipcCacheService;

    @Autowired
    private MqIpcComponent mqIpcComponent;

    /**
     * 推流id，用来区分不同的推流
     */
    private AtomicInteger streamId = new AtomicInteger();

    /**
     * 定时检测某个流是否有人在播放，如果没有直接停止这条拉流的推流
     */
    @Scheduled(initialDelay = 1000, fixedDelayString = "300000")
    public void checkStreamOnPlay() {
        log.info("Check all stream on play now...");
        List<StreamInfoEntity> streamInfoEntities = streamInfoCacheService.getAllStreamCache();
        streamInfoEntities.forEach(streamInfoEntity -> {
            PushStreamEntity pushStreamEntity = streamInfoEntity.getPushStreamEntity();
            if (pushStreamEntity.getRtmpObserverNum() <= 0 && pushStreamEntity.getHlsObserverNum() <= 0) {
                // 这个流已经没有人在看了，发起停止推流命令并从缓存中移除
                stopPushStream(streamInfoEntity.getIpcEntity());
            }
        });
    }

    @Override
    public List<IpcEntity> getIpcList() {
        return mqIpcComponent.getIpcList();
    }

    @Override
    public HttpResponse<PushStreamEntity> startMonitor(Integer id) {
        String ipcId = String.valueOf(id);
        // 判断是否已经在推流
        if (streamInfoCacheService.existStreamInfo(ipcId)) {
            return new HttpResponse<>(HttpStatus.OK.getCode(), "This ipc stream is already push",
                    streamInfoCacheService.getStreamInfo(ipcId).getPushStreamEntity());
        }
        IpcEntity ipcEntity = ipcCacheService.getIpcCache(ipcId);
        String ipcAddr = ipcEntity.getRtspAddr();
        StartMonitorReq startMonitorReq = new StartMonitorReq();
        startMonitorReq.setIpcAddr(ipcAddr);
        // 生成推流地址
        String pushAddr = myConfigProperties.getPushToNginxStreamPrefix() + incrementStreamId();
        startMonitorReq.setPushAddr(pushAddr);
        MqResponse response = null;
        try {
            response = mqProducer.sendAndReceive(startMonitorReq, MqResponse.class);
        } catch (MqException e) {
            log.error(e.getMessage());
            decrementStreamId();
            return new HttpResponse<>(HttpStatus.PUSH_STREAM_FAIL);
        }
        if (response.getStatus() == MqStatus.OK.getCode()) {
            // 将成功推流的流放入缓存
            StreamInfoEntity streamInfoEntity = new StreamInfoEntity();
            streamInfoEntity.setIpcEntity(ipcEntity);
            PushStreamEntity pushStreamEntity = initPushStreamEntity(pushAddr);
            streamInfoEntity.setPushStreamEntity(pushStreamEntity);
            streamInfoCacheService.setStreamInfo(streamInfoEntity);
            return new HttpResponse<>(HttpStatus.OK.getCode(), "OK", pushStreamEntity);
        } else {
            decrementStreamId();
            return new HttpResponse<>(response.getStatus(), response.getMessage());
        }
    }

    private PushStreamEntity initPushStreamEntity(String pushAddr) {
        PushStreamEntity pushStreamEntity = new PushStreamEntity();
        pushStreamEntity.setPushAddr(pushAddr);
        String[] arr = pushAddr.split("/");
        if (null != arr && arr.length > 2) {
            pushStreamEntity.setApp(arr[arr.length - 2]);
            pushStreamEntity.setName(arr[arr.length - 1]);
        }
        return pushStreamEntity;
    }

    @Override
    public boolean stopMonitor(Integer id) {
        String ipcId = String.valueOf(id);
        // 是否在推流中
        if (!streamInfoCacheService.existStreamInfo(ipcId)) {
            return false;
        }
        StreamInfoEntity streamInfoEntity = streamInfoCacheService.getStreamInfo(ipcId);
        PushStreamEntity pushStreamEntity = streamInfoEntity.getPushStreamEntity();
        if (pushStreamEntity.getRtmpObserverNum() > 0) {
            log.info("this stream is be used for rtmp");
            return false;
        }
        if (pushStreamEntity.getHlsObserverNum() > 0) {
            log.info("this stream is be used for hls");
            return false;
        }
        // 命令本地端停止推流
        IpcEntity ipcEntity = ipcCacheService.getIpcCache(ipcId);
        MqResponse response = stopPushStream(ipcEntity);
        log.info("stop monitor response: {}", response);
        return response.getStatus() == MqStatus.OK.getCode();
    }

    @Override
    public List<StreamInfoEntity> getStreamInfoList() {
        return streamInfoCacheService.getAllStreamCache();
    }

    private MqResponse stopPushStream(IpcEntity ipcEntity) {
        StopMonitorReq stopMonitorReq = new StopMonitorReq();
        stopMonitorReq.setIpcAddr(ipcEntity.getRtspAddr());
        MqResponse response = mqProducer.sendAndReceive(stopMonitorReq, MqResponse.class);
        // 删除推流信息缓存
        if (response.getStatus() == MqStatus.OK.getCode()) {
            streamInfoCacheService.removeStreamInfo(String.valueOf(ipcEntity.getId()));
        }
        return response;
    }

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
    public void streamOnPlay(String app, String name) {
        streamOnPlay(app, name, true);
    }

    @Override
    public void streamOnPlayDone(String app, String name) {
        streamOnPlay(app, name, false);
    }

    private void streamOnPlay(String app, String name, boolean onPlay) {
        List<StreamInfoEntity> streamInfoEntities = streamInfoCacheService.getAllStreamCache();
        for(StreamInfoEntity streamInfoEntity : streamInfoEntities) {
            PushStreamEntity pushStreamEntity = streamInfoEntity.getPushStreamEntity();
            // 找到对应的推流，操作rtmp流的观看人数
            if (pushStreamEntity.getApp().equals(app) && pushStreamEntity.getName().equals(name)) {
                if (onPlay) {
                    pushStreamEntity.setRtmpObserverNum(pushStreamEntity.getRtmpObserverNum() + 1);
                } else {
                    pushStreamEntity.setRtmpObserverNum(pushStreamEntity.getRtmpObserverNum() - 1);
                }
            }
        }
        log.info("{}", streamInfoEntities);
        streamInfoEntities.forEach(streamInfoEntity -> streamInfoCacheService.setStreamInfo(streamInfoEntity));
    }

    public int incrementStreamId() {
        return streamId.incrementAndGet();
    }

    public int decrementStreamId() {
        return streamId.decrementAndGet();
    }
}
