package com.qxy.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.util.Properties;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/8/28 21:09
 */
@Configuration
public class KaptchaConfig {
    @Bean
    public Producer kaptchaProducer() {
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width", "100");//设置图片宽度
        properties.setProperty("kaptcha.image.height", "40");//设置图片高度
        properties.setProperty("kaptcha.textproducer,font.size", "32");//设置字体大小
        properties.setProperty("kaptcha.textproducer.font.color", "0,0,0");//设置字体颜色
        properties.setProperty("kaptcha.texstproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");//设置字符范围
        properties.setProperty("kaptcha.textproducer.char.length", "4");//设置字符数量
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        Config config = new Config(properties);
        kaptcha.setConfig(config);
        return kaptcha;
    }
}
