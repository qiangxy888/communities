package com.qxy.community.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/8/27 7:40
 */
@Data
public class DiscussPost {
    private Integer id;//主键
    private Integer userId;//用户id
    private String title;//文章标题
    private String content;//内容
    private int type;//类型 0普通，1置顶
    private int status;//状态 0正常 1精华 2拉黑
    private Date createTime;//创建时间
    private int commentCount;// 评论数量
    private double score;//得分
}
