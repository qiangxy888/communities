package com.qxy.community.dao;

import com.qxy.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/9/25 10:51
 */
@Mapper
public interface MessageMapper {
    //查询当前用户的会话列表
    List<Message> selectConversation(int userId,int offset,int rowCnt);
    //查询当前用户的会话数量
    int selectConversationCount(int userId);
    //查询某个会话所包含的私信列表
    List<Message> selectLetters(String conversationId,int offset,int rowCnt);
    //查询某个会话所包含的私信数量
    int selectLetterCount(String conversation);
    //查询未读私信的数量
    int selectLetterUnreadCount(int userId,String conversationId);
    //新增消息
    int saveMessage(Message message);
    //更新消息状态
    int updateMessageStatus(List<Integer> ids,int status);
}
