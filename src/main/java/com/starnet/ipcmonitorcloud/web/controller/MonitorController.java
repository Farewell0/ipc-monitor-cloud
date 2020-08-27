package com.starnet.ipcmonitorcloud.web.controller;

import com.starnet.ipcmonitorcloud.mq.MqResponse;
import com.starnet.ipcmonitorcloud.web.config.NoToken;
import com.starnet.ipcmonitorcloud.web.model.HttpResponse;
import com.starnet.ipcmonitorcloud.web.model.HttpStatus;
import com.starnet.ipcmonitorcloud.web.service.MonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * MonitorController
 *
 * @author wzz
 * @date 2020/8/24 15:59
 **/
@Slf4j
@RestController
public class MonitorController {

    @Autowired
    private MonitorService monitorService;

    @PostMapping("/monitor/start")
    public HttpResponse startMonitor(String ipcAddr) {
        if (StringUtils.isEmpty(ipcAddr)) {
            return new HttpResponse(HttpStatus.REQUEST_PARAMS_ERROR);
        }
        MqResponse response = monitorService.startMonitor(ipcAddr);
        return new HttpResponse(response.getStatus(), response.getMessage());
    }

    @PostMapping("/monitor/stop")
    public HttpResponse stopMonitor(String ipcAddr) {
        if (StringUtils.isEmpty(ipcAddr)) {
            return new HttpResponse(HttpStatus.REQUEST_PARAMS_ERROR);
        }
        boolean flag = monitorService.stopMonitor(ipcAddr);
        return new HttpResponse(HttpStatus.OK);
    }

    /**
     * 向nginx推流需要向此接口发出认证请求，返回200后，nginx才能推流
     * @param token UUID生成的唯一token
     * @return HttpResponse
     */
    @NoToken
    @PostMapping("/monitor/onPublish")
    public ResponseEntity pushIdentifyAuth(String token) {
        if (StringUtils.isEmpty(token)) {
            return new ResponseEntity(org.springframework.http.HttpStatus.BAD_REQUEST);
        }
        if (monitorService.validatePushStreamToken(token)) {
          return new ResponseEntity(org.springframework.http.HttpStatus.OK);
        }
        return new ResponseEntity(org.springframework.http.HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/monitor/getPushStreamToken")
    public HttpResponse<String> getPushStreamToken() {
        String token = monitorService.getPushStreamToken();
        if (!StringUtils.isEmpty(token)) {
            return new HttpResponse<>(token);
        }
        return new HttpResponse<>(HttpStatus.SERVER_ERROR, "Get push stream token fail");
    }

    @NoToken
    @PostMapping("/monitor/onPlay")
    public ResponseEntity streamOnPlay(HttpServletRequest request) {
        // 在这里统计某个流的播放人数，onPlay 人数+1
        // 这种方式只能统计以RTMP流播放的人数
        String app = request.getParameter("app");
        String streamName = request.getParameter("name");
        log.info("app: {} stream: {} is on play", app, streamName);
        monitorService.streamOnPlay(app, streamName);
        return new ResponseEntity(org.springframework.http.HttpStatus.OK);
    }

    @NoToken
    @PostMapping("/monitor/onPlayDone")
    public ResponseEntity streamOnPlayDone(HttpServletRequest request) {
        String app = request.getParameter("app");
        String streamName = request.getParameter("name");
        log.info("app: {} stream: {} is on play done", app, streamName);
        monitorService.streamOnPlayDone(app, streamName);
        return new ResponseEntity(org.springframework.http.HttpStatus.OK);
    }

    @NoToken
    @PostMapping("/monitor/onConnect")
    public ResponseEntity streamOnConnect(HttpServletRequest request) {
        request.getParameterMap().forEach((k, v) -> {
            log.info("[{}] : [{}]", k, v);
        });
        return new ResponseEntity(org.springframework.http.HttpStatus.OK);
    }

}
