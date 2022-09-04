package com.qxy.community.util;

import com.qxy.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/9/4 10:47
 */

/**
 * 持有用户信息，用于代替session对象
 */
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();
    public void setUser(User user){
        users.set(user);
    }
    public User getUser(){
        return users.get();
    }
    public void clear(){
        users.remove();
    }
}
