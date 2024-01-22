package com.zstars.wxchatgptbot.handler;

import com.zstars.wxchatgptbot.pojo.Sender;
import com.zstars.wxchatgptbot.service.ChatService;
import com.zstars.wxchatgptbot.util.GetSenderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class TextMessageHandler implements MessageHandler {

    @Autowired
    private ChatService chatService;
    @Autowired
    private GetSenderUtil getSenderUtil;

    @Override
    public void handle(String content, String source) throws IOException {
        Sender sender = getSenderUtil.extractSenderFromSource(source);
        System.out.println(sender);
        System.out.println("Received text message from :" + sender.getName() + " ,he/she message is:" + content);
        if (!sender.getName().isEmpty()) {
            if (sender.isRoom()){
                if (content.contains("@ZZZ")) {
                    chatService.Chatgpt(content, sender);
                }
            }else {
                chatService.Chatgpt(content, sender);
            }

        }
    }
}
