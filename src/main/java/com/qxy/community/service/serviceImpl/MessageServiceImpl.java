package com.qxy.community.service.serviceImpl;

import com.qxy.community.constant.CommunityConstant;
import com.qxy.community.dao.MessageMapper;
import com.qxy.community.entity.Message;
import com.qxy.community.service.MessageService;
import com.qxy.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
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
    @Autowired
    private SensitiveFilter sensitiveFilter;

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

    @Override
    public int sendMessage(Message message) {
        int row = 0;
        if (message != null) {
//            message.setContent(HtmlUtils.htmlEscape(message.getContent()));//特殊字符转义 防止恶意攻击
            message.setContent(sensitiveFilter.filter(message.getContent()));
            row = messageMapper.saveMessage(message);
        }
        if (row <= 0) {
            throw new IllegalArgumentException("发送私信失败");
        }
        return row;
    }

    @Override
    public int readMessage(List<Integer> ids) {
        return messageMapper.updateMessageStatus(ids, CommunityConstant.READ_MESSAGE);
    }

}
