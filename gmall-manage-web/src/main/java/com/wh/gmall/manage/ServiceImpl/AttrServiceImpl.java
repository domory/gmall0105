package com.wh.gmall.manage.ServiceImpl;

import com.wh.gmall.bean.PmsBaseAttrInfo;
import com.wh.gmall.bean.PmsBaseAttrValue;
import com.wh.gmall.bean.PmsBaseSaleAttr;
import com.wh.gmall.manage.Mapper.PmsBaseAttrInfoMapper;
import com.wh.gmall.manage.Mapper.PmsBaseAttrValueMapper;
import com.wh.gmall.manage.Mapper.PmsBaseSaleAttrListMapper;
import com.wh.gmall.service.AttrService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class AttrServiceImpl implements AttrService {
    @Autowired
    PmsBaseAttrInfoMapper pmsBaseAttrInfoMapper;
    @Autowired
    PmsBaseAttrValueMapper pmsBaseAttrValueMapper;
    @Autowired
    PmsBaseSaleAttrListMapper pmsBaseSaleAttrListMapper;

    @Override
    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id) {
        Example example=new Example(PmsBaseAttrInfo.class);
        example.createCriteria().andEqualTo("catalog3Id",catalog3Id);
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = pmsBaseAttrInfoMapper.selectByExample(example);
        return pmsBaseAttrInfos;
    }

    @Override
    public String saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo) {
        String id = pmsBaseAttrInfo.getId();
        //id为空则是插入 否则是更新操作
        if(StringUtils.isBlank(id)){
            pmsBaseAttrInfoMapper.insert(pmsBaseAttrInfo);
            //根据pmsBaseAttrInfo拿到PmsBaseAttrValue属性值
            List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
            for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
                pmsBaseAttrValue.setAttrId(pmsBaseAttrInfo.getId());
                pmsBaseAttrValueMapper.insertSelective(pmsBaseAttrValue);
            }
            return "success";}
        else {
            //更新属性pmsBaseAttrInfo
            Example example=new Example(PmsBaseAttrInfo.class);
            example.createCriteria().andEqualTo("id",id);
            pmsBaseAttrInfoMapper.updateByExample(pmsBaseAttrInfo,example);
            //更新属性值pmsBaseAttrValue
            //先删除之前的 再插入
            Example example1=new Example(PmsBaseAttrValue.class);
            example1.createCriteria().andEqualTo("attrId",id);
            pmsBaseAttrValueMapper.deleteByExample(example1);

            List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
            for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
                pmsBaseAttrValue.setAttrId(id);
                pmsBaseAttrValueMapper.insertSelective(pmsBaseAttrValue);
            }
            return "success";
        }
    }

    @Override
    public List<PmsBaseAttrValue> getAttrValueList(String attrId) {
        PmsBaseAttrValue pmsBaseAttrValue=new PmsBaseAttrValue();
        pmsBaseAttrValue.setAttrId(attrId);
        List<PmsBaseAttrValue> pmsBaseAttrValues = pmsBaseAttrValueMapper.select(pmsBaseAttrValue);
        return pmsBaseAttrValues;
    }

    @Override
    public List<PmsBaseSaleAttr> baseSaleAttrList() {
        List<PmsBaseSaleAttr> pmsBaseSaleAttrs = pmsBaseSaleAttrListMapper.selectAll();
        return pmsBaseSaleAttrs;
    }
}
