package com.wh.gmall.user.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.wh.gmall.bean.UmsMember;
import com.wh.gmall.bean.UmsMemberReceiveAddress;
import com.wh.gmall.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author DOMORY
 */
@Controller
public class UserController {

    @Reference
    UserService userService;


    @RequestMapping("getMemberAddressById")
    @ResponseBody
    public List<UmsMemberReceiveAddress> getMemberAddressByMemberId(String MemberId){

        List<UmsMemberReceiveAddress> umsMemberReceiveAddressList= userService.getMemberAddressByMemberId(MemberId);

        return umsMemberReceiveAddressList;
    }

    @RequestMapping("getAllUser")
    @ResponseBody
    public List<UmsMember> getAllUSer(){

      List<UmsMember> umsMemberList= userService.getAllUSer();

        return umsMemberList;
    }


}
