package com.qxy.community.service;

import com.qxy.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/8/27 8:05
 */
public interface DiscussPostService {
    List<DiscussPost> queryPageList(Integer userId, int pageCnt, int rowCnt);

    int queryTotalCnt(Integer userId);

    DiscussPost saveDiscussPost(DiscussPost discussPost);

    DiscussPost queryById(int id);

    int updateCommentCount(int id, int count);

    int updateType(int id, int type);

    int updateStatus(int id, int status);
}
