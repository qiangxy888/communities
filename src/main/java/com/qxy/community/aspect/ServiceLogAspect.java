package com.qxy.community.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/9/29 19:27
 */
@Component
@Aspect
public class ServiceLogAspect {
    private static final Logger log = LoggerFactory.getLogger(ServiceLogAspect.class);

    @Pointcut("execution(* com.qxy.community.service.*.*(..))")
    public void pointcut(){

    }
//    @Before("pointcut()")
//    public void before(JoinPoint joinPoint){//参数：连接点
//        //用户[1.2.3.4]，在[某时间]，访问了[com.qxy.community.service.xxx]
//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        HttpServletRequest request = attributes.getRequest();
//        String ip = request.getRemoteHost();
//        String now = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
//        String target = joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName();//得到该连接点的类名和方法名
//        log.info(String.format("用户[%s],在[%s],访问了[%s]",ip,now,target));
//    }

    @Pointcut("execution(* com.qxy.community.controller.*.*(..))")
    public void controllerPointcut(){

    }
    @Before("controllerPointcut()")
    public void beforeController(JoinPoint joinPoint){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes==null){
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getRemoteHost();
        String now = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
        String target = joinPoint.getSignature().getDeclaringTypeName()+"."+ joinPoint.getSignature().getName();
        log.info(String.format("用户[%s],在[%s],访问了[%s]",ip,now,target));
    }
}
