package com.wh.gmall.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TestJwt {
    public static void main(String[] args) {
        Map<String,Object> map=new HashMap<>();
        map.put("UmsMemberId","1");
        map.put("nickname","嗡嗡嗡");
        String ip ="192.168.1.10";
        String time=new SimpleDateFormat("yyyyMMDD hhmmss").format(new Date());
        String encode = JwtUtil.encode("whgmall0105", map, ip+time);
        System.err.println(encode);
    }
}
