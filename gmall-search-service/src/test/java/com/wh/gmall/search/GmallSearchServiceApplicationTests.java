package com.wh.gmall.search;

import com.alibaba.dubbo.config.annotation.Reference;
import com.wh.gmall.bean.PmsSearchSkuInfo;
import com.wh.gmall.bean.PmsSkuInfo;
import com.wh.gmall.service.SkuService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallSearchServiceApplicationTests {
    @Reference(timeout = 10000)
    SkuService skuService;//查询mysql数据

    @Autowired
    JestClient jestClient;

    @Test
    public void contextLoads() throws IOException {
        //jest的dsl工具
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();

            //bool
            BoolQueryBuilder boolQueryBuilder=new BoolQueryBuilder();
            //filter
             TermQueryBuilder termQueryBuilder=new TermQueryBuilder("skuAttrValueList.valueId","39");
            boolQueryBuilder.filter(termQueryBuilder);
             TermQueryBuilder termQueryBuilder1=new TermQueryBuilder("skuAttrValueList.valueId","43");
            boolQueryBuilder.filter(termQueryBuilder1);
            //must
            MatchQueryBuilder matchQueryBuilder=new MatchQueryBuilder("skuName","华为");
            boolQueryBuilder.must(matchQueryBuilder);
        //query
        searchSourceBuilder.query(boolQueryBuilder);

        //from
        searchSourceBuilder.from(0);
        //size
        searchSourceBuilder.size(20);
        //hightlight
        searchSourceBuilder.highlight(null);

        String s = searchSourceBuilder.toString();
        System.out.println(s);
        //用api执行复杂查询
        List<PmsSearchSkuInfo> pmsSearchSkuInfo=new ArrayList<>();

        Search build = new Search.Builder(s).addIndex("gmall0105").addType("PmsSkuInfo").build();
        SearchResult execute = jestClient.execute(build);
        List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = execute.getHits(PmsSearchSkuInfo.class);
        for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
            PmsSearchSkuInfo source = hit.source;
            pmsSearchSkuInfo.add(source);
        }
        System.out.println(pmsSearchSkuInfo.size());
    }

    @Test
    public void put() throws IOException{
        //查询MySQL数据
        List<PmsSkuInfo> pmsSkuInfoList = new ArrayList<>();
        pmsSkuInfoList = skuService.getAllSku(":287");
        //转换为es的数据结构
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfoList) {
            PmsSearchSkuInfo pmsSearchSkuInfo = new PmsSearchSkuInfo();
            BeanUtils.copyProperties(pmsSkuInfo, pmsSearchSkuInfo);
            //把string类型转为long型
            pmsSearchSkuInfo.setId(Long.parseLong(pmsSkuInfo.getId()));
            pmsSearchSkuInfos.add(pmsSearchSkuInfo);
        }
        //导入es
        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
            Index put = new Index.Builder(pmsSearchSkuInfo).index("gmall0105").type("PmsSkuInfo").id(pmsSearchSkuInfo.getId()+"").build();
            jestClient.execute(put);
        }
    }
}
