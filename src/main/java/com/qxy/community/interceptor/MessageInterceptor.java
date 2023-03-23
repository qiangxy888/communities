package com.qxy.community.interceptor;

import com.qxy.community.entity.Message;
import com.qxy.community.entity.User;
import com.qxy.community.service.MessageService;
import com.qxy.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author qxy
 * @version 1.0
 * @Date 2023/2/12 10:44
 */
@Component
public class MessageInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private MessageService messageService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user!=null && modelAndView!=null){
            int letterUnreadCount = messageService.selectLetterUnreadCount(user.getId(), null);
            int unreadSysMsgCount = messageService.selectUnreadSysMsgCount(user.getId(), null);
            modelAndView.addObject("allUnreadCount",letterUnreadCount+unreadSysMsgCount);
        }
    }
}
