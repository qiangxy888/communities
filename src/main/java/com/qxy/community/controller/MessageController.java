package com.qxy.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.qxy.community.constant.CommunityConstant;
import com.qxy.community.entity.Message;
import com.qxy.community.entity.Page;
import com.qxy.community.entity.User;
import com.qxy.community.service.MessageService;
import com.qxy.community.service.UserService;
import com.qxy.community.util.CommunityUtil;
import com.qxy.community.util.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import javax.jws.WebParam;
import javax.swing.text.html.HTMLDocument;
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
     *
     * @param model
     * @param page
     * @return
     */
    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page) {
        User user = hostHolder.getUser();
        //分页消息
        page.setRowCnt(5);
        page.setPath("/letter/list");
        page.setRows(messageService.selectConversationCount(user.getId()));
        //查询会话列表
        List<Message> conversationList = messageService.selectConversation(user.getId(), page.getOffset(), page.getRowCnt());
        log.info("conversationList>>>>>>>>>" + conversationList);
        ArrayList<Map<String, Object>> conversations = new ArrayList<>();
        if (!CollectionUtils.isEmpty(conversationList)) {
            for (Message message : conversationList) {
                log.info("message>>>>>>>>>" + message);
                HashMap<String, Object> map = new HashMap<>();
                map.put("conversation", message);
                int letterCount = messageService.selectLetterCount(message.getConversationId());
                map.put("letterCount", letterCount);
                log.info("letterCount>>>>>>>>" + letterCount);
                int unreadCount = messageService.selectLetterUnreadCount(user.getId(), message.getConversationId());
                map.put("unreadCount", unreadCount);
                log.info("unreadCount>>>>>>>>>>" + unreadCount);
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                log.info("targetId>>>>>>>>>>>" + targetId);
                map.put("target", userService.queryById(targetId));
                conversations.add(map);
            }
            model.addAttribute("conversations", conversations);
        }
        //查询总未读消息数量
        int letterUnreadCount = messageService.selectLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        int noticeUnreadCount = messageService.selectUnreadSysMsgCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);
        return "/site/letter";
    }

    /**
     * 显示某会话的详细私信列表
     *
     * @param conversationId
     * @param model
     * @param page
     * @return
     */
    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Model model, Page page) {
        //分页信息
        page.setRowCnt(5);//每页显示条数
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.selectLetterCount(conversationId));
        //私信列表
        List<Message> letterList = messageService.selectLetters(conversationId, page.getOffset(), page.getRowCnt());
        ArrayList<Map<String, Object>> letters = new ArrayList<>();
        if (!letterList.isEmpty()) {
            for (Message message : letterList) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.queryById(message.getFromId()));
                letters.add(map);
            }
            model.addAttribute("letters", letters);
            //私信目标(页面显示来自XXX的私信)
            model.addAttribute("target", getLetterTarget(conversationId));
        }
        //设置已读
        List<Integer> ids = getUnreadIds(letterList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }
        return "/site/letter-detail";
    }

    /**
     * 得到会话的另一方
     *
     * @param conversationId
     * @return
     */
    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        if (hostHolder.getUser().getId() == id0) {
            return userService.queryById(id1);
        } else {
            return userService.queryById(id0);
        }
    }

    /**
     * 发送私信
     *
     * @param toName
     * @param content
     * @return
     */
    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content) {
        if (StringUtils.isBlank(toName) || StringUtils.isBlank(content)) {
            throw new IllegalArgumentException("参数不能为空");
        }
        //根据姓名得到收件方user
        User target = userService.queryByName(toName);
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        message.setContent(content);
        //拼接conversationId，数字小的id在前
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setCreateTime(new Date());
        messageService.sendMessage(message);
        return CommunityUtil.getJsonString(0);
    }

    /**
     * 获取所有未读消息id
     *
     * @param letterList
     * @return
     */
    public List<Integer> getUnreadIds(List<Message> letterList) {
        ArrayList<Integer> ids = new ArrayList<>();
        if (!letterList.isEmpty()) {
            for (Message letter : letterList) {
                if (hostHolder.getUser().getId() == letter.getToId() &&
                        letter.getStatus() == CommunityConstant.UNREAD_MESSAGE) {
                    ids.add(letter.getId());
                }
            }
        }
        return ids;
    }

    /**
     * 系统通知
     *
     * @param model
     */
    @RequestMapping(path = "/notice/list", method = RequestMethod.GET)
    public String showSysMsg(Model model) {
        User user = hostHolder.getUser();
        //获取关注、点赞、评论的最新一条通知
        Map<String, Object> messageVo;
        messageVo = getMessageVo(user.getId(), CommunityConstant.TOPIC_LIKE);
        model.addAttribute("likeSysMsg", messageVo);
        messageVo = getMessageVo(user.getId(), CommunityConstant.TOPIC_COMMENT);
        model.addAttribute("commentSysMsg", messageVo);
        messageVo = getMessageVo(user.getId(), CommunityConstant.TOPIC_FOLLOW);
        model.addAttribute("followSysMsg", messageVo);
        //获取未读私信数量
        int letterUnreadCount = messageService.selectLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        //获取未读系统通知数量
        int noticeUnreadCount = messageService.selectUnreadSysMsgCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);
        return "/site/notice";
    }

    public Map<String, Object> getMessageVo(int userId, String topic) {
        Message sysMsg = messageService.selectLastedSysMsg(userId, topic);
        if (sysMsg == null) {
            log.info("系统通知列表——" + topic + "为空");
            return null;
        } else {
            Map<String, Object> messageVo = new HashMap<>();
            messageVo.put("message", sysMsg);
            String content = HtmlUtils.htmlUnescape(sysMsg.getContent());//恢复转义字符
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
            messageVo.put("user", userService.queryById(Integer.parseInt(data.get("userId").toString())));
            messageVo.put("entityType", data.get("entityType"));
            messageVo.put("entityId", data.get("entityId"));
            if (data.containsKey("postId")) {
                messageVo.put("postId", data.get("postId"));
            }
            int count = messageService.selectSysMsgCount(userId, topic);
            messageVo.put("count", count);
            int unread = messageService.selectUnreadSysMsgCount(userId, topic);
            messageVo.put("unread", unread);
            return messageVo;
        }
    }

    @RequestMapping(path = "/notice/detail/{topic}", method = RequestMethod.GET)
    public String getSysMsgList(@PathVariable("topic") String topic, Model model, Page page) {
        User user = hostHolder.getUser();
        int count = messageService.selectSysMsgCount(user.getId(), topic);
        //设置分页数据
        page.setPath("/notice/detail/" + topic);
        page.setRows(count);
        page.setRowCnt(5);
        //消息列表
        List<Message> messages = messageService.selectSysMsgList(user.getId(), topic, page.getOffset(), page.getRowCnt());
        List<Map<String, Object>> messageList = new ArrayList<>();
        if (!messages.isEmpty()) {
            for (Message message : messages) {
                String content = HtmlUtils.htmlUnescape(message.getContent());
                HashMap data = JSONObject.parseObject(content, HashMap.class);
                HashMap<String, Object> detailMap = new HashMap<>();
                detailMap.put("message", message);
                detailMap.put("user", userService.queryById(Integer.parseInt(data.get("userId").toString())));
                detailMap.put("entityId",data.get("entityId"));
                detailMap.put("entityType",data.get("entityType"));
                //通知作者
                detailMap.put("fromUser", userService.queryById(message.getFromId()));
                detailMap.put("postId", data.get("postId"));//关注类通知没有这个值
                messageList.add(detailMap);
            }
        }
        model.addAttribute("messageList", messageList);
        //设置已读
        List<Integer> ids = getUnreadIds(messages);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }
        return "site/notice-detail";
    }
}
