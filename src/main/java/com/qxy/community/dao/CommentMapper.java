package com.qxy.community.dao;

import com.qxy.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/9/24 19:05
 */
@Mapper
public interface CommentMapper {
    List<Comment> queryCommentsByEntity(int entityType, int entityId, int offset, int rowCnt);

    int queryCommentsTotal(int entityType, int entityId);

    int insertComment(Comment comment);

    Comment queryCommentById(int commentId);
}
