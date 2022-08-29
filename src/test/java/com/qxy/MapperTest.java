package com.qxy;

import com.qxy.community.dao.DiscussPostMapper;
import com.qxy.community.dao.UserMapper;
import com.qxy.community.entity.DiscussPost;
import com.qxy.community.entity.User;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/8/22 22:33
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunitiesApplication.class)
public class MapperTest {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Test
    public void queryById(){
        User user = userMapper.queryById(1);
        System.out.println(user);
    }
    @Test
    public void queryByName(){
        User user = userMapper.queryByName("SYSTEM");
        System.out.println(user);
    }
    @Test
    public void queryPageList(){
        List<DiscussPost> discussPosts = discussPostMapper.queryPageList(null, 1, 10);
        for(DiscussPost discussPost:discussPosts){
            System.out.println(discussPost);
        }
    }
    @Test
    public void queryTotalCnt(){
        int cnt = discussPostMapper.queryTotalCnt(null);
        System.out.println(cnt);
    }
}
