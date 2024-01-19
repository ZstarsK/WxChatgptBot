package com.zstars.wxchatgptbot.handler;

import java.io.IOException;

public interface MessageHandler {
    void handle(String content, String source) throws IOException;
}
