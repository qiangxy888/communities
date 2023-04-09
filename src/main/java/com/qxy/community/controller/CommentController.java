package com.qxy.community.controller;

import com.qxy.community.constant.CommunityConstant;
import com.qxy.community.entity.Comment;
import com.qxy.community.entity.DiscussPost;
import com.qxy.community.entity.Event;
import com.qxy.community.event.EventProducer;
import com.qxy.community.service.CommentService;
import com.qxy.community.service.DiscussPostService;
import com.qxy.community.util.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;
import java.util.HashMap;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/9/25 9:07
 */
@Slf4j
@Controller
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private CommentService commentService;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private EventProducer producer;

    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.insertComment(comment);

        //触发评论事件
        Event event = new Event().setUserId(hostHolder.getUser().getId())
                .setEntityId(comment.getEntityId())
                .setTopic(CommunityConstant.TOPIC_COMMENT)
                .setEntityType(comment.getEntityType())
                .setData("postId", discussPostId);
        //评论的对象可能是帖子也可能是评论，需要判断得到帖子或者评论发布者的id即entityUserId
        if (comment.getEntityType() == CommunityConstant.ENTITY_TYPE_POST) {
            DiscussPost target = discussPostService.queryById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        } else if (comment.getEntityType() == CommunityConstant.ENTITY_TYPE_COMMENT) {
            Comment target = commentService.queryCommentById(comment.getId());
            event.setEntityUserId(target.getUserId());
        }
        producer.fireEvent(event);
        // 回复帖子相当于更改了帖子信息，需要触发事件  以便更新到es
        if (comment.getEntityType() == CommunityConstant.ENTITY_TYPE_POST) {
            event = new Event()
                    .setTopic(CommunityConstant.TOPIC_PUBLISH)
                    .setUserId(comment.getUserId())
                    .setEntityType(CommunityConstant.ENTITY_TYPE_POST)
                    .setEntityId(discussPostId);
            producer.fireEvent(event);

        }
        return "redirect:/discuss/detail/" + discussPostId;
    }
}
