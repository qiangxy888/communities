package com.qxy.community.service.serviceImpl;

import com.qxy.community.constant.CommunityConstant;
import com.qxy.community.dao.LoginTicketMapper;
import com.qxy.community.dao.UserMapper;
import com.qxy.community.entity.LoginTicket;
import com.qxy.community.entity.User;
import com.qxy.community.service.UserService;
import com.qxy.community.util.CommunityUtil;
import com.qxy.community.util.MailClientUtil;
import com.qxy.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.concurrent.TimeUnit;

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
    //    @Autowired
//    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public User queryById(int userId) {
//        return userMapper.queryById(userId);
        //先尝试从缓存中取数据
        User user = getCache(userId);
        if (user == null) {
            user = initCache(userId);
        }
        return user;
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
        int rows = userMapper.updateUsername(id, username);
        clearCache(id);
        return rows;
    }

    @Override
    public int updateStatus(int id, int status) {
        int rows = userMapper.updateStatus(id, status);
        clearCache(id);
        return rows;
    }

    /**
     * 修改密码
     *
     * @param id
     * @param password
     * @return
     */
    @Override
    public int updatePassword(int id, String password) {
        int rows = userMapper.updatePassword(id, password);
        clearCache(id);
        return rows;
    }

    /**
     * 更新用户头像
     *
     * @param id        用户id
     * @param headerUrl 头像路径
     * @return
     */
    @Override
    public int updateHeaderUrl(int id, String headerUrl) {
        int rows = userMapper.updateHeaderUrl(id, headerUrl);
        clearCache(id);
        return rows;
    }

    @Override
    public int deleteUserById(int id) {
        int rows = userMapper.deleteUserById(id);
        clearCache(id);
        return rows;
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
        //TODO 为了注册多个测试账号，暂时取消邮箱唯一性的校验
     /*   User queryByEmail = userMapper.queryByEmail(user.getEmail());
        if (queryByEmail != null) {
            map.put("emailMsg", "该邮箱已被注册");
            return map;
        }*/
        //保存数据到数据库
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));//生成五位数的salt，用于提高密码复杂度
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));//给密码加密
        user.setStatus(0);//设置状态 未激活
        user.setType(0);//设置类型 普通用户
        user.setActivationCode(CommunityUtil.generateUUID());//生成激活码
        //TODO 设置用户默认头像
//        user.setHeaderUrl(String.format("http://images.nowvoder.com/head/%dt.png", new Random().nextInt(1000)));//生成自带随机头像
        user.setHeaderUrl("../resources/static/image/header1.jpg");//系统默认头像
        user.setCreateTime(new Date());//设置创建时间为当前时间
        userMapper.save(user);//添加用户
        //发送激活文件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://localhost:8080/community/activation/userId/activationCode
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClientUtil.sendMail(user.getEmail(), "激活账号", content);
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
            clearCache(userId);
            return CommunityConstant.ACTIVATION_SUCCESS;
        } else {
            return CommunityConstant.ACTIVATION_FAIL;
        }
    }

    /**
     * 验证用户登录
     *
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
//        loginTicketMapper.insertLoginTicket(loginTicket);
        String ticketKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(ticketKey, loginTicket);
        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    /**
     * 退出登录
     *
     * @param ticket
     */
    public void logout(String ticket) {
//        loginTicketMapper.updateStatusByTicket(ticket,CommunityConstant.LOGIN_TICKET_STATUS_INVALID);
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        loginTicket.setStatus(CommunityConstant.LOGIN_TICKET_STATUS_INVALID);
        redisTemplate.opsForValue().set(ticketKey, loginTicket);
    }

    @Override
    public LoginTicket findLoginTicket(String ticket) {
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        return loginTicket;
    }

    //1.查询时优先从缓存中取值
    private User getCache(int userId) {
        String userKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(userKey);
    }

    //2.取不到时初始化缓存数据
    private User initCache(int userId) {
        User user = userMapper.queryById(userId);
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(userKey, user, 3600, TimeUnit.SECONDS);
        return (User) redisTemplate.opsForValue().get(userKey);
    }

    //3.数据更新时清除缓存数据
    private void clearCache(int userId) {
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(userKey);
    }

    public Collection<? extends GrantedAuthority> getAuthorities(int userId) {
        User user = this.queryById(userId);

        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {
                switch (user.getType()) {
                    case 1:
                        return CommunityConstant.AUTHORITY_ADMIN;
                    case 2:
                        return CommunityConstant.AUTHORITY_MODERATOR;
                    default:
                        return CommunityConstant.AUTHORITY_USER;
                }
            }
        });
        return list;
    }

}
