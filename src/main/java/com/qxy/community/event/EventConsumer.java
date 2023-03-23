package com.qxy.community.event;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.qxy.community.constant.CommunityConstant;
import com.qxy.community.entity.Event;
import com.qxy.community.entity.Message;
import com.qxy.community.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.log4j2.Log4J2LoggingSystem;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author qxy
 * @version 1.0
 * @Date 2023/1/31 7:08
 */
@Slf4j
@Component
public class EventConsumer {
    @Autowired
    private KafkaTemplate kafkaTemplate;
    @Autowired
    private MessageService messageService;
    @KafkaListener(topics = {CommunityConstant.TOPIC_LIKE,
            CommunityConstant.TOPIC_COMMENT,CommunityConstant.TOPIC_FOLLOW})
    public void handleMessage(ConsumerRecord record){
        if(record==null||record.value()==null){
            log.error("消息为空");
            return;
        }
        log.info("消费的消息 record："+record);
        Event event = JSONObject.parseObject(record.value().toString(),Event.class);
        if(event==null){
            log.error("消息格式错误");
            return;
        }
        sendMessage(event);
    }
    //发送站内通知
    public void sendMessage(Event event){
        Message message = new Message();
        message.setStatus(CommunityConstant.UNREAD_MESSAGE);
        message.setFromId(CommunityConstant.SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());

        Map<String, Object> content = new HashMap<>();
        content.put("entityId",event.getEntityId()+"");
        content.put("entityType",event.getEntityType()+"");
        content.put("userId",event.getEntityUserId()+"");
        if(!event.getData().isEmpty()){
            for(Map.Entry<String,Object> entry : event.getData().entrySet()){
                content.put(entry.getKey(), entry.getValue());
            }
        }
        log.info("站内通知内容："+content);
        log.info("转换格式后的内容："+JSONObject.toJSONString(content));
        message.setContent(JSONObject.toJSONString(content));

        message.setCreateTime(new Date());
        messageService.sendMessage(message);
    }
}
