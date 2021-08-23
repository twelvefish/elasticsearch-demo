package com.twelvefish.elasticsearchdemo.service;

import com.google.gson.Gson;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.twelvefish.elasticsearchdemo.model.Book;
import com.twelvefish.elasticsearchdemo.utils.HtmlParseUtils;
import jdk.nashorn.internal.runtime.JSONFunctions;
import org.apache.lucene.util.QueryBuilder;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.directory.SearchResult;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author twelvefish
 * @create 2021-08-22 下午 06:36
 */
@Service
public class BookService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    // 解析數據放入ES中
    public Boolean ParseContent(String keyvalue) throws Exception {
        List<Book> bookList = new HtmlParseUtils().parseJD(keyvalue);

        // 將查詢到的資料放入ES
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("2m");

        for (int i = 0; i < bookList.size() ; i++) {
            Book book = bookList.get(i);
            Gson gson = new Gson();
            String data = gson.toJson(book);
            bulkRequest.add(new IndexRequest("bookstore").source(data,XContentType.JSON));
        }

        // 批量儲存
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        //無失敗，返回成功
        return !bulk.hasFailures();

    }


    public List<Map<String,Object>> searchPage(String keywords, int pageNo, int pageSize) throws Exception {

        if(pageNo <= 1 ){
            pageNo = 1;
        }

        //條件搜索
        SearchRequest searchRequest = new SearchRequest("bookstore");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //分頁
        sourceBuilder.from(pageNo);
        sourceBuilder.size(pageSize);

        //精準匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("bookName", keywords);
        sourceBuilder.query(termQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        //執行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        //解析結果
        List<Map<String, Object>> list = new ArrayList<>();
        for (SearchHit documentFields : searchResponse.getHits().getHits()) {
            list.add(documentFields.getSourceAsMap());
        }
        return list;
    }

    public List<Map<String,Object>> searchHighPage(String keywords, int pageNo, int pageSize) throws Exception {

        if(pageNo <= 1 ){
            pageNo = 1;
        }

        //條件搜索
        SearchRequest searchRequest = new SearchRequest("bookstore");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //分頁
        sourceBuilder.from(pageNo);
        sourceBuilder.size(pageSize);

        //精準匹配
        MatchQueryBuilder termQueryBuilder = QueryBuilders.matchQuery("bookName", keywords);
        sourceBuilder.query(termQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("bookName");
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);

        //執行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        //解析結果
        List<Map<String, Object>> list = new ArrayList<>();
        for (SearchHit documentFields : searchResponse.getHits().getHits()) {

            //解析高亮
            Map<String, HighlightField> highlightFields = documentFields.getHighlightFields();
            HighlightField bookName = highlightFields.get("bookName");
            Map<String, Object> sourceAsMap = documentFields.getSourceAsMap(); //原本解析結果

            //解析高亮字串
            if(bookName != null){
                Text[] fragments = bookName.fragments();

                String new_booName = "";
                for (Text text : fragments) {
                    new_booName += text;
                }
                sourceAsMap.put("bookName", new_booName);
            }
            list.add(sourceAsMap);
        }
        return list;
    }
}


