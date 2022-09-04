package com.qxy.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/9/3 18:52
 */
@Controller
@RequestMapping("/index")
public class UserController {
    @RequestMapping(path = "/set",method = RequestMethod.GET)
    public void set(){

    }
}
