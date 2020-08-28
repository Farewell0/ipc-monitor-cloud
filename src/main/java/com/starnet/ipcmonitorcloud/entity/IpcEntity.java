package com.starnet.ipcmonitorcloud.entity;

import lombok.Data;

/**
 * IpcEntity
 *
 * @author wzzfarewell
 * @date 2020/8/28 上午10:20
 **/
@Data
public class IpcEntity {
    private Integer id;
    private String name;
    private String company;
    private String account;
    private String password;
    private String rtspAddr;
    private String description;

}
