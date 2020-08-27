package com.starnet.ipcmonitorcloud.database.mapper;

import com.starnet.ipcmonitorcloud.database.model.PushStreamAuthEntity;

import java.util.List;

/**
 * PushStreamAuthMapper.xml
 *
 * @author wzz
 * @date 2020/8/24 17:56
 **/
public interface PushStreamAuthMapper {

    List<PushStreamAuthEntity> selectAll();
}
