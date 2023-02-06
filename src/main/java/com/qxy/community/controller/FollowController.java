package com.qxy.community.controller;

import com.qxy.community.constant.CommunityConstant;
import com.qxy.community.entity.Event;
import com.qxy.community.entity.Page;
import com.qxy.community.entity.User;
import com.qxy.community.event.EventProducer;
import com.qxy.community.service.FollowService;
import com.qxy.community.service.UserService;
import com.qxy.community.util.CommunityUtil;
import com.qxy.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private UserService userService;
    @Autowired
    private EventProducer producer;

    /**
     * 关注
     * @param entityType
     * @param entityId
     * @return
     */
    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        //TODO 拦截器拦截未登录状态下的请求
        followService.follow(user.getId(), entityType, entityId);
        //触发关注事件
        Event event = new Event();
        event.setTopic(CommunityConstant.TOPIC_FOLLOW)
                .setUserId(user.getId())
                .setEntityId(entityId)
                .setEntityType(entityType)
                .setEntityUserId(entityId);
        producer.fireEvent(event);

        return CommunityUtil.getJsonString(0, "已关注");
    }

    /**
     * 取消关注
     * @param entityType
     * @param entityId
     * @return
     */
    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        //TODO 拦截器
        followService.unfollow(user.getId(), entityType, entityId);
        return CommunityUtil.getJsonString(0, "已取关");
    }

    /**
     * 关注列表
     * @param userId
     * @param page
     * @param model
     * @return
     */
    @RequestMapping(path = "/followee/{userId}", method = RequestMethod.GET)
    public String followeeList(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.queryById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        model.addAttribute("user",user);
        page.setRowCnt(5);
        page.setPath("/followee/" + userId);
        page.setRows((int) followService.findFolloweeCount(hostHolder.getUser().getId(), CommunityConstant.ENTITY_TYPE_USER));
        List<Map<String, Object>> followeeList = followService.findFolloweeList(userId, page.getOffset(), page.getRowCnt());
        if (followeeList != null) {
            for(Map<String,Object> map:followeeList){
                User U = (User)map.get("user");
                map.put("hasFollowed",hasFollowed(U.getId()));
            }
        }
        model.addAttribute("users",followeeList);
        return "/site/followee";
    }

    /**
     * 粉丝列表
     * @param userId
     * @param page
     * @param model
     * @return
     */
    @RequestMapping(path = "/follower/{userId}", method = RequestMethod.GET)
    public String followerList(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.queryById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        model.addAttribute("user",user);
        page.setRowCnt(5);
        page.setPath("/follower/" + userId);
        page.setRows((int) followService.findFollowerCount(hostHolder.getUser().getId(), CommunityConstant.ENTITY_TYPE_USER));
        List<Map<String, Object>> followerList = followService.findFollowerList(userId, page.getOffset(), page.getRowCnt());
        if (followerList != null) {
            for(Map<String,Object> map:followerList){
                User U = (User)map.get("user");
                map.put("hasFollowed",hasFollowed(U.getId()));
            }
        }
        model.addAttribute("users",followerList);
        return "/site/follower";
    }
    /**
     * 判断登录用户是否关注了列表中的用户
     * @param userId
     * @return
     */
    private boolean hasFollowed(int userId){
        if(hostHolder.getUser()==null){
            return false;
        }
        return followService.hasFollowed(hostHolder.getUser().getId(),CommunityConstant.ENTITY_TYPE_USER,userId);
    }
}
