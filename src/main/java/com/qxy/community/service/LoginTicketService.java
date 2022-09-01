package com.qxy.community.service;

import com.qxy.community.entity.LoginTicket;

import java.util.Map;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/8/30 22:47
 */
public interface LoginTicketService {


    int updateStatusById(int id, int status);

    int updateStatusByTicket(String ticket,int status);

    LoginTicket selectByTicket(String ticket);
}
