package com.wh.gmall.interceptor;

import com.alibaba.fastjson.JSON;
import com.wh.gmall.annotations.LoginRequired;
import com.wh.gmall.util.CookieUtil;
import com.wh.gmall.util.HttpclientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {



    @Override
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

        //验证token
        String success="fail";
        Map<String,String> successMap=new HashMap<>();
        if(StringUtils.isNotBlank(token)){
            //跳转
             String  ip="127.0.0.1"; //如果查不到ip 则说明此时没有通过客户端和nginx访问 直接返回空
//            String header = request.getHeader("x-forwarded-for");
//            String remoteAddr = request.getRemoteAddr();
         String successjson = HttpclientUtil.doGet("http://localhost:8085/verify?token=" + token + "&currentIp="+ip);
             successMap= JSON.parseObject(successjson,Map.class);
             success = successMap.get("status");
        }

        //是否登录
        boolean loginsuccess = methodAnnotation.loginsuccess();//获得该请求是否必须登录成功才能访问
        if(loginsuccess){
            //必须登录
           if(!success.equals("success")){
               //没有认证成功 token为假 重定向回登录
               StringBuffer requestURL = request.getRequestURL();
               request.getSession().setAttribute("ReturnUrl",requestURL);
               response.sendRedirect("http://localhost:8085/index?ReturnUrl="+requestURL);
               return false;
           }else {
               //验证通过
               request.setAttribute("memberId",successMap.get("memberId"));
               request.setAttribute("nickname",successMap.get("nickname"));
               //通过验证 将token写入cookie
               if(StringUtils.isNotBlank(token)){
                   CookieUtil.setCookie(request,response,"oldtoken",token,60*60,true);}
           }
        }else {
            if(success.equals("success")){
                request.setAttribute("memberId",successMap.get("memberId"));
                request.setAttribute("nickname",successMap.get("nickname"));
                //通过验证 将token写入cookie
                if(StringUtils.isNotBlank(token)){
                    CookieUtil.setCookie(request,response,"oldtoken",token,60*60,true);}
            }
        }
        return true;
    }
}
