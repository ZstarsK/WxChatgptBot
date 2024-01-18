package com.zstars.wxchatgptbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.stereotype.Service;

@Service
public class ReceiveService {

    @Autowired
    private SendService sendService;

    public void processReceivedMessage(String message) throws JSONException {

        System.out.println("Received message: " + message);
        
        // 解析消息，生成回复
        String reply = generateReply(message);

        // 使用 SendService 发送回复
        sendService.sendMessage("testUser", reply, false);
    }

    private String generateReply(String content) {
        // 生成回复逻辑
        // ...
        
        return "Generated reply";
    }
}

