package com.starnet.ipcmonitorcloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.starnet.ipcmonitorcloud.database.mapper")
@SpringBootApplication
public class IpcMonitorCloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(IpcMonitorCloudApplication.class, args);
    }

}
