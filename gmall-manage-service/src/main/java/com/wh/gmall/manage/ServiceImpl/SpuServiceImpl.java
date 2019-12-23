package com.wh.gmall.manage.ServiceImpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.wh.gmall.bean.PmsProductImage;
import com.wh.gmall.bean.PmsProductInfo;
import com.wh.gmall.bean.PmsProductSaleAttr;
import com.wh.gmall.bean.PmsProductSaleAttrValue;
import com.wh.gmall.manage.Mapper.PmsProductImageMapper;
import com.wh.gmall.manage.Mapper.PmsProductInfoMapper;
import com.wh.gmall.manage.Mapper.PmsProductSaleAttrMapper;
import com.wh.gmall.manage.Mapper.PmsProductSaleAttrValueMapper;
import com.wh.gmall.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author DOMORY
 */
@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    PmsProductInfoMapper pmsProductInfoMapper;
    @Autowired
    PmsProductSaleAttrMapper pmsProductSaleAttrMapper;
    @Autowired
    PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;
    @Autowired
    PmsProductImageMapper pmsProductImageMapper;


    @Override
    public List<PmsProductInfo> spuList(String catalog3Id) {
        Example example = new Example(PmsProductInfo.class);
        example.createCriteria().andEqualTo("catalog3Id", catalog3Id);
        List<PmsProductInfo> pmsProductInfos = pmsProductInfoMapper.selectByExample(example);
        return pmsProductInfos;
    }

    @Override
    public String saveSpuInfo(PmsProductInfo pmsProductInfo) {
        pmsProductInfoMapper.insert(pmsProductInfo);

        List<PmsProductImage> spuImageList = pmsProductInfo.getSpuImageList();
        for (PmsProductImage pmsProductImage : spuImageList) {
            pmsProductImage.setProductId(pmsProductInfo.getId());
            pmsProductImageMapper.insert(pmsProductImage);
        }

        List<PmsProductSaleAttr> pmsProductSaleAttrList = pmsProductInfo.getSpuSaleAttrList();
        for (PmsProductSaleAttr pmsProductSaleAttr : pmsProductSaleAttrList) {
            List<PmsProductSaleAttrValue> pmsProductSaleAttrValueList = pmsProductSaleAttr.getSpuSaleAttrValueList();
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

    @Override
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId) {
        Example example = new Example(PmsProductSaleAttr.class);
        example.createCriteria().andEqualTo("productId", spuId);
        List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductSaleAttrMapper.selectByExample(example);
        for (PmsProductSaleAttr pmsProductSaleAttr : pmsProductSaleAttrs) {

            Example example1 = new Example(PmsProductSaleAttrValue.class);//"productId",spuId
            example1.createCriteria().andEqualTo("saleAttrId", pmsProductSaleAttr.getSaleAttrId()).andEqualTo("productId", spuId);
            List<PmsProductSaleAttrValue> pmsProductSaleAttrValues = pmsProductSaleAttrValueMapper.selectByExample(example1);
            pmsProductSaleAttr.setSpuSaleAttrValueList(pmsProductSaleAttrValues);
        }


        return pmsProductSaleAttrs;
    }

    @Override
    public List<PmsProductImage> spuImageList(String spuId) {
        Example example = new Example(PmsProductImage.class);
        example.createCriteria().andEqualTo("productId", spuId);
        List<PmsProductImage> pmsProductImages = pmsProductImageMapper.selectByExample(example);
        return pmsProductImages;
    }

    @Override
    public List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String productId, String skuId) {
//        Example example=new Example(PmsProductSaleAttr.class);
//        example.createCriteria().andEqualTo("productId",productId);
//        List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductSaleAttrMapper.selectByExample(example);
//        for (PmsProductSaleAttr pmsProductSaleAttr : pmsProductSaleAttrs) {
//            Example example1=new Example(PmsProductSaleAttrValue.class);
//            example1.createCriteria().andEqualTo("productId",productId).andEqualTo("saleAttrId",pmsProductSaleAttr.getSaleAttrId());
//            List<PmsProductSaleAttrValue> pmsProductSaleAttrValues = pmsProductSaleAttrValueMapper.selectByExample(example1);
//            pmsProductSaleAttr.setSpuSaleAttrValueList(pmsProductSaleAttrValues);
//        }
        List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductSaleAttrMapper.selectSpuSaleAttrListCheckBySku(productId, skuId);

        return pmsProductSaleAttrs;
    }

}
