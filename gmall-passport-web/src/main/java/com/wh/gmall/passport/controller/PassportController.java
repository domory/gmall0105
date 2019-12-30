package com.wh.gmall.passport.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.wh.gmall.bean.UmsMember;
import com.wh.gmall.service.UserService;
import com.wh.gmall.util.HttpclientUtil;
import com.wh.gmall.util.JwtUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @author DOMORY
 */
@Controller
@CrossOrigin
public class PassportController {

    @Reference
    UserService userService;


    @RequestMapping("verify")
    @ResponseBody
    public String verify(String token, String currentIp ) {
        //用jwt校验token真假
        Map<String, String> map = new HashMap<>();
        Map<String, Object> decode = JwtUtil.decode(token, "whgmall", currentIp);
        if (decode != null) {
            map.put("status", "success");
            map.put("memberId", decode.get("memberId").toString());
            map.put("nickname", (String) decode.get("nickname"));
        } else {
            map.put("status", "fail");
        }
        return JSON.toJSONString(map);
    }


    @RequestMapping("login")
    @ResponseBody
    public String login(UmsMember umsMember, HttpServletRequest request) {
        String token = "";
        UmsMember umsMemberlogin = userService.login(umsMember);
        if (umsMemberlogin != null) {
            //登录成功
            //制作token
            Map<String, Object> usermap = new HashMap<>();
            usermap.put("memberId", umsMemberlogin.getId());
            usermap.put("nickname", umsMemberlogin.getNickname());
            //获取ip

            String  ip="127.0.0.1";
//            String header = request.getHeader("x-forwarded-for");
//            if(StringUtils.isNotBlank(header)){
//            System.out.println(header);}
//            String remoteAddr = request.getRemoteAddr();
//            System.out.println(remoteAddr);
            token = JwtUtil.encode("whgmall", usermap, ip);
            //将token存入redis
            userService.addToken(umsMemberlogin.getId(), token);
        } else {
            //登录失败
            token = "fail";
        }
        return token;
    }

    @RequestMapping("vlogin")
    public String vlogin(String code, HttpServletRequest request, HttpSession session) {
        //获取授权码access_token
        Map<String, String> map = new HashMap<>();
        String s3="https://api.weibo.com/oauth2/access_token?";
        map.put("client_id","2901863716");
        map.put("client_secret","348e94a64b1b7d67a8ff25a81e4e77fa");
        map.put("grant_type","authorization_code");
        map.put("redirect_uri","http://192.168.1.155:8085/vlogin");
        map.put("code",code);
        String accessToken = HttpclientUtil.doPost(s3,map);
        Map<String,Object> accessTokenmap = JSON.parseObject(accessToken, Map.class);
        //access_token获得用户信息
        String access_token =(String)accessTokenmap.get("access_token");
        String uid = (String)accessTokenmap.get("uid");
        String s4="https://api.weibo.com/2/users/show.json?access_token="+access_token+"&uid="+uid;
        String userJson = HttpclientUtil.doGet(s4);
        Map<String,Object> userinfo = JSON.parseObject(userJson, Map.class);
        //把用户信息存到数据库
        UmsMember umsMember=new UmsMember();
        umsMember.setAccessCode(code);
        umsMember.setAccessToken(access_token);
        umsMember.setSourceType(2);
        String gender =(String) userinfo.get("gender");
        Long sourceUid=(Long)userinfo.get("id");
        umsMember.setSourceUid((Long)userinfo.get("id"));
        umsMember.setCity((String)userinfo.get("location"));
        umsMember.setNickname((String)userinfo.get("screen_name"));
        Integer g=null;
        if(gender.equals("m")){
            g=2;
        }else if(gender.equals("f")){
            g=1;
        }else {
            g=0;
        }
        umsMember.setGender(g);
        userService.addOauthUser(umsMember);
        //根据第三方uid拿到在数据库中的id和nickname
        UmsMember oauthUser = userService.getOauthUser(sourceUid);
        Long memeberId = oauthUser.getId();
        String nickname = oauthUser.getNickname();
        //生成jwt 重定向到首页
        String token="";
        Map<String, Object> usermap = new HashMap<>();
        usermap.put("memberId", memeberId);
        usermap.put("nickname", nickname);
        //获取ip
        String  ip="127.0.0.1";
        token = JwtUtil.encode("whgmall", usermap, ip);
        //将token存入redis
        userService.addToken(memeberId, token);
        return "redirect:http://localhost:8084/cartList?token="+token;
    }


    @RequestMapping("index")
    public String Index(String ReturnUrl, ModelMap modelMap) {
        modelMap.put("ReturnUrl", ReturnUrl);
        return "index";
    }
}
