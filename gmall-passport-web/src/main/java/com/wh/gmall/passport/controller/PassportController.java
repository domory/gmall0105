package com.wh.gmall.passport.controller;


import com.wh.gmall.bean.UmsMember;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author DOMORY
 */
@Controller
@CrossOrigin
public class PassportController {

    @RequestMapping("login")
    @ResponseBody
    private String login(UmsMember umsMember){

        return "token";
    }

    @RequestMapping("index")
    private String Index(String ReturnUrl, ModelMap modelMap){
        modelMap.put("ReturnUrl",ReturnUrl);
        return "index";
    }
}
