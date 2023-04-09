package com.qxy.community.controller;

import com.qxy.community.constant.CommunityConstant;
import com.qxy.community.entity.DiscussPost;
import com.qxy.community.entity.Page;
import com.qxy.community.entity.SearchResult;
import com.qxy.community.service.ElasticsearchService;
import com.qxy.community.service.LikeService;
import com.qxy.community.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qxy
 * @version 1.0
 * @Date 2023/4/1 16:33
 */
@Controller
@Slf4j
public class SearchController {
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;

    // search?keyword=xxx
    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model) {
        //搜索帖子
        SearchResult searchResult = elasticsearchService.searchDiscussPost(keyword, page.getOffset(), page.getRowCnt());
        log.info("搜索结果{} ", searchResult.getTotal());
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        List<DiscussPost> list = searchResult.getList();
        if (list != null) {
            for (DiscussPost post : list) {
                log.info("post{} ", post);
                Map<String, Object> map = new HashMap<>();
                //帖子 和 作者
                map.put("post", post);
                map.put("user", userService.queryById(post.getUserId()));
                // 点赞数目
                map.put("likeCount", likeService.findEntityLikeCount(CommunityConstant.ENTITY_TYPE_POST, post.getId()));

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("keyword", keyword);
        //分页信息
        page.setPath("/search?keyword=" + keyword);
        page.setRows(searchResult.getTotal() == 0 ? 0 : (int) searchResult.getTotal());
        return "/site/search";
    }
}
