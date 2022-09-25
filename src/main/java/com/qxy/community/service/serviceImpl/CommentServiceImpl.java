package com.qxy.community.service.serviceImpl;

import com.qxy.community.constant.CommunityConstant;
import com.qxy.community.dao.CommentMapper;
import com.qxy.community.dao.DiscussPostMapper;
import com.qxy.community.entity.Comment;
import com.qxy.community.service.CommentService;
import com.qxy.community.util.SensitiveFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/9/24 19:03
 */
@Service
@Slf4j
public class CommentServiceImpl implements CommentService {
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private SensitiveFilter sensitiveFilter;
    @Resource
    private DiscussPostMapper discussPostMapper;

    @Override
    public List<Comment> queryCommentsByEntity(int entityType, int entityId, int offset, int rowCnt) {
        return commentMapper.queryCommentsByEntity(entityType, entityId, offset, rowCnt);
    }

    @Override
    public int queryCommentsTotal(int entityType, int entityId) {
        return commentMapper.queryCommentsTotal(entityType, entityId);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public int insertComment(Comment comment) {
        //判空
        if(comment==null){
            throw new IllegalArgumentException("参数不能为空");
        }
        //内容过滤，添加评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int row = commentMapper.insertComment(comment);
        if(row<=0){
            throw new IllegalArgumentException("添加评论失败");
        }
        //更新帖子评论数量
        if(comment.getEntityType()== CommunityConstant.ENTITY_TYPE_POST){
            //从数据库comment表中查帖子的评论数量
            int count = commentMapper.queryCommentsTotal(comment.getEntityType(), comment.getEntityId());
            //将帖子的评论数量更新到discuss_post表中
            row = discussPostMapper.updateCommentCount(comment.getEntityId(), count);
            if(row<=0){
                throw new IllegalArgumentException("更新评论数量失败");
            }
        }
        return row;
    }
}
