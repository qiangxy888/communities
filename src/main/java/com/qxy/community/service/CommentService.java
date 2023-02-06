package com.qxy.community.service;

import com.qxy.community.entity.Comment;

import java.util.List;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/9/24 19:00
 */
public interface CommentService {
    /**
     * 查询分页评论数据
     *
     * @param entityType
     * @param entityId
     * @param offset
     * @param rowCnt
     * @return
     */
    List<Comment> queryCommentsByEntity(int entityType, int entityId, int offset, int rowCnt);

    /**
     * 查询总评论数
     *
     * @param entityType
     * @param entityId
     * @return
     */
    int queryCommentsTotal(int entityType, int entityId);

    /**
     * 新增评论
     * @param comment 评论实体
     * @return
     */
    int insertComment(Comment comment);

    /**
     * 根据评论id查评论实体
     * @param commentId
     * @return
     */
    Comment queryCommentById(int commentId);
}
