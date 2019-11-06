package com.wh.gmall.manage.ServiceImpl;

import com.wh.gmall.bean.PmsBaseCatalog1;
import com.wh.gmall.bean.PmsBaseCatalog2;
import com.wh.gmall.bean.PmsBaseCatalog3;
import com.wh.gmall.manage.Mapper.PmsBaseCatalog1Mapper;
import com.wh.gmall.manage.Mapper.PmsBaseCatalog2Mapper;
import com.wh.gmall.manage.Mapper.PmsBaseCatalog3Mapper;
import com.wh.gmall.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class CatalogServiceImpl implements CatalogService {

    @Autowired
    PmsBaseCatalog1Mapper pmsBaseCatalog1Mapper;
    @Autowired
    PmsBaseCatalog2Mapper pmsBaseCatalog2Mapper;
    @Autowired
    PmsBaseCatalog3Mapper pmsBaseCatalog3Mapper;

    @Override
    public List<PmsBaseCatalog1> getCatalog1() {
        List<PmsBaseCatalog1> pmsBaseCatalog1s = pmsBaseCatalog1Mapper.selectAll();
        return pmsBaseCatalog1s;
    }

    @Override
    public List<PmsBaseCatalog2> getCatalog2(String catalog1Id) {
        Example example=new Example(PmsBaseCatalog2.class);
        example.createCriteria().andEqualTo("catalog1Id",catalog1Id);
        List<PmsBaseCatalog2> pmsBaseCatalog2s=pmsBaseCatalog2Mapper.selectByExample(example);

        return pmsBaseCatalog2s;
    }

    @Override
    public List<PmsBaseCatalog3> getCatalog3(String catalog2Id) {
        Example example=new Example(PmsBaseCatalog3.class);
        example.createCriteria().andEqualTo("catalog2Id",catalog2Id);
        List<PmsBaseCatalog3> pmsBaseCatalog3s = pmsBaseCatalog3Mapper.selectByExample(example);
        return pmsBaseCatalog3s;
    }
}
