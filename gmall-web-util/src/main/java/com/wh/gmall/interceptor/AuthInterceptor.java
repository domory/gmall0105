package com.wh.gmall.interceptor;

import com.wh.gmall.annotations.LoginRequired;
import com.wh.gmall.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {



    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //判断被拦截的请求方法的注解是否需要拦截
        HandlerMethod hm= (HandlerMethod)handler;
        LoginRequired methodAnnotation = hm.getMethodAnnotation(LoginRequired.class);
        //是否拦截  如果是null,不需要拦截
        if(methodAnnotation==null){
            return true;
        }

        String token="";
        String oldtoken = CookieUtil.getCookieValue(request, "oldtoken", true);
        if(StringUtils.isNotBlank(oldtoken)){
            token=oldtoken;
        }
        String newtoken = request.getParameter("token");
        if(StringUtils.isNotBlank(newtoken)){
            token=newtoken;
        }

        //是否登录
        boolean loginsuccess = methodAnnotation.loginsuccess();//获得该请求是否必须登录成功才能访问
        if(loginsuccess){
            //必须登录
            //如果不空 需要验证
            if(StringUtils.isBlank(token)){
                //踢回验证中心
            }else {
                //验证用户名 密码
            }
        }else {

        }
        System.out.println("进入拦截器方法");
        return true;
    }
}
