package com.qxy.community.service.serviceImpl;

import com.qxy.community.dao.DiscussPostMapper;
import com.qxy.community.entity.DiscussPost;
import com.qxy.community.service.DiscussPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/8/27 8:07
 */
@Service
public class DiscussPostServiceImpl implements DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Override
    public List<DiscussPost> queryPageList(Integer userId, int pageCnt, int rowCnt) {
        List<DiscussPost> discussPosts = discussPostMapper.queryPageList(userId, pageCnt, rowCnt);
        return discussPosts;
    }

    @Override
    public int queryTotalCnt(Integer userId) {
        int cnt = discussPostMapper.queryTotalCnt(userId);
        return cnt;
    }
}
