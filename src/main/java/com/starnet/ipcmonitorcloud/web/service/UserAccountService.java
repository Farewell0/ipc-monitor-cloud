package com.starnet.ipcmonitorcloud.web.service;

import com.starnet.ipcmonitorcloud.web.model.HttpResponse;

/**
 * UserAccountService
 *
 * @author wzz
 * @date 2020/8/25 10:59
 **/
public interface UserAccountService {

    HttpResponse<String> getLoginToken(String username, String password);

    String getEncryptPassword(String source);

    boolean validateLoginToken(String token);
}
