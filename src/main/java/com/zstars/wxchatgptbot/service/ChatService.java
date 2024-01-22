package com.zstars.wxchatgptbot.service;

import com.zstars.wxchatgptbot.mapper.ChatMapper;
import com.zstars.wxchatgptbot.pojo.Chat;
import com.zstars.wxchatgptbot.pojo.Sender;
import com.zstars.wxchatgptbot.util.GetResTextUtil;
import org.apache.commons.codec.binary.Base64;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatMapper chatMapper;
    
    @Value("${Chatgpt.Version4}")
    private String model;
    
    @Value("${Chatgpt.API_KEY}")
    private String API_KEY;
    
    @Value("${Chatgpt.API_URL}")
    private String API_URL;
    
    public String ChatgptText(String content, Sender sender) throws IOException {
        HttpHost proxy = new HttpHost("127.0.0.1", 8123);
        RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
        
        try (CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(config).build()) {
            HttpPost request = getPost(content,sender);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String responseBody = EntityUtils.toString(response.getEntity());

                return GetResTextUtil.getText(responseBody);
            }catch (Exception e) {
                return "服务器返回值错误，请稍后再试";
            }
        } catch (JSONException e) {
            return "服务器返回值错误，请稍后再试";
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
        param.put("model", model);
        param.put("messages", messagesArray);
        param.put("max_tokens",2048);

        StringEntity entity = new StringEntity(param.toString(), "UTF-8");
        
        //打印请求体
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

    public void maintainChatHistory(String userId) {
        // 查找需要删除的聊天记录ID
        List<Integer> idsToDelete = chatMapper.findChatIdsToDelete(userId);

        // 如果有需要删除的记录，则执行删除
        if (!idsToDelete.isEmpty()) {
            chatMapper.deleteChatsByIds(idsToDelete);
        }
    }

    public String ChatgptImage(MultipartFile contentFile , Sender sender) throws IOException, JSONException {
        // 检查文件是否为空
        if (contentFile.isEmpty()) {
            throw new IllegalArgumentException("上传的文件不能为空");
        }

        // 将文件转换为 Base64 编码的字符串
        String base64Image = convertFileToBase64(contentFile);
        // 构造 payload
        JSONObject payload = getJsonObject(base64Image, sender);

        // 发送请求
        HttpPost request = new HttpPost(API_URL);
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + API_KEY);
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        StringEntity entity = new StringEntity(payload.toString(), "UTF-8");
        request.setEntity(entity);
        //设置代理
        HttpHost proxy = new HttpHost("127.0.0.1", 8123);
        RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
        //打印payload
        //System.out.println(payload);
        try (CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(config).build();
             CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            return GetResTextUtil.getText(responseBody);
        }
    }

    @NotNull
    private JSONObject getJsonObject(String base64Image , Sender sender) throws JSONException {
        JSONObject payload = new JSONObject();
        payload.put("model", "gpt-4-vision-preview");

        JSONArray messagesArray = new JSONArray();
        JSONObject message = new JSONObject();
        JSONArray contentArray = getJsonArray(base64Image, sender);

        message.put("role", "user");
        message.put("content", contentArray);
        messagesArray.put(message);

        payload.put("messages", messagesArray);
        payload.put("max_tokens", 500);
        return payload;
    }

    @NotNull
    private JSONArray getJsonArray(String base64Image, Sender sender) throws JSONException {
        JSONArray contentArray = new JSONArray();
        Chat chat = chatMapper.findChatsByUserId_L1(sender.getId());
        
        // 添加文本消息
        JSONObject textMessage = new JSONObject();
        textMessage.put("type", "text");
        textMessage.put("text", chat.getPrompt());
        contentArray.put(textMessage);

        // 添加图片消息
        JSONObject imageMessage = new JSONObject();
        imageMessage.put("type", "image_url");
        JSONObject imageUrl = new JSONObject();
        imageUrl.put("url", "data:image/jpeg;base64," + base64Image);
        imageUrl.put("detail", "low");
        imageMessage.put("image_url", imageUrl);
        contentArray.put(imageMessage);
        return contentArray;
    }

    private String convertFileToBase64(MultipartFile file) throws IOException {
        byte[] bytes;
        try (InputStream inputStream = file.getInputStream()) {
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
        }
        return Base64.encodeBase64String(bytes);
    }

}
