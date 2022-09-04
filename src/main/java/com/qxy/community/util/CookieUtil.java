package com.qxy.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/9/4 10:31
 */
public class CookieUtil {
    public static String getValue(HttpServletRequest request,String name){
        if(request==null||name==null){
            new  IllegalArgumentException("参数为空");
        }
        Cookie[] cookies = request.getCookies();
        if(cookies!=null){
            for(Cookie cookie:cookies){
                if(cookie.getName().equals(name)){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
