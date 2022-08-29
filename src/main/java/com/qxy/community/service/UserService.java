package com.qxy.community.service;

import com.qxy.community.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

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

    int activation(int userId,String code);
}
