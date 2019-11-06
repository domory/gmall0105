package com.wh.gmall.manage.ServiceImpl;

import com.wh.gmall.bean.PmsProductInfo;
import com.wh.gmall.bean.PmsProductSaleAttr;
import com.wh.gmall.bean.PmsProductSaleAttrValue;
import com.wh.gmall.manage.Mapper.PmsProductInfoMapper;
import com.wh.gmall.manage.Mapper.PmsProductSaleAttrMapper;
import com.wh.gmall.manage.Mapper.PmsProductSaleAttrValueMapper;
import com.wh.gmall.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    PmsProductInfoMapper pmsProductInfoMapper;
    @Autowired
    PmsProductSaleAttrMapper pmsProductSaleAttrMapper;
    @Autowired
    PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;
    @Override
    public List<PmsProductInfo> spuList(String catalog3Id) {
        Example example=new Example(PmsProductInfo.class);
        example.createCriteria().andEqualTo("catalog3Id",catalog3Id);
        List<PmsProductInfo> pmsProductInfos = pmsProductInfoMapper.selectByExample(example);
        return pmsProductInfos;
    }

    @Override
    public String saveSpuInfo(PmsProductInfo pmsProductInfo) {
        pmsProductInfoMapper.insert(pmsProductInfo);

        List<PmsProductSaleAttr> pmsProductSaleAttrList = pmsProductInfo.getPmsProductSaleAttrList();
        for (PmsProductSaleAttr pmsProductSaleAttr : pmsProductSaleAttrList) {
            List<PmsProductSaleAttrValue> pmsProductSaleAttrValueList = pmsProductSaleAttr.getPmsProductSaleAttrValueList();
            for (PmsProductSaleAttrValue pmsProductSaleAttrValue : pmsProductSaleAttrValueList) {
                pmsProductSaleAttrValue.setSaleAttrId(pmsProductSaleAttr.getSaleAttrId());
                pmsProductSaleAttrValue.setProductId(pmsProductInfo.getId());
                pmsProductSaleAttrValueMapper.insert(pmsProductSaleAttrValue);
            }
            pmsProductSaleAttr.setProductId(pmsProductInfo.getId());
            pmsProductSaleAttrMapper.insert(pmsProductSaleAttr);
        }

        return null;
    }
}
