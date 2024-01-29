package com.zstars.wxchatgptbot.util;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class GetResTextUtil {

    public GetResTextUtil() {
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
