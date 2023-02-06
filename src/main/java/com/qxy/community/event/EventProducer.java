package com.qxy.community.event;

import com.alibaba.fastjson.JSONObject;
import com.qxy.community.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * @author qxy
 * @version 1.0
 * @Date 2023/1/31 7:02
 */
@Component
public class EventProducer {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    //触发事件
    public void fireEvent(Event event){
        kafkaTemplate.send(event.getTopic(),JSONObject.toJSONString(event));
    }
}
