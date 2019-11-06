package com.wh.gmall.user.controller;


import com.wh.gmall.bean.UmsMember;
import com.wh.gmall.bean.UmsMemberReceiveAddress;
import com.wh.gmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    UserService userService;


    @RequestMapping("getMemberAddressById")
    @ResponseBody
    public List<UmsMemberReceiveAddress> getMemberAddressById(Integer Id){

        List<UmsMemberReceiveAddress> umsMemberReceiveAddressList= userService.getMemberAddressById(Id);

        return umsMemberReceiveAddressList;
    }

    @RequestMapping("getAllUser")
    @ResponseBody
    public List<UmsMember> getAllUSer(){

      List<UmsMember> umsMemberList= userService.getAllUSer();

        return umsMemberList;
    }

    @RequestMapping("index")
    @ResponseBody
    public String d(){
        return "fuck huabang";
    }
}
