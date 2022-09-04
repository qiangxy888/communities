package com.qxy.community.controller;

import com.qxy.community.entity.DiscussPost;
import com.qxy.community.entity.Page;
import com.qxy.community.entity.User;
import com.qxy.community.service.DiscussPostService;
import com.qxy.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/8/27 8:11
 */
@Controller
public class HomeController {
    @Autowired
    DiscussPostService discussPostService;
    @Autowired
    UserService userService;
    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page){
        //方法调用前，SpringMVC会自动实例化Model和Page，并将Page注入Model
        //所以在thymeleaf中可以直接访问page对象中的数据
        page.setRows(discussPostService.queryTotalCnt(null));
        page.setPath("/index");
        //获取到首页的帖子
        List<DiscussPost> list = discussPostService.queryPageList(null, page.getOffset(), page.getRowCnt());
        //定义最终用于传到模板的list集合
        ArrayList<Map<String, Object>> discussPosts = new ArrayList<>();
        //遍历集合，根据帖子的userId得到user对象
        for(DiscussPost post :list){
            User user = userService.queryById(post.getUserId());
            Map<String, Object> map = new HashMap<>();
            map.put("user",user);
            map.put("post",post);
            discussPosts.add(map);
        }
        //把准备好的数据传给模板
        model.addAttribute("discussPosts",discussPosts);
        //返回模板
        return "/index";
    }
}