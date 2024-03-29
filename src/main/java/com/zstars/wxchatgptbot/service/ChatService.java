package com.zstars.wxchatgptbot.service;

import com.zstars.wxchatgptbot.mapper.ChatMapper;
import com.zstars.wxchatgptbot.pojo.entity.Chat;
import com.zstars.wxchatgptbot.pojo.dto.Sender;
import com.zstars.wxchatgptbot.util.OpenAiApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatMapper chatMapper;

    @Autowired
    private OpenAiApiClient openAiApiClient;
    
    @Autowired
    ChatHistoryService chatHistoryService;

    public String chatGptText(String content, Sender sender) throws IOException, JSONException {
        chatHistoryService.maintainChatHistory(sender.getId());
        List<Chat> historyChat = chatMapper.findChatsByUserId(sender.getId());
        return openAiApiClient.constructTextPayload(content, historyChat);
    }



    public String chatGptImage(MultipartFile contentFile, Sender sender) throws IOException, JSONException {
        Chat latestChat = chatMapper.findChatsByUserId_L1(sender.getId());
        return openAiApiClient.constructImagePayload(contentFile, latestChat);
    }
}
