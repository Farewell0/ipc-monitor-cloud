package com.starnet.ipcmonitorcloud.web.controller;

import com.starnet.ipcmonitorcloud.web.config.NoToken;
import com.starnet.ipcmonitorcloud.web.response.HttpResponse;
import com.starnet.ipcmonitorcloud.web.response.HttpStatus;
import com.starnet.ipcmonitorcloud.web.service.UserAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * UserAuthController
 *
 * @author wzz
 * @date 2020/8/25 10:51
 **/
@Slf4j
@RestController
public class UserAuthController {

    @Autowired
    private UserAccountService userAccountService;

    @NoToken
    @PostMapping("/user/login")
    public HttpResponse<String> login(String username, String password) {
        if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
            return userAccountService.getLoginToken(username, password);
        }
        return new HttpResponse<>(HttpStatus.REQUEST_PARAMS_ERROR);
    }
}
