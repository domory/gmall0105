package com.wh.gmall.service;

import com.wh.gmall.bean.PmsSearchParam;
import com.wh.gmall.bean.PmsSearchSkuInfo;

import java.util.List;

/**
 * @author DOMORY
 */
public interface SearchService {

    List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam);
}
