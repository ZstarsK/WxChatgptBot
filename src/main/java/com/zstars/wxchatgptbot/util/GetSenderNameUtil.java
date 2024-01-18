package com.zstars.wxchatgptbot.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
@Service
public class GetSenderNameUtil {

    private final ObjectMapper objectMapper;

    public GetSenderNameUtil() {
        this.objectMapper = new ObjectMapper();
    }

    public String extractSenderNameFromSource(String source) throws IOException {
        JsonNode rootNode = objectMapper.readTree(source);
        JsonNode fromNode = rootNode.path("from");
        if (!fromNode.isMissingNode()) {
            JsonNode nameNode = fromNode.path("payload").path("name");
            if (!nameNode.isMissingNode()) {
                return nameNode.asText();
            } else {
                //throw new IOException("The 'name' field is missing in 'from' object.");
            }
        } else {
            //throw new IOException("The 'from' field is missing.");
        }
        return "";
    }
}
