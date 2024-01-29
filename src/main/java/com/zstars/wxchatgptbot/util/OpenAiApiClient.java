package com.zstars.wxchatgptbot.util;

import com.zstars.wxchatgptbot.pojo.dto.ImageMessageDto;
import com.zstars.wxchatgptbot.pojo.dto.TextMessageDto;
import com.zstars.wxchatgptbot.pojo.entity.Chat;
import okhttp3.*;
import org.apache.http.HttpHeaders;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class OpenAiApiClient {

    private final OkHttpClient client;
    private final String model;
    private final String apiKey;
    private final String apiUrl;
    
    

    @Autowired
    public OpenAiApiClient(@Value("${Chatgpt.Version4}") String model,
                           @Value("${Chatgpt.API_KEY}") String apiKey,
                           @Value("${Chatgpt.API_URL}") String apiUrl,
                           @Value("${RemoteServer.ip}") String serverip,
                           @Value("${RemoteServer.port}") String serverPort) {
        this.model = model;
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(serverip, Integer.parseInt(serverPort))); // 代理地址和端口
        this.client = new OkHttpClient.Builder()
                .proxy(proxy)
                .readTimeout(120, TimeUnit.SECONDS)
                .connectTimeout(120, TimeUnit.SECONDS)
                .followRedirects(true)
                .build();
    }

    public String constructTextPayload (String content, List<Chat> historyChat) throws IOException, JSONException {
        JSONArray messagesArray = TextMessageDto.buildMessagesArray(content, historyChat);
        JSONObject payload = new JSONObject();
        payload.put("model", model);
        payload.put("messages", messagesArray);
        payload.put("max_tokens", 2048);
        return sendRequest(payload);
    }

    public String constructImagePayload(MultipartFile contentFile, Chat latestChat) throws IOException, JSONException {
        String base64Image = convertFileToBase64(contentFile);
        JSONObject payload = getJsonObjectForImage(base64Image, latestChat);
        return sendRequest(payload);
    }

    //发送请求
    private String sendRequest(JSONObject payload) throws IOException, JSONException {
        Request request = buildRequest(payload.toString());
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            if (response.body() != null) {
                return GetResTextUtil.getText((response.body()).string());
            }
            return "服务器返回值错误，请稍后再试或联系管理员解决";
        }
    }

    @NotNull
    private JSONObject getJsonObjectForImage(String base64Image, Chat latestChat) throws JSONException {
        ImageMessageDto textMessage = ImageMessageDto.createTextMessage("user", latestChat.getPrompt());
        ImageMessageDto imageMessage = ImageMessageDto.createImageMessage("user", base64Image, "low");

        JSONArray messagesArray = new JSONArray();
        messagesArray.put(textMessage.toJson());
        messagesArray.put(imageMessage.toJson());

        JSONObject payload = new JSONObject();
        payload.put("model", "gpt-4-vision-preview");
        payload.put("messages", messagesArray);
        payload.put("max_tokens", 500);
        return payload;
    }
    
    private String convertFileToBase64(MultipartFile file) throws IOException {
        byte[] bytes;
        try (InputStream inputStream = file.getInputStream()) {
            bytes = new byte[inputStream.available()];
            //noinspection ResultOfMethodCallIgnored
            inputStream.read(bytes);
        }
        return java.util.Base64.getEncoder().encodeToString(bytes);
    }

    private Request buildRequest(String payload) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(payload, JSON);
        return new Request.Builder()
                .url(apiUrl)
                .post(body)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
    }
}
