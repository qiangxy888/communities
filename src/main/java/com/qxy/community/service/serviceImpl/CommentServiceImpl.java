package com.qxy.community.service.serviceImpl;

import com.qxy.community.dao.CommentMapper;
import com.qxy.community.entity.Comment;
import com.qxy.community.service.CommentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/9/24 19:03
 */
@Service
public class CommentServiceImpl implements CommentService {
    @Resource
    private CommentMapper commentMapper;

    @Override
    public List<Comment> queryCommentsByEntity(int entityType, int entityId, int offset, int rowCnt) {
        return commentMapper.queryCommentsByEntity(entityType, entityId, offset, rowCnt);
    }

    @Override
    public int queryCommentsTotal(int entityType, int entityId) {
        return commentMapper.queryCommentsTotal(entityType, entityId);
    }
}
