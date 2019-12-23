package com.wh.gmall.search.serviceimpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.wh.gmall.bean.PmsSearchParam;
import com.wh.gmall.bean.PmsSearchSkuInfo;
import com.wh.gmall.service.SearchService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author DOMORY
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    JestClient jestClient;

    @Override
    public List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam)  {
        String searchDsl = getSearchDsl(pmsSearchParam);
        System.out.println(searchDsl);
        //用api执行复杂查询
        List<PmsSearchSkuInfo> pmsSearchSkuInfos=new ArrayList<>();
        Search build = new Search.Builder(searchDsl).addIndex("gmall0105").addType("PmsSkuInfo").build();
        SearchResult execute  = null;
        try {
            execute = jestClient.execute(build);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = execute.getHits(PmsSearchSkuInfo.class);
        for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
            PmsSearchSkuInfo source = hit.source;
            //拿出高亮显示的字段  若不是关键字搜索需要判断是否需要高亮显示
            Map<String, List<String>> highlight = hit.highlight;
            if(highlight!=null){
            String skuName = highlight.get("skuName").get(0);
            //source设置高亮字段
            source.setSkuName(skuName);}
            pmsSearchSkuInfos.add(source);
        }
      System.out.println(pmsSearchSkuInfos.size());

        return pmsSearchSkuInfos;
    }

    private String getSearchDsl(PmsSearchParam pmsSearchParam) {
        String[] skuAttrValueList = pmsSearchParam.getValueId();
        String keyword = pmsSearchParam.getKeyword();
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        //jest的dsl工具
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();

        //bool
        BoolQueryBuilder boolQueryBuilder=new BoolQueryBuilder();

        if(StringUtils.isNotBlank(catalog3Id)){
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id", catalog3Id);
            boolQueryBuilder.filter(termQueryBuilder);
        }
        //filter
        if(skuAttrValueList!=null) {
            for (String pmsSkuAttrValue : skuAttrValueList) {
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId", pmsSkuAttrValue);
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }
        //must
        if(StringUtils.isNotBlank(keyword)){
            MatchQueryBuilder matchQueryBuilder=new MatchQueryBuilder("skuName",keyword);
            boolQueryBuilder.must(matchQueryBuilder);
        }
        //query
        searchSourceBuilder.query(boolQueryBuilder);

        //from
        searchSourceBuilder.from(0);
        //size
        searchSourceBuilder.size(200);
        //hightlight
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        //让关键字变红
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.field("skuName");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlight(highlightBuilder);


        //sort 排序  (在这里重新定义了PmsSearchSkuInfo的id属性为long，重新导入了es的数据)
        searchSourceBuilder.sort("id",SortOrder.DESC);
        return  searchSourceBuilder.toString();
    }
}
