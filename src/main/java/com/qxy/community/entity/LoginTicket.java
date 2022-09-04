package com.qxy.community.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/8/30 22:16
 */
@Data
public class LoginTicket {
    private int id;//主键Id
    private int userId;//用户id
    private String ticket;//登录凭证
    private int status;//状态 0有效，1无效
    private Date expired;//有效期
    private Date createTime;//创建时间
}
