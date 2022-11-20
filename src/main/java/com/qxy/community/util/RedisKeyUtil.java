package com.qxy.community.util;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/10/29 10:36
 */
public class RedisKeyUtil {
    //key的分隔符
    private static final String SPLIT = ":";
    //点赞的key的前缀
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    //某个实体的赞的key
    //如  like:entity:entityType:entityId
    public static String getEntityLikeKey(int eneityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + eneityType + SPLIT + entityId;
    }
    //某个用户的赞的key
    //如  like:user:userId
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE+SPLIT+userId;
    }
}
