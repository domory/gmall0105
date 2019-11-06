package com.wh.gmall.service;

import com.wh.gmall.bean.PmsProductInfo;

import java.util.List;

public interface SpuService {
    List<PmsProductInfo> spuList(String catalog3Id);


    String saveSpuInfo(PmsProductInfo pmsProductInfo);
}
