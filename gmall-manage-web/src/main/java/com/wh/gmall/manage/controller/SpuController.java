package com.wh.gmall.manage.controller;


import com.wh.gmall.bean.PmsProductInfo;
import com.wh.gmall.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@CrossOrigin
public class SpuController {

    @Autowired
    SpuService spuService;



    @RequestMapping("fileUpload")
    @ResponseBody
    public String fileUpload(@RequestParam("file") MultipartFile multipartFile){
        //将图片上传到分布式存储系统

        //将图片的imgUrl返回给前端

        String imgUrl= "https://m.360buyimg.com/babel/jfs/t5137/20/1794970752/352145/d56e4e94/591417dcN4fe5ef33.jpg";
        return imgUrl;
    }

    @RequestMapping("saveSpuInfo")
    @ResponseBody//catalog3Id=61
    public String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo){
       // String c= spuService.saveSpuInfo(pmsProductInfo);
        return "success";
    }


    @RequestMapping("spuList")
    @ResponseBody//catalog3Id=61
    public List<PmsProductInfo> spuList(String catalog3Id){
        List<PmsProductInfo> PmsProductInfoList= spuService.spuList(catalog3Id);
        return PmsProductInfoList;
    }
}
