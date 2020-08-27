package com.starnet.ipcmonitorcloud.web.service.impl;

import com.starnet.ipcmonitorcloud.database.mapper.UserAccountMapper;
import com.starnet.ipcmonitorcloud.database.model.UserAccountEntity;
import com.starnet.ipcmonitorcloud.utils.Md5Utils;
import com.starnet.ipcmonitorcloud.web.model.HttpResponse;
import com.starnet.ipcmonitorcloud.web.model.HttpStatus;
import com.starnet.ipcmonitorcloud.web.service.UserAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * UserAccountServiceImpl
 *
 * @author wzz
 * @date 2020/8/25 11:01
 **/
@Slf4j
@Service
public class UserAccountServiceImpl implements UserAccountService {

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Override
    public HttpResponse<String> getLoginToken(String username, String password) {
        UserAccountEntity entity = userAccountMapper.selectByUsername(username);
        if (null != entity) {
            boolean flag = getEncryptPassword(password).equals(entity.getPassword());
            if (flag) {
                return new HttpResponse<>(generateLoginToken());
            } else {
                return new HttpResponse<>(HttpStatus.UNAUTHORIZED, "Login user error");
            }
        }
        return new HttpResponse<>(HttpStatus.UNAUTHORIZED, "Login user error");
    }

    private String generateLoginToken() {
        return Md5Utils.getMd5("STARnet002396");
    }

    @Override
    public String getEncryptPassword(String source) {
        return Md5Utils.getMd5(source);
    }

    @Override
    public boolean validateLoginToken(String token) {
        return generateLoginToken().equals(token);
    }


    public static void main(String[] args) {
        System.out.println(Md5Utils.getMd5("STARnet002396"));
    }
}
