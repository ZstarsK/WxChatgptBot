package com.zstars.wxchatgptbot.handler;

import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class FileMessageHandler implements MessageHandler {

    @Override
    public void handle(String content, String source) throws IOException {
        // 处理文件消息的逻辑
        System.out.println("Received file message: " + content);
    }
}
