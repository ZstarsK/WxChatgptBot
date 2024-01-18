package com.zstars.wxchatgptbot.controller;

import com.zstars.wxchatgptbot.service.GptService;
import com.zstars.wxchatgptbot.util.GetSenderNameUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/message")
public class messageController {

    private final GetSenderNameUtil getSenderNameUtil;
    @Autowired
    GptService gptService;

    public messageController(GetSenderNameUtil getSenderNameUtil) {
        this.getSenderNameUtil = getSenderNameUtil;
    }

    @PostMapping(value = "/receive")
    public String receiveMessage(
            @RequestParam("type") String type,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "content", required = false) MultipartFile contentFile,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "isMentioned", required = false) String isMentioned,
            @RequestParam(value = "isSystemEvent", required = false) String isSystemEvent) throws IOException {

        // 根据类型处理内容
        switch (type) {
            case "text":

                String senderName = getSenderNameUtil.extractSenderNameFromSource(source);
                
                // 处理文本消息
                System.out.println("Received text message from :"+senderName +"---- text:"+ content);
                
                if (!senderName.isEmpty()) {
                    gptService.askChatgpt(content, senderName);
                }
                break;
            case "file":
                // 处理文件消息
                if (contentFile != null && !contentFile.isEmpty()) {
                    try {
                        
                        System.out.println("Received file with original name: " + contentFile.getOriginalFilename());
                        // 这里可以添加保存文件的代码
                    } catch (Exception e) {
                        System.out.println("Error while processing the file: " + e.getMessage());
                    }
                }
                break;
            case "urlLink":
            case "friendship":
            case "systemEvent":
                // 处理JSON内容的消息
                System.out.println("Received " + type + " message: " + content);
                break;
            default:
                System.out.println("Received unsupported type of message: " + type);
                break;
        }

        return "Message received";
    }
}

