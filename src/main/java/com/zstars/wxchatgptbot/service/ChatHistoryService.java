package com.zstars.wxchatgptbot.service;

import com.zstars.wxchatgptbot.mapper.ChatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ChatHistoryService {

    private final ChatMapper chatMapper;

    @Autowired
    public ChatHistoryService(ChatMapper chatMapper) {
        this.chatMapper = chatMapper;
    }

    public void maintainChatHistory(String userId) {
        List<Integer> idsToDelete = chatMapper.findChatIdsToDelete(userId);
        if (!idsToDelete.isEmpty()) {
            chatMapper.deleteChatsByIds(idsToDelete);
        }
    }

    // ... 其他与聊天历史相关的方法 ...
}
