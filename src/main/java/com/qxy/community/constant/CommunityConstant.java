package com.qxy.community.constant;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/8/28 11:31
 */
public class CommunityConstant {
    //用户相关
    public static final int USER_NOT_EXISTS = 10;//用户不存在

    //激活操作
    public static final int ACTIVATION_SUCCESS = 20;//激活成功
    public static final int ACTIVATION_REPEAT = 21;//重复激活
    public static final int ACTIVATION_FAIL = 22;//激活失败

    //登录相关
    public static final int REMEMBER_EXPIRED_SECONDS=1000*3600*48;//记住我
    public static final int DEFAULT_EXPIRED_SECONDS=1000*3600*6;//默认
    public static final int LOGIN_TICKET_STATUS_VALID=0;//登录凭证有效
    public static final int LOGIN_TICKET_STATUS_INVALID=1;//登录凭证无效

    //评论相关
    public static final int ENTITY_TYPE_POST=1;//评论对象是帖子
    public static final int ENTITY_TYPE_COMMENT=2;//评论对象是评论

    //私信相关
    public static final int UNREAD_MESSAGE=0;//未读消息
    public static final int READ_MESSAGE=1;//已读消息
}
