package com.zstars.wxchatgptbot.handler;

import com.zstars.wxchatgptbot.mapper.ChatMapper;
import com.zstars.wxchatgptbot.pojo.Chat;
import com.zstars.wxchatgptbot.pojo.Sender;
import com.zstars.wxchatgptbot.service.ChatService;
import com.zstars.wxchatgptbot.service.SendService;
import com.zstars.wxchatgptbot.util.GetResTextUtil;
import com.zstars.wxchatgptbot.util.GetSenderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class TextMessageHandler extends MessageHandler {

    @Autowired
    private ChatService chatService;
    @Autowired
    private GetSenderUtil getSenderUtil;
    @Autowired
    private ChatMapper chatMapper;
    @Autowired
    SendService sendService;

    @Override
    public void handle(String content, String source) throws IOException, JSONException {
        Sender sender = getSenderUtil.extractSenderFromSource(source);
        System.out.println(sender);
        System.out.println("Received text message from :" + sender.getName() + ". The message is:" + content);
        if (!sender.getName().isEmpty()) {
            if (sender.isRoom()){
                //如果是群聊信息，仅回复@消息
                if (content.contains("@ZZZ")) {
                    content = content.replace("@ZZZ", "");
                    String gptRespond = chatService.ChatgptText(content, sender);
                    // 保存聊天记录到数据库
                    saveChat(sender, content, gptRespond);
                    // 发送消息
                    sendService.sendMessage(sender.getName(), gptRespond, sender.isRoom());
                }
            }else {
                String gptRespond = chatService.ChatgptText(content, sender);
                // 保存聊天记录到数据库
                saveChat(sender, content, gptRespond);
                // 发送消息
                sendService.sendMessage(sender.getName(), gptRespond, sender.isRoom());
            }

        }
    }

    private void saveChat(Sender sender, String content, String gptRespond) {
        Chat chat = new Chat();
        chat.setName(sender.getName());
        chat.setUserid(sender.getId());
        chat.setPrompt(content);
        chat.setPromptanswer(gptRespond);
        chat.setTimestamp(String.valueOf(new Date().getTime()));
        System.out.println(chat);
        chatMapper.insertChat(chat);
    }


}
