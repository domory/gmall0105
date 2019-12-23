package com.wh.gmall.service;

import com.wh.gmall.bean.PmsProductImage;
import com.wh.gmall.bean.PmsProductInfo;
import com.wh.gmall.bean.PmsProductSaleAttr;

import java.util.List;

/**
 * @author DOMORY
 */
public interface SpuService {
    List<PmsProductInfo> spuList(String catalog3Id);


    String saveSpuInfo(PmsProductInfo pmsProductInfo);

    List<PmsProductSaleAttr> spuSaleAttrList(String spuId);

    List<PmsProductImage> spuImageList(String spuId);

    List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String productId,String skuId);
}
