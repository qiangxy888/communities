package com.qxy.community.service.serviceImpl;

import com.qxy.community.dao.LoginTicketMapper;
import com.qxy.community.dao.UserMapper;
import com.qxy.community.entity.LoginTicket;
import com.qxy.community.entity.User;
import com.qxy.community.service.LoginTicketService;
import com.qxy.community.util.CommunityUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/8/30 22:48
 */
@Service
public class LoginTicketServiceImpl implements LoginTicketService {
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private UserMapper userMapper;



    @Override
    public int updateStatusById(int id, int status) {
        return loginTicketMapper.updateStatusById(id,status);
    }

    @Override
    public int updateStatusByTicket(String ticket, int status) {
        return loginTicketMapper.updateStatusByTicket(ticket,status);
    }

    @Override
    public LoginTicket selectByTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }


}
