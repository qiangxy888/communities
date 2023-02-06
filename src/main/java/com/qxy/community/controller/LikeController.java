package com.qxy.community.controller;

import com.qxy.community.constant.CommunityConstant;
import com.qxy.community.entity.Event;
import com.qxy.community.entity.Page;
import com.qxy.community.entity.User;
import com.qxy.community.event.EventProducer;
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
    @Autowired
    private EventProducer producer;

    @RequestMapping(path = "/like",method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType,int entityId,int entityUserId,int postId){
        User user = hostHolder.getUser();
        //点赞
        //TODO 待处理异常
        likeService.like(user.getId(),entityType,entityId,entityUserId);
        //数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        //状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        //返回的结果
        HashMap<String, Object> map = new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);
        //触发点赞通知  判断点赞发通知 取消点赞不发通知
        if(likeStatus==CommunityConstant.LIKE_STATUS){
            Event event = new Event().setTopic(CommunityConstant.TOPIC_LIKE)
                    .setUserId(user.getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId",postId);//设置帖子id用于跳转到帖子页面
            producer.fireEvent(event);
        }
        return CommunityUtil.getJsonString(0,null,map);
    }
}
