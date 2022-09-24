package com.qxy.community.controller;

import com.qxy.community.entity.DiscussPost;
import com.qxy.community.entity.User;
import com.qxy.community.service.DiscussPostService;
import com.qxy.community.service.UserService;
import com.qxy.community.util.CommunityUtil;
import com.qxy.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/9/12 14:14
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;
    @RequestMapping(path = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title,String content){
        //获取当前用户
        User user = hostHolder.getUser();
        if(user==null){
            return CommunityUtil.getJsonString(403,"请先登录");
        }
        //数据校验
        if(StringUtils.isBlank(title)||StringUtils.isBlank(content)){
            return CommunityUtil.getJsonString(403,"参数不能为空");
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        discussPostService.saveDiscussPost(discussPost);
        return CommunityUtil.getJsonString(0,"发布成功");
    }
    @RequestMapping(path = "/detail/{discussPostId}",method = RequestMethod.GET)
    public String queryDiscussPost(@PathVariable("discussPostId") int id, Model model){
        //帖子
        DiscussPost post = discussPostService.queryById(id);
        model.addAttribute("post",post);
        //作者
        User user = userService.queryById(post.getUserId());
        model.addAttribute("user",user);
        return "/site/discuss-detail";
    }
}
