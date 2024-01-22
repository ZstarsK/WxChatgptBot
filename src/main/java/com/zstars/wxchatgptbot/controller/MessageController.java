package com.zstars.wxchatgptbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.zstars.wxchatgptbot.factory.MessageHandlerFactory;
import com.zstars.wxchatgptbot.handler.MessageHandler;
import com.zstars.wxchatgptbot.vo.ResultBean;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageHandlerFactory messageHandlerFactory;

    @PostMapping(value = "/receive")
    public ResultBean receiveMessage(
            HttpServletRequest request,
            @RequestParam("type") String type,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "isSystemEvent", required = false) String isSystemEvent) throws IOException, JSONException {

        if (Objects.equals(isSystemEvent, "0")) {
            MessageHandler handler = messageHandlerFactory.getHandler(type);

            if ("text".equals(type)) {
                String textContent = request.getParameter("content");
                if (textContent != null) {

                    handler.handle(textContent, source);
                }
            } else if ("file".equals(type)) {
                MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
                Iterator<String> fileNames = multipartRequest.getFileNames();
                while (fileNames.hasNext()) {
                    String fileName = fileNames.next();
                    MultipartFile fileContent = multipartRequest.getFile(fileName);
                    if (fileContent != null) {
                        handler.handle(fileContent, source);
                    }
                }
            }
        }

        return ResultBean.success("success");
    }
}
