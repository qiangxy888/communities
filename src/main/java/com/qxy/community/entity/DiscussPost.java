package com.qxy.community.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.util.Date;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/8/27 7:40
 */
@Data
@Setting()
@Document(indexName = "discusspost")
public class DiscussPost {
    @Id
    private Integer id;//主键
    @Field(type = FieldType.Integer)
    private Integer userId;//用户id
    @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer = "ik_smart")
    private String title;//文章标题
    @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer = "ik_smart")
    private String content;//内容
    @Field(type = FieldType.Integer)
    private int type;//类型 0普通，1置顶
    @Field(type = FieldType.Integer)
    private int status;//状态 0正常 1精华 2拉黑
    @Field(type = FieldType.Date,format = DateFormat.basic_date)
    private Date createTime;//创建时间
    @Field(type = FieldType.Integer)
    private int commentCount;// 评论数量
    @Field(type = FieldType.Double)
    private double score;//得分
}
