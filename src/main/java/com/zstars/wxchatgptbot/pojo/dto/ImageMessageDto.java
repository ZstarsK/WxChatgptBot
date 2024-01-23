package com.zstars.wxchatgptbot.pojo.dto;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

public class ImageMessageDto {

    private String role;
    private String text;
    private String type;
    private String imageUrl;
    private String imageDetail;

    // 构造函数
    public ImageMessageDto (String role, String text, String type, String imageUrl, String imageDetail) {
        this.role = role;
        this.text = text;
        this.type = type;
        this.imageUrl = imageUrl;
        this.imageDetail = imageDetail;
    }

    // 创建文本消息的静态方法
    public static ImageMessageDto createTextMessage(String role, String text) {
        return new ImageMessageDto(role, text, "text", null, null);
    }

    // 创建图片消息的静态方法
    public static ImageMessageDto createImageMessage(String role, String base64Image, String imageDetail) {
        return new ImageMessageDto(role, null, "image_url", "data:image/jpeg;base64," + base64Image, imageDetail);
    }

    // 转换为 JSON 对象
    public JSONObject toJson() throws JSONException {
        JSONObject message = new JSONObject();
        JSONArray contentArray = new JSONArray();

        if ("text".equals(this.type)) {
            JSONObject textMessage = new JSONObject();
            textMessage.put("type", this.type);
            textMessage.put("text", this.text);
            contentArray.put(textMessage);
        }

        if ("image_url".equals(this.type)) {
            JSONObject imageMessage = new JSONObject();
            JSONObject imageUrl = new JSONObject();
            imageUrl.put("url", this.imageUrl);
            imageUrl.put("detail", this.imageDetail);
            imageMessage.put("type", this.type);
            imageMessage.put("image_url", imageUrl);
            contentArray.put(imageMessage);
        }

        message.put("role", this.role);
        message.put("content", contentArray);
        return message;
    }
}