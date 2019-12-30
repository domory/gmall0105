package com.wh.gmall.search.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.wh.gmall.annotations.LoginRequired;
import com.wh.gmall.bean.*;
import com.wh.gmall.service.AttrService;
import com.wh.gmall.service.SearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author DOMORY
 */
@CrossOrigin
@Controller
public class SearchController {

    @Reference
    SearchService searchService;

    @Reference
    AttrService attrService;
    @RequestMapping("list.html")
    public String list(PmsSearchParam pmsSearchParam, ModelMap modelMap) {//三级分类id和关键字
        //调用搜索服务返回搜索结果 可以根据calogo3Id 和 搜索进行商品查询
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = searchService.list(pmsSearchParam);
        modelMap.put("skuLsInfoList",pmsSearchSkuInfos);
        //取出所有商品属性的信息供筛选 set集合不会有重复值
        //防止有的catalog3Id没有数据
        if(pmsSearchSkuInfos.size()!=0) {
            Set<String> valueIdSet = new HashSet<>();
            for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
                List<PmsSkuAttrValue> skuAttrValueList = pmsSearchSkuInfo.getSkuAttrValueList();
                for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
                    //获取循环的valueId
                    String s1 = pmsSkuAttrValue.getValueId();
                    //获取当前已经点击的属性值的id
                    String[] valueId = pmsSearchParam.getValueId();
                    String s = StringUtils.join(valueId, ",");
                    //判断是否已经点击属性列表的属性值
                    if (s != null) {
                        //如果不包含当前循环的valueId,就添加到set中
                        //对平台属性进行进一步处理，去掉点击的valued所在的属性
                        if (!s.contains(s1)) {
                            valueIdSet.add(s1);
                        }
                    } else {
                        valueIdSet.add(s1);
                    }
                }
            }
            //拿到符合条件的sku的所有属性和属性值 如果最后查到只有一个商品那这时属性列表可能没有值
            if (valueIdSet.size() != 0) {
                List<PmsBaseAttrInfo>  pmsBaseAttrInfos = attrService.getPmsBaseAttrInfoByValueId(valueIdSet);
                modelMap.put("attrList", pmsBaseAttrInfos);
            }
        }

        //根据pmsSearchParam获得当前url
         String UrlParam=getUrlParam(pmsSearchParam);
        modelMap.put("urlParam",UrlParam);
        String keyword = pmsSearchParam.getKeyword();
        if(StringUtils.isNotBlank(keyword)){
            modelMap.put("keyword",keyword);
    }
        //面包屑
        String[] valueId = pmsSearchParam.getValueId();
        List<PmsSearchCrumb> list=new ArrayList<>();
        Set<String> valueIds=new HashSet<>();
        if(valueId!=null){
        for (String s1 : valueId) {
            valueIds.add(s1);
        }
        //重新获取剩下的sku的属性列表
       List<PmsBaseAttrInfo> pmsBaseAttrInfoByValueId = attrService.getPmsBaseAttrInfoByValueId(valueIds);
        for (PmsBaseAttrInfo pmsBaseAttrInfo : pmsBaseAttrInfoByValueId) {
            List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
            PmsSearchCrumb pmsSearchCrumb=new PmsSearchCrumb();
            String attrName = pmsBaseAttrInfo.getAttrName();
            for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
                String valueName = pmsBaseAttrValue.getValueName();
                //面包屑显示为字符拼接的形式 颜色:红色
                valueName=attrName+":"+valueName;
                //获得当前面包屑的ValueId
                String id = pmsBaseAttrValue.getId();
                id="&valueId="+id;
                //去掉当前面包屑的valuedId
                String Url = UrlParam.replace(id, "");
                pmsSearchCrumb.setValueName(valueName);
                pmsSearchCrumb.setUrlParam(Url);


            }
            list.add(pmsSearchCrumb);
        }
        modelMap.put("attrValueSelectedList",list);}
        return "list";
    }







    //获得url 用户可能通过不同的方式访问 keyword catalog3Id skuAttrValueList 所以写一个统一的geturl方法
    private String getUrlParam(PmsSearchParam pmsSearchParam) {
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String keyword = pmsSearchParam.getKeyword();
        String[] skuAttrValueList = pmsSearchParam.getValueId();
        String UrlParam="";
        if(StringUtils.isNotBlank(catalog3Id)){
            if(StringUtils.isNotBlank(UrlParam)){
                UrlParam =UrlParam+"&";
            }
            UrlParam =UrlParam+"catalog3Id="+catalog3Id;
        }
        if(StringUtils.isNotBlank(keyword)){
            if(StringUtils.isNotBlank(UrlParam)){
                UrlParam =UrlParam+"&";
            }
            UrlParam =UrlParam+"keyword="+keyword;
        }
        if(skuAttrValueList!=null){
            for (String valueId : skuAttrValueList) {
                UrlParam =UrlParam+"&valueId="+valueId;
            }
        }

        return  UrlParam;
    }

    @RequestMapping("index")
    @LoginRequired(loginsuccess = false)
    public String index(){

        return "index";
    }

}
