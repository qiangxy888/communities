package com.qxy.community.entity;

import java.util.List;

/**
 * @author qxy
 * @version 1.0
 * @Date 2023/4/1 16:13
 */

/**
 * 自定义实体
 * 用于暂存es中查询到的列表和总行数
 */
public class SearchResult {
    private List<DiscussPost> list;
    private long total;
    public SearchResult(List<DiscussPost> list, long total) {
        this.list = list;
        this.total = total;
    }

    public List<DiscussPost> getList() {
        return list;
    }

    public void setList(List<DiscussPost> list) {
        this.list = list;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
