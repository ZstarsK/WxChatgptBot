package com.zstars.wxchatgptbot.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zstars.wxchatgptbot.pojo.dto.Sender;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GetSenderUtil {

    private final ObjectMapper objectMapper;

    public GetSenderUtil() {
        this.objectMapper = new ObjectMapper();
    }

    public Sender extractSenderFromSource(String source) throws IOException {
        //System.out.println(source);
        Sender sender = new Sender();
        JsonNode rootNode = objectMapper.readTree(source);
        JsonNode roomNode = rootNode.path("room");
        if (roomNode.isObject() && !roomNode.isEmpty()) {
            // 处理群发消息
            String roomId = roomNode.path("id").asText();
            String roomTopic = roomNode.path("payload").path("topic").asText();

            sender.setId(roomId);
            sender.setName(roomTopic);
            sender.setRoom(true);
        } else {
            // 处理个人消息
            JsonNode fromNode = rootNode.path("from");
            if (!fromNode.isMissingNode()) {
                JsonNode nameNode = fromNode.path("payload").path("name");
                if (!nameNode.isMissingNode()) {
                    sender.setId(fromNode.path("id").asText());
                    sender.setName(nameNode.asText());
                    sender.setRoom(false);
                } else {
                    throw new IOException("The 'name' field is missing in 'from' object.");
                }
            } else {
                throw new IOException("The 'from' field is missing.");
            }
        }
        return sender;
    }
}
