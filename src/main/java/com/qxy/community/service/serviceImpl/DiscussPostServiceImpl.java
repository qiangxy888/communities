package com.qxy.community.service.serviceImpl;

import com.qxy.community.dao.DiscussPostMapper;
import com.qxy.community.entity.DiscussPost;
import com.qxy.community.service.DiscussPostService;
import com.qxy.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

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
    @Autowired
    private SensitiveFilter sensitiveFilter;
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

    @Override
    public DiscussPost saveDiscussPost(DiscussPost discussPost) {
        if(discussPost==null){
            throw new IllegalArgumentException("参数不能为空");
        }
        //转义HTML标记
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        //过滤敏感词
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));

        discussPostMapper.saveDiscussPost(discussPost);
        return discussPost;
    }

    @Override
    public DiscussPost queryById(int id) {
        return this.discussPostMapper.queryById(id);
    }

    @Override
    public int updateCommentCount(int id, int count) {
        return discussPostMapper.updateCommentCount(id,count);
    }
}
