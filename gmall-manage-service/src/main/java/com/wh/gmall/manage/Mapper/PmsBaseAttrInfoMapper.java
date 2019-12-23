package com.wh.gmall.manage.Mapper;

import com.wh.gmall.bean.PmsBaseAttrInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author DOMORY
 */
public interface PmsBaseAttrInfoMapper extends Mapper<PmsBaseAttrInfo> {
    List<PmsBaseAttrInfo> selectPmsBaseAttrInfoByValueId(@Param("valuedIds") String valuedIds);
}
