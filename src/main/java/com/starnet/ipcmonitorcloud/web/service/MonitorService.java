package com.starnet.ipcmonitorcloud.web.service;

import com.starnet.ipcmonitorcloud.mq.MqRequest;
import com.starnet.ipcmonitorcloud.mq.MqResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * MonitorService
 *
 * @author wzz
 * @date 2020/8/24 14:38
 **/
public interface MonitorService {

    MqResponse startMonitor(String ipcAddr);

    boolean stopMonitor(String ipcAddr);

    boolean validatePushStreamToken(String token);

    String getPushStreamToken();

    void createStreamInfoCache(String pullAddr, String pushAddr);

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
