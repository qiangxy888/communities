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
    //关注的key的前缀
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";
    //某个实体的赞的key
    //如  like:entity:entityType:entityId
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }
    //某个用户的赞的key
    //如  like:user:userId
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE+SPLIT+userId;
    }
    //某个用户关注的实体
    //这里的实体可以是用户、帖子等
    //如 key=>followee:userId:entityType,value=>zset(entityId,now())
    public static String getFollowee(int userId,int entityType){
        return PREFIX_FOLLOWEE+SPLIT+userId+SPLIT+entityType;
    }
    //某个实体拥有的粉丝
    //entityType和entityId可以唯一确定一个实体
    //key=>follower:entityType:entityId,value=>zset(userId,now())
    public static String getFollower(int entityType,int entityId){
        return PREFIX_FOLLOWER+SPLIT+entityType+SPLIT+entityId;
    }
}
