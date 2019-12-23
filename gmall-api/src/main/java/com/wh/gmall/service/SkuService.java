package com.wh.gmall.service;

import com.wh.gmall.bean.PmsSkuInfo;

import java.util.List;

public interface SkuService {
    void saveSkuInfo(PmsSkuInfo pmsSkuInfo);

    PmsSkuInfo getSkuBySkuId(String skuId);

    List<PmsSkuInfo> getSkuAttrValueListBySpuId(String productId);

    List<PmsSkuInfo> getAllSku(String catalog3Id);
}
