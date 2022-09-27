package com.qxy.community.controller;

import com.qxy.community.entity.Message;
import com.qxy.community.entity.Page;
import com.qxy.community.entity.User;
import com.qxy.community.service.MessageService;
import com.qxy.community.service.UserService;
import com.qxy.community.util.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.jws.WebParam;
import java.util.*;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/9/25 17:12
 */
@Slf4j
@Controller
public class MessageController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;

    /**
     * 显示私信列表、未读消息数
     * @param model
     * @param page
     * @return
     */
    @RequestMapping(path = "/letter/list",method = RequestMethod.GET)
    public String getLetterList(Model model, Page page){
        User user = hostHolder.getUser();
        //分页消息
        page.setRowCnt(5);
        page.setPath("/letter/list");
        page.setRows(messageService.selectConversationCount(user.getId()));
        //查询会话列表
        List<Message> conversationList = messageService.selectConversation(user.getId(), page.getOffset(), page.getRowCnt());
        log.info("conversationList>>>>>>>>>"+conversationList);
        ArrayList<Map<String, Object>> conversations = new ArrayList<>();
        if(!CollectionUtils.isEmpty(conversationList)){
            for(Message message:conversationList){
                log.info("message>>>>>>>>>"+message);
                HashMap<String, Object> map = new HashMap<>();
                map.put("conversation",message);
                int letterCount = messageService.selectLetterCount(message.getConversationId());
                map.put("letterCount",letterCount );
                log.info("letterCount>>>>>>>>"+letterCount);
                int unreadCount = messageService.selectLetterUnreadCount(user.getId(), message.getConversationId());
                map.put("unreadCount",unreadCount);
                log.info("unreadCount>>>>>>>>>>"+unreadCount);
                int targetId = user.getId()==message.getFromId()? message.getToId() : message.getFromId();
                log.info("targetId>>>>>>>>>>>"+targetId);
                map.put("target",userService.queryById(targetId));
                conversations.add(map);
            }
            model.addAttribute("conversations",conversations);
        }
        //查询总未读消息数量
        int letterUnreadCount = messageService.selectLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        return "/site/letter";
    }

    /**
     * 显示某会话的详细私信列表
     * @param conversationId
     * @param model
     * @param page
     * @return
     */
    @RequestMapping(path = "/letter/detail/{conversationId}",method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId ,Model model, Page page){
        //分页信息
        page.setRowCnt(5);//每页显示条数
        page.setPath("/letter/detail/"+conversationId);
        page.setRows(messageService.selectLetterCount(conversationId));
        //私信列表
        List<Message> letterList = messageService.selectLetters(conversationId, page.getOffset(), page.getRowCnt());
        ArrayList<Map<String, Object>> letters = new ArrayList<>();
        if(!letterList.isEmpty()){
            for(Message message:letterList){
                HashMap<String, Object> map = new HashMap<>();
                map.put("letter",message);
                map.put("fromUser",userService.queryById(message.getFromId()));
                letters.add(map);
            }
            model.addAttribute("letters",letters);
            //私信目标(页面显示来自XXX的私信)
            model.addAttribute("target",getLetterTarget(conversationId));
        }
        return "/site/letter-detail";
    }

    /**
     * 得到会话的另一方
     * @param conversationId
     * @return
     */
    private User getLetterTarget(String conversationId){
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        if(hostHolder.getUser().getId()==id0){
            return userService.queryById(id1);
        }else {
            return userService.queryById(id0);
        }
    }
}
