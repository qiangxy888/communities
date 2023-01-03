package com.qxy.community.controller;

import com.qxy.community.entity.User;
import com.qxy.community.service.FollowService;
import com.qxy.community.util.CommunityUtil;
import com.qxy.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/11/20 21:40
 */
@Controller
public class FollowController {
    @Autowired
    private FollowService followService;
    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        //TODO 拦截器拦截未登录状态下的请求
        followService.follow(user.getId(), entityType, entityId);
        return CommunityUtil.getJsonString(0, "已关注");
    }

    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        //TODO 拦截器
        followService.unfollow(user.getId(), entityType, entityId);
        return CommunityUtil.getJsonString(0, "已取关");
    }
}
