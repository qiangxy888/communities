package com.qxy.community.entity;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/9/25 10:26
 */
@Data
@ToString
public class Message {
    private Integer id;//主键id
    private Integer fromId;//发送者id
    private Integer toId;//接受者id
    private String conversationId;//会话id
    private String content;//内容
    private int status;//状态 0未读，1已读，2删除
    private Date createTime;//创建时间
}
