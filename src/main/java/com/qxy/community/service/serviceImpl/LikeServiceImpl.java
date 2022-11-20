package com.qxy.community.service.serviceImpl;

import com.qxy.community.service.LikeService;
import com.qxy.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/10/29 10:46
 */
@Service
public class LikeServiceImpl implements LikeService {
    @Autowired
    private RedisTemplate redisTemplate;

    //点赞
    public void like(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        //判断是否点过赞
        Boolean member = redisTemplate.opsForSet().isMember(entityLikeKey, userId);//集合中是否存在这个userId
        if (member) {
            redisTemplate.opsForSet().remove(entityLikeKey, userId);//取消赞
        } else {
            redisTemplate.opsForSet().add(entityLikeKey, userId);//点赞
        }
    }

    //查询某实体点赞的数量
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);//返回集合的大小，表示有多少人点过赞
    }

    //查询某人对某实体的点赞状态
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
        //已赞 1，未赞0
        //可扩展，如果之后增加回踩的功能，返回-1即可
    }
}
