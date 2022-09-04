package com.qxy.community.interceptor;

import com.qxy.community.constant.CommunityConstant;
import com.qxy.community.entity.LoginTicket;
import com.qxy.community.entity.User;
import com.qxy.community.service.LoginTicketService;
import com.qxy.community.service.UserService;
import com.qxy.community.util.CookieUtil;
import com.qxy.community.util.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/9/1 22:08
 */
@Slf4j
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {
    @Autowired
    LoginTicketService loginTicketService;
    @Autowired
    UserService userService;
    @Autowired
    HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从cookie中获取凭证
        String ticket = CookieUtil.getValue(request, "ticket");
        if (!StringUtils.isBlank(ticket)) {
            //查询凭证
            LoginTicket loginTicket = loginTicketService.selectByTicket(ticket);
            //判断凭证是否有效
            if (loginTicket != null && loginTicket.getStatus() == CommunityConstant.LOGIN_TICKET_STATUS_VALID && loginTicket.getExpired().after(new Date())) {
                //根据凭证查询用户
                User user = userService.queryById(loginTicket.getUserId());
                //在本次请求中持有用户
                hostHolder.setUser(user);//将用户暂存到线程的对象中
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //将用户传给模板，用于显示数据
        User user = hostHolder.getUser();
        log.debug("user="+user);
        if(user!=null&&modelAndView!=null){
            modelAndView.addObject("loginUser",user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //请求结束后清理用户
        hostHolder.clear();
    }
}
