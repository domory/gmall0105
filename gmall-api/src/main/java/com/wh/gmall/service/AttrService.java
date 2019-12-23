package com.wh.gmall.service;

import com.wh.gmall.bean.PmsBaseAttrInfo;
import com.wh.gmall.bean.PmsBaseAttrValue;
import com.wh.gmall.bean.PmsBaseSaleAttr;

import java.util.List;
import java.util.Set;

public interface AttrService  {
    List<PmsBaseAttrInfo> attrInfoList(String catalog3Id);

    String saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo);

    List<PmsBaseAttrValue> getAttrValueList(String attrId);

    List<PmsBaseSaleAttr> baseSaleAttrList();

    List<PmsBaseAttrInfo> getPmsBaseAttrInfoByValueId(Set<String> valueIdSet);
}
