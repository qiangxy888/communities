package com.qxy.community.service.serviceImpl;

import com.qxy.community.dao.MessageMapper;
import com.qxy.community.entity.Message;
import com.qxy.community.service.MessageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/9/25 17:00
 */
@Service
public class MessageServiceImpl implements MessageService {
    @Resource
    private MessageMapper messageMapper;
    @Override
    public List<Message> selectConversation(int userId, int offset, int rowCnt) {
        return messageMapper.selectConversation(userId, offset, rowCnt);
    }

    @Override
    public int selectConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    @Override
    public List<Message> selectLetters(String conversationId, int offset, int rowCnt) {
        return messageMapper.selectLetters(conversationId, offset, rowCnt);
    }

    @Override
    public int selectLetterCount(String conversation) {
        return messageMapper.selectLetterCount(conversation);
    }

    @Override
    public int selectLetterUnreadCount(int userId, String conversationId) {
        return messageMapper.selectLetterUnreadCount(userId, conversationId);
    }
}
