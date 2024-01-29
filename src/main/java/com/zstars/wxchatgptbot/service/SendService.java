package com.zstars.wxchatgptbot.service;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SendService {
    

    public void sendMessage(String to, String content, boolean isRoom) throws JSONException {
        String url = "http://localhost:3001/webhook/msg/v2";

        // 使用 JSONObject 构建 JSON
        JSONObject dataObject = new JSONObject();
        dataObject.put("content", content);

        JSONObject jsonBody = new JSONObject();

        jsonBody.put("to", to);
        jsonBody.put("data", dataObject);
        jsonBody.put("isRoom", isRoom);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(url);
            request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            request.setEntity(new StringEntity(jsonBody.toString(), "UTF-8"));

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                System.out.println(responseBody); // 打印响应内容
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
}