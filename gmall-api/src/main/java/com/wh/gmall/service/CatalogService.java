package com.wh.gmall.service;

import com.wh.gmall.bean.PmsBaseCatalog1;
import com.wh.gmall.bean.PmsBaseCatalog2;
import com.wh.gmall.bean.PmsBaseCatalog3;

import java.util.List;

/**
 * @author DOMORY
 */
public interface CatalogService {


    /**
     * 获得目录一的所有信息
     * @return
     */
    List<PmsBaseCatalog1> getCatalog1();
    /**
     * @return
     * 获得目录二的所有信息
     * @param catalog1Id
     * @return List<PmsBaseCatalog2>
     */
    List<PmsBaseCatalog2> getCatalog2(String catalog1Id);
    /**
     * @return
     * 获得目录三的所有信息
     * @param catalog2Id
     * @return List<PmsBaseCatalog3>
     */
    List<PmsBaseCatalog3> getCatalog3(String catalog2Id);
}
