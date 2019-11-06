package com.wh.gmall.service;

import com.wh.gmall.bean.PmsBaseAttrInfo;
import com.wh.gmall.bean.PmsBaseAttrValue;
import com.wh.gmall.bean.PmsBaseSaleAttr;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface AttrService  {
    List<PmsBaseAttrInfo> attrInfoList(String catalog3Id);

    String saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo);

    List<PmsBaseAttrValue> getAttrValueList(String attrId);

    List<PmsBaseSaleAttr> baseSaleAttrList();
}
