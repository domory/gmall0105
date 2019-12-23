package com.wh.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.wh.gmall.bean.PmsBaseCatalog1;
import com.wh.gmall.bean.PmsBaseCatalog2;
import com.wh.gmall.bean.PmsBaseCatalog3;
import com.wh.gmall.service.CatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author DOMORY
 */
@Controller
@CrossOrigin
public class CatalogController {

    @Reference
    CatalogService catalogService;

    @RequestMapping("getCatalog3")
    @ResponseBody //@ResponseBody以json格式传送数据  @requestBody这个标签在post 、put 方法中用于接收json格式的数据
    public List<PmsBaseCatalog3> getCatalog3(String catalog2Id) {
            List<PmsBaseCatalog3> catalog3s = catalogService.getCatalog3(catalog2Id);
            return catalog3s;
    }

    @RequestMapping("getCatalog2")
    @ResponseBody //@ResponseBody以json格式传送数据  @requestBody这个标签在post 、put 方法中用于接收json格式的数据
    public List<PmsBaseCatalog2> getCatalog2( String catalog1Id) {
            List<PmsBaseCatalog2> catalog2s = catalogService.getCatalog2(catalog1Id);
            return catalog2s;
    }

    @RequestMapping("getCatalog1")
    @ResponseBody
    public List<PmsBaseCatalog1> getCatalog1() {
        List<PmsBaseCatalog1> catalog1s = catalogService.getCatalog1();
        return catalog1s;
    }
}
