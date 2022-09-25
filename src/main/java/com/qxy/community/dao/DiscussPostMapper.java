package com.qxy.community.dao;

import com.qxy.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/8/27 7:45
 */
@Mapper
public interface DiscussPostMapper {
    List<DiscussPost> queryPageList(@Param("userId") Integer userId, int pageCnt, int rowCnt);

    int queryTotalCnt(@Param("userId") Integer userId);

    int saveDiscussPost(DiscussPost discussPost);

    DiscussPost queryById(int id);

    int updateCommentCount(int id,int count);
}
