package com.qxy.community.dao;

import com.qxy.community.entity.LoginTicket;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/8/30 22:33
 */
@Mapper
@Deprecated
public interface LoginTicketMapper {
    int insertLoginTicket(LoginTicket loginTicket);

    int updateStatusById(int id, int status);

    int updateStatusByTicket(String ticket,int status);

    LoginTicket selectByTicket(String ticket);
}
