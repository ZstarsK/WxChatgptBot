package com.zstars.wxchatgptbot.controller;

import com.zstars.wxchatgptbot.factory.MessageHandlerFactory;
import com.zstars.wxchatgptbot.handler.MessageHandler;
import com.zstars.wxchatgptbot.vo.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageHandlerFactory messageHandlerFactory;
    

    @PostMapping(value = "/receive")
    public ResultBean receiveMessage(
            @RequestParam("type") String type,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "content", required = false) MultipartFile contentFile,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "isMentioned", required = false) String isMentioned,
            @RequestParam(value = "isSystemEvent", required = false) String isSystemEvent) throws IOException {

        if (Objects.equals(isSystemEvent, "0")) {
            MessageHandler handler = messageHandlerFactory.getHandler(type);
            handler.handle(content, source);
        }
        return ResultBean.success("success");
    }
}

