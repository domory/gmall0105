package com.wh.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.wh.gmall.bean.PmsSkuInfo;
import com.wh.gmall.service.SkuService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@CrossOrigin
public class SkuController {

    @Reference
    SkuService skuservice;


    @RequestMapping("saveSkuInfo")
    @ResponseBody//spuImageList?spuId=24
    public String saveSkuInfo(@RequestBody PmsSkuInfo pmsSkuInfo){
        //将spuId封装给productId
        pmsSkuInfo.setProductId(pmsSkuInfo.getSpuId());
        //未选择默认图片则自己设置默认图片
        String skuDefaultImg = pmsSkuInfo.getSkuDefaultImg();
        if (StringUtils.isBlank(skuDefaultImg)){
            //把第一个图片设为默认并且将pmsSkuImage表中isDefault字段值改为1
            pmsSkuInfo.setSkuDefaultImg(pmsSkuInfo.getSkuImageList().get(0).getImgUrl());
            pmsSkuInfo.getSkuImageList().get(0).setIsDefault("1");
        }
        skuservice.saveSkuInfo(pmsSkuInfo);
        return "success";
    }
}
