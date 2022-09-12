package com.qxy.community.util;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/8/28 9:21
 */

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
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

    /**
     * 将参数装入到json对象中并返回字符串
     * @param code 编码
     * @param msg 信息
     * @param map
     * @return
     */
    public static String getJsonString(Integer code, String msg, Map<String,Object> map){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        if(map!=null){
            //遍历map集合，取数据装入json对象
            for(String key:map.keySet()){
                json.put(key,map.get(key));
            }
        }
        return json.toJSONString();
    }
    public static String getJsonString(Integer code,String msg){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        return json.toJSONString();
    }
    public static String getJsonString(Integer code){
        JSONObject json = new JSONObject();
        json.put("code",code);
        return json.toJSONString();
    }

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("name","lll");
        map.put("age",18);
        System.out.println(getJsonString(0,"success",map));
    }
}
