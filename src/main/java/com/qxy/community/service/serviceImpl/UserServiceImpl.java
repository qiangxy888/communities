package com.qxy.community.service.serviceImpl;

import com.qxy.community.constant.CommunityConstant;
import com.qxy.community.dao.LoginTicketMapper;
import com.qxy.community.dao.UserMapper;
import com.qxy.community.entity.LoginTicket;
import com.qxy.community.entity.User;
import com.qxy.community.service.UserService;
import com.qxy.community.util.CommunityUtil;
import com.qxy.community.util.MailClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/8/27 8:59
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private MailClientUtil mailClientUtil;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Value("${community.path.domain}")
    private String domain;
    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Override
    public User queryById(int userId) {
        return userMapper.queryById(userId);
    }

    @Override
    public User queryByName(String username) {
        return userMapper.queryByName(username);
    }

    @Override
    public User queryByEmail(String email) {
        return userMapper.queryByEmail(email);
    }

    @Override
    public int save(User user) {
        int save = userMapper.save(user);
        return save;
    }

    @Override
    public int updateUsername(int id, String username) {
        return userMapper.updateUsername(id, username);
    }

    @Override
    public int updateStatus(int id, int status) {
        return userMapper.updateStatus(id, status);
    }

    @Override
    public int updatePassword(int id, String password) {
        return userMapper.updatePassword(id, password);
    }

    @Override
    public int updateHeaderUrl(int id, String headerUrl) {
        return userMapper.updateHeaderUrl(id, headerUrl);
    }

    @Override
    public int deleteUserById(int id) {
        return userMapper.deleteUserById(id);
    }

    @Override
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();
        //空值处理
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空");
            return map;
        }
        //验证账号
        User queryByName = userMapper.queryByName(user.getUsername());
        if (queryByName != null) {
            map.put("usernameMsg", "该账号已被注册");
            return map;
        }
        User queryByEmail = userMapper.queryByEmail(user.getEmail());
        if (queryByEmail != null) {
            map.put("emailMsg", "该邮箱已被注册");
            return map;
        }
        //保存数据到数据库
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));//生成五位数的salt，用于提高密码复杂度
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));//给密码加密
        user.setStatus(0);//设置状态 未激活
        user.setType(0);//设置类型 普通用户
        user.setActivationCode(CommunityUtil.generateUUID());//生成激活码
        user.setHeaderUrl(String.format("http://images.nowvoder.com/head/%dt.png", new Random().nextInt(1000)));//生成自带随机头像
        user.setCreateTime(new Date());//设置创建时间为当前时间
        userMapper.save(user);//添加用户
        //发送激活文件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://localhost:8080/community/activayion/userId/activationCode
        String url = domain + contextPath + "/activation/" + user.getId() +"/"+ user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClientUtil.sendMail(user.getEmail(), "激活账号",content);
        return map;
    }

    /**
     * 更改用户的激活状态
     *
     * @param userId 用户Id
     * @param code   激活码
     * @return
     */
    @Override
    public int activation(int userId, String code) {
        User user = userMapper.queryById(userId);
        if (user == null) {
            return CommunityConstant.USER_NOT_EXISTS;//用户不存在
        } else if (user.getStatus() == 1) {
            return CommunityConstant.ACTIVATION_REPEAT;//重复激活
        } else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId, 1);
            return CommunityConstant.ACTIVATION_SUCCESS;
        } else {
            return CommunityConstant.ACTIVATION_FAIL;
        }
    }

    /**
     * 验证用户登录
     * @param username
     * @param password
     * @param expiredSeconds
     * @return
     */
    @Override
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();
        //对传入的数据进行空值判断
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        //验证账号
        User userByName = userMapper.queryByName(username);
        if (userByName == null) {
            map.put("usernameMsg", "该账号不存在");
            return map;
        }
        if (userByName.getStatus() == 0) {
            map.put("usernameMsg", "账号未激活");
            return map;
        }
        //验证密码
        password = CommunityUtil.md5(password + userByName.getSalt());
        if (!password.equals(userByName.getPassword())) {
            map.put("passwordMsg", "密码错误");
            return map;
        }
        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userByName.getId());
        loginTicket.setStatus(CommunityConstant.LOGIN_TICKET_STATUS_VALID);
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicket.setCreateTime(new Date());
        loginTicketMapper.insertLoginTicket(loginTicket);
        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    /**
     * 退出登录
     * @param ticket
     */
    public void logout(String ticket){
        loginTicketMapper.updateStatusByTicket(ticket,CommunityConstant.LOGIN_TICKET_STATUS_INVALID);
    }
}
