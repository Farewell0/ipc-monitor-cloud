package com.starnet.ipcmonitorcloud.mq.component;

import com.starnet.ipcmonitorcloud.cache.service.IpcCacheService;
import com.starnet.ipcmonitorcloud.entity.IpcEntity;
import com.starnet.ipcmonitorcloud.exception.MqException;
import com.starnet.ipcmonitorcloud.mq.MqProducer;
import com.starnet.ipcmonitorcloud.mq.MqRequest;
import com.starnet.ipcmonitorcloud.mq.MqResponse;
import com.starnet.ipcmonitorcloud.mq.MqStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * MqIpcComponent
 *
 * @author wzzfarewell
 * @date 2020/8/28 上午10:16
 **/
@Slf4j
@Component
public class MqIpcComponent {
    @Autowired
    private MqProducer mqProducer;
    @Autowired
    private IpcCacheService ipcCacheService;

    @Scheduled(initialDelay = 1000, fixedDelayString = "300000")
    public void initIpc() {
        // 同步本地的ipc设备列表
        log.info("sync local ipc list...");
        List<IpcEntity> ipcEntityList = getIpcList();
        if (null != ipcEntityList) {
            ipcEntityList.forEach(ipcEntity -> ipcCacheService.setIpcCache(ipcEntity));
        }
    }

    public List<IpcEntity> getIpcList() {
        GetIpcListResp resp;
        GetIpcListReq req = new GetIpcListReq();
        try {
            resp = mqProducer.sendAndReceive(req, GetIpcListResp.class);
        } catch (MqException e) {
            log.error(e.getMessage());
            throw new MqException(MqStatus.GET_IPC_LIST_FAIL);
        }
        if (null != resp) {
            return resp.getIpcList();
        }
        return null;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class GetIpcListReq extends MqRequest {
        public GetIpcListReq() {
            this.setCommand(Command.GET_IPC_LIST.getType());
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class GetIpcListResp extends MqResponse {
        private List<IpcEntity> ipcList;
        public GetIpcListResp(MqStatus mqStatus) {
            super(mqStatus);
        }
    }
}
