package com.starnet.ipcmonitorcloud.web.service;

import com.starnet.ipcmonitorcloud.entity.IpcEntity;
import com.starnet.ipcmonitorcloud.entity.PushStreamEntity;
import com.starnet.ipcmonitorcloud.entity.StreamInfoEntity;
import com.starnet.ipcmonitorcloud.mq.MqRequest;
import com.starnet.ipcmonitorcloud.web.response.HttpResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * MonitorService
 *
 * @author wzz
 * @date 2020/8/24 14:38
 **/
public interface MonitorService {

    List<IpcEntity> getIpcList();

    HttpResponse<PushStreamEntity> startMonitor(Integer id);

    boolean stopMonitor(Integer id);

    List<StreamInfoEntity> getStreamInfoList();

    boolean validatePushStreamToken(String token);

    /**
     * 获取推流认证需要的token
     * @return token
     */
    String getPushStreamToken();

    /**
     * 根据推流的app和name进行播放时的相应操作
     * @param app 如：hls
     * @param name 如：stream_1
     */
    void streamOnPlay(String app, String name);

    void streamOnPlayDone(String app, String name);

    @Data
    @EqualsAndHashCode(callSuper = false)
    class StartMonitorReq extends MqRequest {
        private String ipcAddr;
        private String pushAddr;
        public StartMonitorReq() {
            this.setCommand(Command.START_MONITOR.getType());
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    class StopMonitorReq  extends MqRequest{
        private String ipcAddr;
        public StopMonitorReq() {
            this.setCommand(Command.STOP_MONITOR.getType());
        }
    }
}
