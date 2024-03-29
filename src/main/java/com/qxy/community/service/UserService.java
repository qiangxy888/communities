package com.qxy.community.service;

import com.qxy.community.entity.LoginTicket;
import com.qxy.community.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/8/27 8:54
 */
public interface UserService {
    User queryById(int userId);

    User queryByName(String username);

    User queryByEmail(String email);

    int save(User user);

    int updateUsername(int id, String username);

    int updateStatus(int id, int status);

    int updatePassword(int id, String password);

    int updateHeaderUrl(int id, String headerUrl);

    int deleteUserById(int id);

    Map<String, Object> register(User user);

    int activation(int userId, String code);

    Map<String, Object> login(String username, String password, int expiredSeconds);

    void logout(String ticket);

    /**
     * 查询登录凭证
     * @param ticket
     * @return
     */
    LoginTicket findLoginTicket(String ticket);

    Collection<? extends GrantedAuthority> getAuthorities(int userId);

}
