package com.qxy.community.service;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/11/20 21:18
 */
public interface FollowService {
    /**
     * 关注
     *
     * @param entityType 关注的实体类型
     * @param entityId   实体Id
     */
    void follow(int userId, int entityType, int entityId);

    /**
     * 取消关注
     *
     * @param userId
     * @param entityType
     * @param entityId
     */
    void unfollow(int userId, int entityType, int entityId);

    /**
     * 查询关注的实体的数量
     *
     * @param userId
     * @param entityType
     * @return
     */
    long findFolloweeCount(int userId, int entityType);

    /**
     * 查询实体的粉丝数量
     *
     * @param entityType
     * @param entityId
     * @return
     */
    long findFollowerCount(int entityType, int entityId);

    /**
     * 查询当前用户是否关注某实体
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    boolean hasFollowed(int userId,int entityType,int entityId);
}
