package com.zstars.wxchatgptbot.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
@Service
public class GetResTextUtil {

    public GetResTextUtil(ObjectMapper objectMapper) {
    }

    public static String getText(String res) throws JSONException {
        JSONObject responseObject = new JSONObject(res);
        JSONArray choicesArray = responseObject.getJSONArray("choices");
        if (choicesArray.length() > 0) {
            JSONObject firstChoice = choicesArray.getJSONObject(0);
            JSONObject message = firstChoice.getJSONObject("message");

            return message.getString("content");
        } else {
            
            return "error";
        }
    }
}
