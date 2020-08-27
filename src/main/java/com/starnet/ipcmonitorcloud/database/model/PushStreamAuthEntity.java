package com.starnet.ipcmonitorcloud.database.model;

import lombok.Data;

/**
 * PushStreamAuthEntity
 *
 * @author wzz
 * @date 2020/8/24 17:47
 **/
@Data
public class PushStreamAuthEntity {
    private int id;
    private String pushAuthToken;
}
