package com.qxy.community.dao.elasticSearch;

import com.qxy.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author qxy
 * @version 1.0
 * @Date 2023/4/10 21:00
 */
@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost,Integer> {
}
