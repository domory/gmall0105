package com.wh.gmall.manage.Mapper;

import com.wh.gmall.bean.PmsSkuInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author DOMORY
 */
public interface PmsSkuInfoMapper extends Mapper<PmsSkuInfo> {
    List<PmsSkuInfo> selectSkuAttrValueListBySpuId(@Param("productId") String productId);


}
