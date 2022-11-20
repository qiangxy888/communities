package com.qxy.community.controller;

import com.qxy.community.annotation.LoginRequired;
import com.qxy.community.entity.User;
import com.qxy.community.service.LikeService;
import com.qxy.community.service.UserService;
import com.qxy.community.util.CommunityUtil;
import com.qxy.community.util.CookieUtil;
import com.qxy.community.util.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.jws.WebParam;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/9/3 18:52
 */
@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private LikeService likeService;
    @Value("${community.path.upload}")
    private String uploadPath;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * 访问设置账号页面
     *
     * @return
     */
    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    /**
     * 上传头像
     * @param headerImage
     * @param model
     * @return
     */
    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeadUrl(@Param("headerImage") MultipartFile headerImage,
                                Model model) {
        if (headerImage == null || StringUtils.isBlank(headerImage.getOriginalFilename())) {
            model.addAttribute("error", "您还没有选择图片");
            return "/site/setting";
        }
        //判断文件格式
        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf(".") + 1);
        if (StringUtils.isBlank(suffix) || (!suffix.equals("png") && !suffix.equals("jpg") && !suffix.equals("jpeg"))) {
            model.addAttribute("error", "文件的格式不正确");
            return "/site/setting";
        }
        //生成随机文件名
        filename = CommunityUtil.generateUUID() + "." + suffix;
        //确定文件的存放路径
        File dest = new File(uploadPath + "/" + filename);
        try {
            //将当前文件内容写入到目标路径中
            headerImage.transferTo(dest);
        } catch (IOException e) {
            log.error("上传文件失败" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常" + e.getMessage());
        }
        //更新当前用户头像的路径（web访问路径）
        //http://localhost:8080/community/user/header/xxx.png
        //获取当前用户
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + filename;
        userService.updateHeaderUrl(user.getId(), headerUrl);
        return "redirect:/index";
    }

    /**
     * 获取图片（二进制文件）
     */
    @RequestMapping(path = "/header/{filename}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response) {
        //服务器存放路径
        filename = uploadPath + "/" + filename;
        //文件的后缀
        String suffix = filename.substring(filename.lastIndexOf(".") + 1);
        //响应图片
        response.setContentType("image/" + suffix);
        //获取字节流
        try (OutputStream os = response.getOutputStream();
             FileInputStream fis = new FileInputStream(filename);) {
            //批量输入输出
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            log.error("读取头像失败" + e.getMessage());
        }
    }

    /**
     * 修改密码
     * @param request
     * @param model
     * @return
     */
    @LoginRequired
    @RequestMapping(path = "/submit",method = RequestMethod.POST)
    public String updatePassword(HttpServletRequest request,Model model){
        System.out.println(request);
        if(request==null){
            new IllegalArgumentException("参数为空");
        }
        //判空
        String oldPassword = request.getParameter("oldPassword");
        if(StringUtils.isBlank(oldPassword)){
            model.addAttribute("oldPasswordMsg","旧密码不能为空");
            return "/site/setting";
        }
        String newPassword = request.getParameter("newPassword");
        if(StringUtils.isBlank(newPassword)){
            model.addAttribute("newPasswordMsg","新密码不能为空");
            return "/site/setting";
        }
        String confirmPassword = request.getParameter("confirmPassword");
        if(StringUtils.isBlank(confirmPassword)){
            model.addAttribute("confirmPasswordMsg","确认密码不能为空");
            return "/site/setting";
        }
        //获取当前用户
        User user = hostHolder.getUser();
        String salt = user.getSalt();
        //判断输入的原密码是否有误
        oldPassword=CommunityUtil.md5(oldPassword+salt);
        if(StringUtils.isBlank(user.getPassword())||!oldPassword.equals(user.getPassword())){
            model.addAttribute("oldPasswordMsg","原密码有误");
            return "/site/setting";
        }
        //判断两次输入新密码是否一致
        newPassword=CommunityUtil.md5(newPassword+salt);
        confirmPassword=CommunityUtil.md5(confirmPassword+salt);
        if(!confirmPassword.equals(newPassword)){
            model.addAttribute("confirmPasswordMsg","两次输入新密码不一致");
            return "/site/setting";
        }
        userService.updatePassword(user.getId(),newPassword);
        return "redirect:/index";
    }
    //个人主页
    @RequestMapping(path = "/profile/{userId}" ,method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId,Model model){
        User user = userService.queryById(userId);
        if(user==null){
            throw new RuntimeException("该用户不存在");
        }
        //用户
        model.addAttribute("user",user);
        //点赞数量
        long likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);
        return "/site/profile";
    }
}
