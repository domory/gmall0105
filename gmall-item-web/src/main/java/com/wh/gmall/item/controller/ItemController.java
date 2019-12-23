package com.wh.gmall.item.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.wh.gmall.bean.PmsProductSaleAttr;
import com.wh.gmall.bean.PmsSkuInfo;
import com.wh.gmall.bean.PmsSkuSaleAttrValue;
import com.wh.gmall.service.SkuService;
import com.wh.gmall.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ItemController {

    @Reference
    SkuService skuservice;

    @Reference
    SpuService spuservice;

    @RequestMapping("{skuId}.html")
    public String  index(@PathVariable String skuId, ModelMap modelMap){

        //sku对象封装了image
        PmsSkuInfo pmsSkuInfo=skuservice.getSkuBySkuId(skuId);
        modelMap.put("skuInfo",pmsSkuInfo);
        //销售属性
      List<PmsProductSaleAttr> PmsProductSaleAttr= spuservice.spuSaleAttrListCheckBySku(pmsSkuInfo.getProductId(),pmsSkuInfo.getId());
      modelMap.put("spuSaleAttrListCheckBySku",PmsProductSaleAttr);

      //获得当前sku所属的spu对应的其他sku的hash集合
       List<PmsSkuInfo> pmsSkuInfoList= skuservice.getSkuAttrValueListBySpuId(pmsSkuInfo.getProductId());
        Map<String, String> skuSaleAttrValueHash =new HashMap<>();
        for (PmsSkuInfo skuInfo : pmsSkuInfoList) {
            String k="";
            String v = skuInfo.getId();
            List<PmsSkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
            for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
                k += pmsSkuSaleAttrValue.getSaleAttrValueId()+"|";
            }
            skuSaleAttrValueHash.put(k,v);
        }
        String skuSaleAttrHashJsonStr = JSON.toJSONString(skuSaleAttrValueHash);
        modelMap.put("skuSaleAttrHashJsonStr",skuSaleAttrHashJsonStr);
        return "item";
    }



    @RequestMapping("index")
    public String  index(ModelMap modelMap){
        List<String > list=new ArrayList<>();
        for (int i = 0; i <6 ; i++) {
            list.add("循环"+i);
        }
        modelMap.put("list",list);
        modelMap.put("s","123123");
        modelMap.put("check","1");
        return "index";
    }
}
