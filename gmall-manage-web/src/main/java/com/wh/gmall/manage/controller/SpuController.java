package com.wh.gmall.manage.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.wh.gmall.bean.PmsProductImage;
import com.wh.gmall.bean.PmsProductInfo;
import com.wh.gmall.bean.PmsProductSaleAttr;
import com.wh.gmall.manage.util.PmsUploadUtil;
import com.wh.gmall.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@CrossOrigin
public class SpuController {

    @Reference
    SpuService spuService;



    @RequestMapping("fileUpload")
    @ResponseBody
    public String fileUpload(@RequestParam("file") MultipartFile multipartFile){
        //将图片上传到分布式存储系统

        //将图片的imgUrl返回给前端

        String imgUrl= PmsUploadUtil.uploadImage(multipartFile);
        System.out.println(imgUrl);
        return imgUrl;
    }

    @RequestMapping("spuImageList")
    @ResponseBody//spuImageList?spuId=24
    public List<PmsProductImage> spuImageList(String spuId){
        List<PmsProductImage> pmsProductImageList= spuService.spuImageList(spuId);
        return pmsProductImageList;
    }

    @RequestMapping("spuSaleAttrList")
    @ResponseBody//spuSaleAttrList?spuId=24
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId){
        List<PmsProductSaleAttr> pmsProductSaleAttrList= spuService.spuSaleAttrList(spuId);
        return pmsProductSaleAttrList;
    }

    @RequestMapping("saveSpuInfo")
    @ResponseBody//catalog3Id=61
    public String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo){
        spuService.saveSpuInfo(pmsProductInfo);
        return "success";
    }


    @RequestMapping("spuList")
    @ResponseBody//catalog3Id=61
    public List<PmsProductInfo> spuList(String catalog3Id){
        List<PmsProductInfo> PmsProductInfoList= spuService.spuList(catalog3Id);
        return PmsProductInfoList;
    }
}
