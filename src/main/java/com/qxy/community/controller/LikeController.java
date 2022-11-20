package com.qxy.community.controller;

import com.qxy.community.entity.Page;
import com.qxy.community.entity.User;
import com.qxy.community.service.LikeService;
import com.qxy.community.util.CommunityUtil;
import com.qxy.community.util.HostHolder;
import com.sun.xml.internal.bind.v2.TODO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/10/29 11:15
 */
@Controller
public class LikeController {
    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/like",method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType,int entityId){
        User user = hostHolder.getUser();
        //点赞
        //TODO 待处理异常
        likeService.like(user.getId(),entityType,entityId);
        //数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        //状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        //返回的结果
        HashMap<String, Object> map = new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);
        return CommunityUtil.getJsonString(0,null,map);
    }
}
