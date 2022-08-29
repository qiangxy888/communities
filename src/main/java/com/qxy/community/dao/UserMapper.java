package com.qxy.community.dao;

import com.qxy.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
 * @author qxy
 * @version 1.0
 * @Date 2022/8/22 21:47
 */
@Mapper
public interface UserMapper {
    User queryById(@Param("id") int userId);

    User queryByName(String username);

    User queryByEmail(String email);

    int save(User user);

    int updateUsername(int is,String username);

    int updateStatus(int id,int status);

    int updatePassword(int id,String password);

    int updateHeaderUrl(int id, String headerUrl);

    int deleteUserById(int id);

}
