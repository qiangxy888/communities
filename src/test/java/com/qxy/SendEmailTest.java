package com.qxy;

import com.qxy.community.util.MailClientUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import sun.security.krb5.Config;

import javax.jws.Oneway;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/8/28 8:06
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunitiesApplication.class)

public class SendEmailTest {
    @Autowired
    private MailClientUtil mailClientUtil;

    @Autowired
    TemplateEngine templateEngine;//模板引擎

    @Test
    public void testCommonEmail() {
        mailClientUtil.sendMail("2374661759@qq.com", "Test", "hello!!");
    }
    @Test
    public void testHtmlEmail(){
        //需要传入相关参数
        Context context = new Context();
        context.setVariable("username","hahaha");
        //把内容注入模板引擎
        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);//打印在控制台直观展示
        //通过工具类方法发送邮件
        mailClientUtil.sendMail("2374661759@qq.com","HtmlTest",content);
    }
}
