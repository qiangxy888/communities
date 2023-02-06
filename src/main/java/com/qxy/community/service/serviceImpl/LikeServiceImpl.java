package com.qxy.community.service.serviceImpl;

import com.qxy.community.constant.CommunityConstant;
import com.qxy.community.service.LikeService;
import com.qxy.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
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
    public void like(int userId, int entityType, int entityId, int entityUserId) {
//        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
//        //判断是否点过赞
//        Boolean member = redisTemplate.opsForSet().isMember(entityLikeKey, userId);//集合中是否存在这个userId
//        if (member) {
//            redisTemplate.opsForSet().remove(entityLikeKey, userId);//取消赞
//        } else {
//            redisTemplate.opsForSet().add(entityLikeKey, userId);//点赞
//        }
        //对redis有两次操作，加上事务
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                Boolean member = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
                redisTemplate.multi();//开启事务
                if (member) {
                    redisTemplate.opsForSet().remove(entityLikeKey, userId);//取消赞
                    redisTemplate.opsForValue().decrement(userLikeKey);//用户的赞减一
                } else {
                    redisTemplate.opsForSet().add(entityLikeKey, userId);//点赞
                    redisTemplate.opsForValue().increment(userLikeKey);//用户的赞加一
                }
                return redisTemplate.exec();//提交事务
            }
        });
    }

    //查询某实体点赞的数量
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);//返回集合的大小，表示有多少人点过赞
    }

    //查询某人对某实体的点赞状态
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ?
                CommunityConstant.LIKE_STATUS : CommunityConstant.UNLIKE_STATUS;
        //已赞 1，未赞0
        //可扩展，如果之后增加回踩的功能，返回-1即可
    }

    @Override
    //查询某用户获得赞的数量
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        return redisTemplate.opsForValue().get(userLikeKey) == null ? 0 :
                (Integer)redisTemplate.opsForValue().get(userLikeKey);
    }
}
