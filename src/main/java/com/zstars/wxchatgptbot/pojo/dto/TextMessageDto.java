package com.zstars.wxchatgptbot.pojo.dto;


import com.zstars.wxchatgptbot.pojo.entity.Chat;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.util.List;

public class TextMessageDto {

    private final String role;
    private final String content;

    private TextMessageDto(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public static TextMessageDto createTextMessage(String role, String content) {
        return new TextMessageDto(role, content);
    }

    public JSONObject toJson() throws JSONException {
        JSONObject message = new JSONObject();
        message.put("role", role);
        message.put("content", content);
        return message;
    }

    public static JSONArray buildMessagesArray(String prompt, List<Chat> historyChat) throws JSONException {
        JSONArray messagesArray = new JSONArray();
        for (Chat chat : historyChat) {
            messagesArray.put(createTextMessage("user", chat.getPrompt()).toJson());
            messagesArray.put(createTextMessage("assistant", chat.getPromptanswer()).toJson());
        }
        messagesArray.put(createTextMessage("user", prompt).toJson());
        return messagesArray;
    }
}
