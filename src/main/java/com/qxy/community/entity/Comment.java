package com.qxy.community.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/9/24 18:42
 */
@Data
public class Comment {
    private int id;//主键
    private int userId;//用户id
    private int entityType;//评论目标的类型
    private int entityId;//评论目标的id
    private int targetId;//指向的评论id
    private String content;//内容
    private int status;//状态
    private Date createTime;//创建时间
}
