package com.qxy.community.service.serviceImpl;

import com.qxy.community.constant.CommunityConstant;
import com.qxy.community.dao.UserMapper;
import com.qxy.community.entity.User;
import com.qxy.community.service.FollowService;
import com.qxy.community.util.HostHolder;
import com.qxy.community.util.RedisKeyUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/11/20 21:20
 */
@Service
public class FollowServiceImpl implements FollowService {
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private UserMapper userMapper;

    /**
     * 关注
     *
     * @param userId
     * @param entityType 关注的实体类型
     * @param entityId   实体Id
     */
    @Override
    public void follow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                //定义key
                String followerKey = RedisKeyUtil.getFollower(entityType, entityId);//某个实体的粉丝
                String followeeKey = RedisKeyUtil.getFollowee(userId, entityType);//某个用户关注的实体
                //开启事务
                redisTemplate.multi();
                redisTemplate.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                redisTemplate.opsForZSet().add(followerKey, userId, System.currentTimeMillis());
                return redisTemplate.exec();//提交事务
            }
        });
    }

    /**
     * 取消关注
     *
     * @param userId
     * @param entityType
     * @param entityId
     */
    @Override
    public void unfollow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFollowee(userId, entityType);//某个用户关注的实体
                String followerKey = RedisKeyUtil.getFollower(entityType, entityId);//某个实体的粉丝
                redisTemplate.multi();
                redisTemplate.opsForZSet().remove(followeeKey, entityId);
                redisTemplate.opsForZSet().remove(followerKey, userId);
                return redisTemplate.exec();
            }
        });
    }

    /**
     * 关注的**的数量
     *
     * @param userId     用户
     * @param entityType 实体类型
     * @return
     */
    @Override
    public long findFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFollowee(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    /**
     * 粉丝数量
     *
     * @param entityType 实体类型
     * @param entityId   实体id
     * @return
     */
    @Override
    public long findFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollower(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    /**
     * 查询当前用户是否已关注该实体
     *
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    @Override
    public boolean hasFollowed(int userId, int entityType, int entityId) {
        String followeeKey = RedisKeyUtil.getFollowee(userId, entityType);
        //关注的实体中是否有该entityId
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;//如果这个条件成立就返回true，说明已关注
    }

    @Override
    public List<Map<String, Object>> findFolloweeList(int userId, int offset, int rowCnt) {
        String followeeKey = RedisKeyUtil.getFollowee(userId, CommunityConstant.ENTITY_TYPE_USER);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + rowCnt - 1);
        if (targetIds == null || targetIds.size() == 0) {
            return null;
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer id : targetIds) {
            User user = userMapper.queryById(id);
            Map<String, Object> map = new HashMap<>();
            map.put("user", user);
            Double score = redisTemplate.opsForZSet().score(followeeKey, id);
            map.put("followeeTime", new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> findFollowerList(int userId, int offset, int rowCnt) {
        String followerKey = RedisKeyUtil.getFollower(CommunityConstant.ENTITY_TYPE_USER, userId);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + rowCnt - 1);
        if (targetIds == null || targetIds.size() == 0)
            return null;
        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer id : targetIds) {
            User user = userMapper.queryById(id);
            Map<String, Object> map = new HashMap<>();
            map.put("user", user);
            Double score = redisTemplate.opsForZSet().score(followerKey, id);
            map.put("followerTime", new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }

}
