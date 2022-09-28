package com.qxy.community.controller.advice;

import com.qxy.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/9/28 19:37
 */
@ControllerAdvice(annotations = Controller.class)//限定值扫描含@Controller注解的组件
public class ExceptionAdvice {
    public static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({Exception.class})//处理哪些异常，Exception是所有异常的父类，所有异常都处理
    public void handlerException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        //记录日志
        logger.error("服务器发生异常：" + e.getMessage());//异常的概括
        for (StackTraceElement element : e.getStackTrace()) {//把异常所有栈的信息都记录下来
            logger.error(element.toString());
        }
        //给浏览器响应
        //分两种情况，从请求头获取这是一个异步请求还是普通请求，异步请求返回JSON字符串，普通请求返回网页
        String xRequestedWith = request.getHeader("x-requested-with");

        if ("XMLHttpRequest".equals(xRequestedWith)) {//如果请求是异步请求
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJsonString(1, "服务器异常"));
        } else {//如果是普通请求，重定向到错误页面
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}
