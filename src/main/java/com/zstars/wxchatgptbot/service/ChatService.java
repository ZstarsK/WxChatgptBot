package com.zstars.wxchatgptbot.service;

import com.zstars.wxchatgptbot.mapper.ChatMapper;
import com.zstars.wxchatgptbot.pojo.Chat;
import com.zstars.wxchatgptbot.pojo.Sender;
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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private ChatMapper chatMapper;

    @Autowired
    private SendService sendService; // 假设您有这样一个用于发送信息的服务

    private static final String API_KEY = "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";
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    
    public void Chatgpt(String content, Sender sender) throws IOException {
        HttpHost proxy = new HttpHost("127.0.0.1", 8123);
        RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
        String prompt = content;
        

        try (CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(config).build()) {
            HttpPost request = getPost(prompt,sender);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                String answer = GetResTextUtil.getText(responseBody);

                System.out.println(answer);
                
                // 保存聊天记录到数据库
                saveChat(sender, content, answer);

                // 发送消息
                sendService.sendMessage(sender.getName(), answer, sender.isRoom());
            }catch (Exception e) {
                sendService.sendMessage(sender.getName(),"服务器错误，请稍后再试。", sender.isRoom());
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }




    public void maintainChatHistory(String userId) {
        // 查找需要删除的聊天记录ID
        List<Integer> idsToDelete = chatMapper.findChatIdsToDelete(userId);

        // 如果有需要删除的记录，则执行删除
        if (!idsToDelete.isEmpty()) {
            chatMapper.deleteChatsByIds(idsToDelete);
        }
    }

    
    @NotNull
    private HttpPost getPost(String prompt ,Sender sender) throws JSONException {
        HttpPost request = new HttpPost(API_URL);
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + API_KEY);
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

        JSONArray messagesArray = new JSONArray();
        maintainChatHistory(sender.getId());
        List<Chat> historyChat = chatMapper.findChatsByUserId(sender.getId());
        //System.out.println("历史聊天记录：" + historyChat);
        for (Chat chat : historyChat) {
            // 处理 chat 对象
            messagesArray.put(putMessage(chat.getPrompt(),"user"));
            messagesArray.put(putMessage(chat.getPromptanswer(),"assistant"));
        }

        messagesArray.put(putMessage(prompt,"user"));// 当前语句 放在最后

        JSONObject param = new JSONObject();
        param.put("model", "gpt-3.5-turbo-1106");
        param.put("messages", messagesArray);

        StringEntity entity = new StringEntity(param.toString(), "UTF-8");
        
        System.out.println("请求体" + param);
        
        request.setEntity(entity);
        
        return request;
    }

    private JSONObject putMessage(String prompt,String role) throws JSONException {
        JSONObject message = new JSONObject();
        message.put("role", role);
        message.put("content", prompt);
        return message;
    }

    private void saveChat(Sender sender, String prompt, String answer) {
        Chat chat = new Chat();
        chat.setName(sender.getName());
        chat.setUserid(sender.getId());
        chat.setPrompt(prompt);
        chat.setPromptanswer(answer);
        chat.setTimestamp(String.valueOf(new Date().getTime()));
        System.out.println(chat);
        chatMapper.insertChat(chat);
    }
}
