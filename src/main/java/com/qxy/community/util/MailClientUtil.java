package com.qxy.community.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


/**
 * @author qxy
 * @version 1.0
 * @Date 2022/8/28 7:33
 */
@Component
public class MailClientUtil {
    private static final Logger logger = LoggerFactory.getLogger(MailClientUtil.class);
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;//发件人邮箱

    /**
     * 发送优先
     *
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    public void sendMail(String to, String subject, String content) {
        try {
            //构建邮件主体
            MimeMessage message = mailSender.createMimeMessage();
            //构建邮件详细内容
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(from);//设置发件人
            helper.setTo(to);//设置收件人
            helper.setSubject(subject);
            helper.setText(content, true);//允许支持HTML文件
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            logger.error("发送邮件失败" + e.getMessage());
        }
    }
}
