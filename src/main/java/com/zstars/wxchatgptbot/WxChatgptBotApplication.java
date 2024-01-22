package com.zstars.wxchatgptbot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.zstars.wxchatgptbot.mapper")
public class WxChatgptBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(WxChatgptBotApplication.class, args);
    }

}
