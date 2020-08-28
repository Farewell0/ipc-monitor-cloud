package com.starnet.ipcmonitorcloud.entity;

import lombok.Data;

import java.util.Objects;

/**
 * NginxHttpLogInfo
 *
 * @author wzzfarewell
 * @date 2020/8/27 上午10:43
 **/
@Data
public class NginxHttpRequestInfoEntity {
    private String requestIp;
    private String requestTime;
    /**
     * 推流地址URI，比如 /hls/stream_1
     */
    private String requestUri;
    private String userAgent;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NginxHttpRequestInfoEntity info = (NginxHttpRequestInfoEntity) o;
        return requestIp.equals(info.requestIp) && requestUri.equals(info.requestUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestIp);
    }
}
