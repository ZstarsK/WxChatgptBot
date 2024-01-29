package com.zstars.wxchatgptbot.factory;

import com.zstars.wxchatgptbot.handler.FileMessageHandler;
import com.zstars.wxchatgptbot.handler.MessageHandler;
import com.zstars.wxchatgptbot.handler.TextMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageHandlerFactory {

    @Autowired
    private TextMessageHandler textMessageHandler;

    @Autowired
    private FileMessageHandler fileMessageHandler;
    

    public MessageHandler getHandler(String type) {
        return switch (type) {
            case "text" -> textMessageHandler;
            case "file" -> fileMessageHandler;
            // 更多情况...
            default -> throw new IllegalArgumentException("Unsupported message type: " + type);
        };
    }
}
