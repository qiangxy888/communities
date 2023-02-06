package com.qxy.community.entity;

import lombok.Data;
import org.springframework.mail.MailParseException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qxy
 * @version 1.0
 * @Date 2023/1/31 6:57
 */
@Data
public class Event {
    private int userId;//触发事件的用户id
    private int entityType;//实体类型 点赞、评论、关注
    private int entityId;//实体id
    private int entityUserId;//接收方id
    private String topic;//主题
    private Map<String,Object> data = new HashMap<>();//发送的数据

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }


    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key,Object value) {
        this.data.put(key,value);
        return this;
    }
}
