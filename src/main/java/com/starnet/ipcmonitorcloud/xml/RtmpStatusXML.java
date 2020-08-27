package com.starnet.ipcmonitorcloud.xml;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * RtmpStatusXML
 *
 * @author wzz
 * @date 2020/8/25 19:21
 **/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "rtmp")
@NoArgsConstructor
@AllArgsConstructor
public class RtmpStatusXML {

    @XmlElement(name = "nginx_version")
    private String nginxVersion;


}
