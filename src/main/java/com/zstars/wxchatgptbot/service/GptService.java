package com.zstars.wxchatgptbot.service;

import com.zstars.wxchatgptbot.util.GetResTextUtil;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GptService {
    
    @Autowired
    SendService sendService;
    private static final String API_KEY = "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"; // API 密钥
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public void askChatgpt(String content, String senderName) throws IOException {
        HttpHost proxy = new HttpHost("127.0.0.1", 8123);
        RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
        
        String prompt = "请用中文回答我的问题："+content; // 替换为您的对话内容
        
        try (CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(config).build()) {
            HttpPost request = getPost(prompt);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                
                //输出
                //System.out.println(GetResTextUtil.getText(responseBody));
                sendService.sendMessage(senderName,GetResTextUtil.getText(responseBody),false);//假设当前不是群发
                //System.out.println(responseBody);//返回完整Res
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private HttpPost getPost(String prompt) throws JSONException {
        HttpPost request = new HttpPost(API_URL);
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + API_KEY);
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

        // 创建包含单个对象的 JSONArray
        JSONArray messagesArray = new JSONArray();
        messagesArray.put(putMessage(prompt));

        // 创建包含 messages 数组的 JSONObject
        JSONObject param = new JSONObject();
        param.put("model", "gpt-3.5-turbo-1106"); //标注gpt使用版本
        param.put("messages", messagesArray);

        StringEntity entity = new StringEntity(param.toString(), "UTF-8");
        request.setEntity(entity);
        return request;
    }
    private JSONObject putMessage(String prompt) throws JSONException{
        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", prompt);
        return message;
    }
    
    
}
