package com.qxy.community.service;

import com.qxy.community.entity.DiscussPost;
import com.qxy.community.entity.SearchResult;
import org.springframework.data.domain.Page;

/**
 * @author qxy
 * @version 1.0
 * @Date 2023/4/1 15:48
 */
public interface ElasticsearchService {
    void saveDiscussPost(DiscussPost post);
    void deleteDiscussPost(int id);

    /**
     *
     * @param keyword 搜索的关键字
     * @param current 显示第几页
     * @param limit 每页显示条数
     * @return
     */
    SearchResult searchDiscussPost(String keyword, int current, int limit);
}
