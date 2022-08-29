package com.qxy.community.util;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/8/28 9:21
 */

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

/**
 * 封装生成随机字符串和密码加密功能
 */
public class CommunityUtil {
    /**
     * 生成随机字符串
     * @return
     */
    public static String generateUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }

    /**
     * 加密
     * @param key
     * @return
     */
    public static String md5(String key){
        if(!StringUtils.isBlank(key)){
            return DigestUtils.md5DigestAsHex(key.getBytes());
        }
        return null;
    }
}
