package com.zstars.wxchatgptbot.handler;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public abstract class MessageHandler {
    public void handle(String content, String source) throws IOException, JSONException {
        // 空实现
    }

    public void handle(MultipartFile content, String source) throws IOException, JSONException {
        // 空实现
    }
}
