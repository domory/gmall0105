package com.wh.gmall.manage.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.wh.gmall.bean.PmsBaseAttrInfo;
import com.wh.gmall.bean.PmsBaseAttrValue;
import com.wh.gmall.bean.PmsBaseSaleAttr;
import com.wh.gmall.service.AttrService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@CrossOrigin//跨端口交互
public class AttrController {

//@Reference注入的是分布式中的远程服务对象，@Resource和@Autowired注入的是本地spring容器中的对象
    @Reference
    AttrService attrService;


    @RequestMapping("baseSaleAttrList")
    @ResponseBody
    public List<PmsBaseSaleAttr> baseSaleAttrList(){
        List<PmsBaseSaleAttr> PmsBaseSaleAttrList= attrService.baseSaleAttrList();
        return PmsBaseSaleAttrList;
    }

    @RequestMapping("getAttrValueList")
    @ResponseBody
    public List<PmsBaseAttrValue> getAttrValueList(String attrId){
        List<PmsBaseAttrValue> pmsBaseAttrValueList= attrService.getAttrValueList(attrId);
        return pmsBaseAttrValueList;
    }

    @RequestMapping("saveAttrInfo")
    @ResponseBody
    public String saveAttrInfo(@RequestBody PmsBaseAttrInfo pmsBaseAttrInfo){
       String success= attrService.saveAttrInfo(pmsBaseAttrInfo);
        return "";
    }

    @RequestMapping("attrInfoList")
    @ResponseBody
    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id){
        List<PmsBaseAttrInfo> pmsBaseAttrInfoList= attrService.attrInfoList(catalog3Id);
        return pmsBaseAttrInfoList;
    }
}
