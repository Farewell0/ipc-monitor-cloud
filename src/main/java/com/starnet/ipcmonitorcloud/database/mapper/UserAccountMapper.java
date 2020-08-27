package com.starnet.ipcmonitorcloud.database.mapper;

import com.starnet.ipcmonitorcloud.database.model.UserAccountEntity;

import java.util.List;

/**
 * UserAccountMapper
 *
 * @author wzz
 * @date 2020/8/25 10:52
 **/
public interface UserAccountMapper {

    UserAccountEntity selectByUsername(String username);

    List<UserAccountEntity> selectAll();
}
