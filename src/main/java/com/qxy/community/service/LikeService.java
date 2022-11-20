package com.qxy.community.service;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/10/29 12:35
 */
public interface LikeService {
    /**
     * 点赞
     * @param userId
     * @param entityType
     * @param entityId
     */
    void like(int userId, int entityType, int entityId,int entityUserId);

    /**
     * 查询某实体的点赞数量
     * @param entityType
     * @param entityId
     * @return
     */
    long findEntityLikeCount(int entityType, int entityId);

    /**
     * 查询某人对某实体的点赞状态
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    int findEntityLikeStatus(int userId, int entityType, int entityId);

    /**
     * 查询某个用户获得的赞的数量
     * @param userId
     * @return
     */
    int findUserLikeCount(int userId);
}
