package com.wh.gmall.passport.controller;

import com.alibaba.fastjson.JSON;
import com.wh.gmall.util.HttpclientUtil;

import java.util.HashMap;
import java.util.Map;

public class TestOauth2 {

    public static void main(String[] args) {
        //  http://192.168.1.155:8085/vlogin   2901863716
      //  String s1 = HttpclientUtil.doGet("https://api.weibo.com/oauth2/authorize?client_id=2901863716&response_type=code&redirect_uri=http://192.168.1.155:8085/vlogin");
        //ad244a11e754cffa50bfe1bbe3cc1b48 i
       // System.out.println(s1);
        String s2="http://192.168.1.155:8085/vlogin?code=6070b7bc928e538e36457ef136140f15";
        Map<String, String> map = new HashMap<>();
        String s3="https://api.weibo.com/oauth2/access_token?client_id=2901863716&client_secret=348e94a64b1b7d67a8ff25a81e4e77fa&grant_type=authorization_code&redirect_uri=http://192.168.1.155:8085/vlogin&code=76da42dfd26ebd95d650857445fca84c";
        String accessToken = HttpclientUtil.doPost(s3,map);
        System.out.println(accessToken);
        Map accessTokenmap = JSON.parseObject(accessToken, Map.class);
        System.out.println(accessTokenmap.get("access_token"));

        String s4="https://api.weibo.com/2/users/show.json?access_token="+accessTokenmap.get("access_token")+"&uid="+accessTokenmap.get("uid");
        String userJson = HttpclientUtil.doGet(s4);
        Map userinfo = JSON.parseObject(userJson, Map.class);
        System.out.println(userJson);
    }
}
