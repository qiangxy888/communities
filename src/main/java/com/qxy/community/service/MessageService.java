package com.qxy.community.service;

import com.qxy.community.entity.Message;

import java.util.List;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/9/25 16:56
 */
public interface MessageService {
    /**
     * 查询当前用户的会话列表，显示最新私信
     *
     * @param userId
     * @param offset
     * @param rowCnt
     * @return
     */
    List<Message> selectConversation(int userId, int offset, int rowCnt);

    /**
     * 查询当前用户的会话数量
     *
     * @param userId
     * @return
     */
    int selectConversationCount(int userId);

    /**
     * 查询某个会话所包含的私信列表
     *
     * @param conversationId
     * @param offset
     * @param rowCnt
     * @return
     */
    List<Message> selectLetters(String conversationId, int offset, int rowCnt);

    /**
     * 查询某个会话所包含的私信数量
     *
     * @param conversation
     * @return
     */
    int selectLetterCount(String conversation);

    /**
     * 查询未读私信的数量(可以查总数，可以查某个会话的未读数)
     * 查总数时 conversationId传null
     * @param userId
     * @param conversationId
     * @return
     */
    int selectLetterUnreadCount(int userId, String conversationId);

    /**
     * 发送私信
     *
     * @param message
     * @return
     */
    int sendMessage(Message message);

    /**
     * 更新未读消息为已读
     *
     * @param ids
     * @return
     */
    int readMessage(List<Integer> ids);

    /**查询某个用户收到的某主题的通知列表
     * 支持分页
     * @param userId
     * @param topic
     * @param offset
     * @param rowCnt
     * @return
     */
    List<Message> selectSysMsgList(int userId, String topic,int offset,int rowCnt);

    //查询某用户收到的最新一条系统通知
    Message selectLastedSysMsg(int userId, String topic);

    /**查询用户收到的系统通知数
     * topic传null 表示系统通知总数
     * @param userId
     * @param topic
     * @return
     */
    int selectSysMsgCount(int userId, String topic);

    /**查询用户未读系统通知数量
     * topic传null 表示系统通知未读总数
     * @param userId
     * @param topic
     * @return
     */
    int selectUnreadSysMsgCount(int userId, String topic);
}
