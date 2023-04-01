package com.qxy.community.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/8/22 21:33
 */
@Data
@ToString
public class User {
    private int id;//主键
    private String username;//用户名
    private String password;//密码
    private String salt;//盐(加强密码)
    private String email;//邮箱
    private int type;//类型 0普通用户 1超级管理员 2版主
    private int status;//状态 0未激活 1已激活
    private String activationCode;//激活码
    private String headerUrl;//用户头像路径
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;//创建时间
}
