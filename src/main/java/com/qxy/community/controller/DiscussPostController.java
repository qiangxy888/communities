package com.qxy.community.controller;

import com.qxy.community.constant.CommunityConstant;
import com.qxy.community.entity.Comment;
import com.qxy.community.entity.DiscussPost;
import com.qxy.community.entity.Page;
import com.qxy.community.entity.User;
import com.qxy.community.service.CommentService;
import com.qxy.community.service.DiscussPostService;
import com.qxy.community.service.LikeService;
import com.qxy.community.service.UserService;
import com.qxy.community.util.CommunityUtil;
import com.qxy.community.util.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/9/12 14:14
 */
@Slf4j
@Controller
@RequestMapping("/discuss")
public class DiscussPostController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private LikeService likeService;

    /**
     * 发布帖子
     *
     * @param title
     * @param content
     * @return
     */
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        //获取当前用户
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJsonString(403, "请先登录");
        }
        //数据校验
        if (StringUtils.isBlank(title) || StringUtils.isBlank(content)) {
            return CommunityUtil.getJsonString(403, "参数不能为空");
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        discussPostService.saveDiscussPost(discussPost);
        return CommunityUtil.getJsonString(0, "发布成功");
    }

    /**
     * 显示帖子详情
     *
     * @param discussPostId 帖子id
     * @param model
     * @return
     */
    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String queryDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        //帖子
        DiscussPost post = discussPostService.queryById(discussPostId);
        model.addAttribute("post", post);
        //作者
        User user = userService.queryById(post.getUserId());
        model.addAttribute("user", user);
        //帖子的点赞数量和点赞状态
        long likeCount = likeService.findEntityLikeCount(CommunityConstant.ENTITY_TYPE_POST, discussPostId);
        //注意如果用户没有登录，无法获得点赞状态
        int likeStatus = hostHolder.getUser() == null ? 0 :
                likeService.findEntityLikeStatus(hostHolder.getUser().getId(), CommunityConstant.ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("likeStatus", likeStatus);
        //分页数据
        page.setRowCnt(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());
        //评论：给帖子的评论
        //回复：给评论的评论
        //评论列表
        List<Comment> commentList = commentService.queryCommentsByEntity(CommunityConstant.ENTITY_TYPE_POST,
                post.getId(), page.getOffset(), page.getRowCnt());
        //评论显示vo列表
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                Map<String, Object> commentVo = new HashMap<>();
                commentVo.put("comment", comment);//评论
                commentVo.put("user", userService.queryById(comment.getUserId()));//作者
                //评论的点赞数量和点赞状态
                likeCount = likeService.findEntityLikeCount(CommunityConstant.ENTITY_TYPE_COMMENT, comment.getId());
                //注意如果用户没有登录，无法获得点赞状态
                likeStatus = hostHolder.getUser() == null ? 0 :
                        likeService.findEntityLikeStatus(hostHolder.getUser().getId(), CommunityConstant.ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeCount",likeCount);
                commentVo.put("likeStatus",likeStatus);
                //回复列表
                List<Comment> replyList = commentService.queryCommentsByEntity(CommunityConstant.ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                //回复显示vo列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        replyVo.put("reply", reply);//回复
                        replyVo.put("user", userService.queryById(reply.getUserId()));
                        User target = reply.getTargetId() == 0 ? null : userService.queryById(reply.getTargetId());//回复目标
                        replyVo.put("target", target);
                        replyVoList.add(replyVo);
                        //回复的点赞数量和点赞状态
                        likeCount = likeService.findEntityLikeCount(CommunityConstant.ENTITY_TYPE_COMMENT, reply.getId());
                        //注意如果用户没有登录，无法获得点赞状态
                        likeStatus = hostHolder.getUser() == null ? 0 :
                                likeService.findEntityLikeStatus(hostHolder.getUser().getId(), CommunityConstant.ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeCount",likeCount);
                        replyVo.put("likeStatus",likeStatus);
                    }
                }
                commentVo.put("replys", replyVoList);
                int replyCount = commentService.queryCommentsTotal(CommunityConstant.ENTITY_TYPE_COMMENT, comment.getEntityId());
                commentVo.put("replyCount", replyCount);
                commentVoList.add(commentVo);
            }
            model.addAttribute("comments", commentVoList);
        }
        return "/site/discuss-detail";
    }
}
