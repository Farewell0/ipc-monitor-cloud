package com.starnet.ipcmonitorcloud.database.model;

import lombok.Data;

/**
 * UserAccountEntity
 *
 * @author wzz
 * @date 2020/8/25 10:51
 **/
@Data
public class UserAccountEntity {
    private int id;
    private String username;
    private String password;
}
