package com.xuecheng.search;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class searchTest {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private RestClient restClient;

    @Test
    public void testDeleteIndex() throws IOException {
        //删除索引请求对象
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("xc_course");
        //删除索引客户端
        IndicesClient indices = restHighLevelClient.indices();
        //删除索引响应结果
        DeleteIndexResponse deleteIndexResponse = indices.delete(deleteIndexRequest);
        boolean acknowledged = deleteIndexResponse.isAcknowledged();
        System.out.println(acknowledged);
    }

    @Test
    public void testCreateIndex() throws IOException {
        //删除索引请求对象
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("xc_course");
        //设置索引参数
        createIndexRequest.settings(Settings.builder().put("number_of_shards", 1).put("number_of_replicas", 0));

        //设置映射
        createIndexRequest.mapping("doc", "{\n" +
                "\"properties\": {\n" +
                "\t\"studymodel\":{\n" +
                "\t\t\"type\":\"keyword\"\n" +
                "\t},\n" +
                "\t\"name\":{\n" +
                "\t\t\"type\":\"text\",\n" +
                "\t\t\"analyzer\":\"ik_max_word\",\n" +
                "\t\t\"search_analyzer\":\"ik_smart\"\n" +
                "\t},\n" +
                "\t\"description\": {\n" +
                "\t\t\"type\": \"text\",\n" +
                "\t\t\"analyzer\":\"ik_max_word\",\n" +
                "\t\t\"search_analyzer\":\"ik_smart\"\n" +
                "\t},\n" +
                "\t\"price\":{\n" +
                "\t\t\"type\":\"float\"\n" +
                "\t}\n" +
                "}\n" +
                "}", XContentType.JSON);

        //删除索引客户端
        IndicesClient indices = restHighLevelClient.indices();
        //删除索引响应结果
        CreateIndexResponse createIndexResponse = indices.create(createIndexRequest);
        boolean acknowledged = createIndexResponse.isAcknowledged();
        System.out.println(acknowledged);
    }

    @Test
    public void testAdd() throws IOException {
        //文档数据
        Map<String, Object> map = new HashMap<>();
        map.put("name", "spring cloud实战");
        map.put("description", "本课程主要从四个章节进行讲解： 1.微服务架构入门 2.spring cloud 基础入门 3.实战Spring Boot 4.注册中心eureka。");
        map.put("studymodel", "201001");
        map.put("price", 5.6f);

        //索引对象
        IndexRequest indexRequest = new IndexRequest("xc_course", "doc");

        indexRequest.source(map);

        IndexResponse indexResponse = restHighLevelClient.index(indexRequest);

        DocWriteResponse.Result result = indexResponse.getResult();
        System.out.println(result);
    }

    @Test
    public void testGet() throws IOException {

        GetRequest getRequest = new GetRequest("xc_course", "doc", "YbjeAnUBKjcIj2sZ5Fa2");

        GetResponse getResponse = restHighLevelClient.get(getRequest);
        Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
        System.out.println(sourceAsMap);
    }

    @Test
    public void testUpdate() throws IOException {

        UpdateRequest updateRequest = new UpdateRequest("xc_course", "doc", "YbjeAnUBKjcIj2sZ5Fa2");

        Map<String, String> map = new HashMap<>();
        map.put("name", "spring cloud...");
        updateRequest.doc(map);

        UpdateResponse updateResponse = restHighLevelClient.update(updateRequest);
        RestStatus status = updateResponse.status();
        System.out.println(status);
    }

    @Test
    public void testDelete() throws IOException {

        DeleteRequest deleteRequest = new DeleteRequest("xc_course", "doc", "YbjeAnUBKjcIj2sZ5Fa2");

        DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest);
        DocWriteResponse.Result result = deleteResponse.getResult();
        System.out.println(result);
    }

    @Test
    public void testSearchAll() throws IOException, ParseException {
        //搜索对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        //源对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        //source源字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp"}, new String[]{});

        //设置搜索对象的源对象
        searchRequest.source(searchSourceBuilder);

        //执行搜索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);

        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (SearchHit searchHit : searchHits) {
            String index = searchHit.getIndex();
            String type = searchHit.getType();
            String id = searchHit.getId();
            float score = searchHit.getScore();
            String sourceAsString = searchHit.getSourceAsString();
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            Date date = simpleDateFormat.parse((String) sourceAsMap.get("timestamp"));

            System.out.println(name + " " + studymodel + " " + price + " " + date);
        }
    }

    @Test
    public void testSearchPage() throws IOException, ParseException {
        //搜索对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        //源对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        //source源字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp"}, new String[]{});

        //设置分页参数
        int page = 1;
        int size = 10;
        int from = (page - 1) * size;

        searchSourceBuilder.from(from);
        searchSourceBuilder.size(size);

        //设置搜索对象的源对象
        searchRequest.source(searchSourceBuilder);

        //执行搜索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);

        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (SearchHit searchHit : searchHits) {
            String index = searchHit.getIndex();
            String type = searchHit.getType();
            String id = searchHit.getId();
            float score = searchHit.getScore();
            String sourceAsString = searchHit.getSourceAsString();
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            //Date date = simpleDateFormat.parse((String) sourceAsMap.get("timestamp"));

//            System.out.println(name + " " + studymodel + " " + price + " " + date);
            System.out.println(name + " " + studymodel + " " + price);
        }
    }

    @Test
    public void testTermQuery() throws IOException, ParseException {
        //搜索对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        //源对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("name", "java"));

        //source源字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp"}, new String[]{});

        //设置分页参数
        int page = 1;
        int size = 1;
        int from = (page - 1) * size;

        searchSourceBuilder.from(from);
        searchSourceBuilder.size(size);

        //设置搜索对象的源对象
        searchRequest.source(searchSourceBuilder);

        //执行搜索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);

        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (SearchHit searchHit : searchHits) {
            String index = searchHit.getIndex();
            String type = searchHit.getType();
            String id = searchHit.getId();
            float score = searchHit.getScore();
            String sourceAsString = searchHit.getSourceAsString();
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            Date date = simpleDateFormat.parse((String) sourceAsMap.get("timestamp"));

            System.out.println(name + " " + studymodel + " " + price + " " + date);
        }
    }

    @Test
    public void testTermsQuery() throws IOException, ParseException {
        //搜索对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        //源对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        String[] ids = new String[]{"1", "2"};
        searchSourceBuilder.query(QueryBuilders.termsQuery("_id", ids));

        //source源字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp"}, new String[]{});

        //设置分页参数
        int page = 1;
        int size = 2;
        int from = (page - 1) * size;

        searchSourceBuilder.from(from);
        searchSourceBuilder.size(size);

        //设置搜索对象的源对象
        searchRequest.source(searchSourceBuilder);

        //执行搜索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);

        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (SearchHit searchHit : searchHits) {
            String index = searchHit.getIndex();
            String type = searchHit.getType();
            String id = searchHit.getId();
            float score = searchHit.getScore();
            String sourceAsString = searchHit.getSourceAsString();
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            Date date = simpleDateFormat.parse((String) sourceAsMap.get("timestamp"));

            System.out.println(name + " " + studymodel + " " + price + " " + date);
        }
    }

    @Test
    public void testMatchQuery() throws IOException, ParseException {
        //搜索对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        //源对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(QueryBuilders.matchQuery("name", "spring开发框架").operator(Operator.OR).minimumShouldMatch("80%"));

        //source源字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp"}, new String[]{});

        //设置分页参数
        int page = 1;
        int size = 2;
        int from = (page - 1) * size;

        searchSourceBuilder.from(from);
        searchSourceBuilder.size(size);

        //设置搜索对象的源对象
        searchRequest.source(searchSourceBuilder);

        //执行搜索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);

        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();

        //查询匹配到的总记录数
        System.out.println(hits.getTotalHits());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (SearchHit searchHit : searchHits) {
            String index = searchHit.getIndex();
            String type = searchHit.getType();
            String id = searchHit.getId();
            float score = searchHit.getScore();
            String sourceAsString = searchHit.getSourceAsString();
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            Date date = simpleDateFormat.parse((String) sourceAsMap.get("timestamp"));

            System.out.println(name + " " + studymodel + " " + price + " " + date);
        }
    }

    @Test
    public void testMultiMatchQuery() throws IOException, ParseException {
        //搜索对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        //源对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(QueryBuilders.multiMatchQuery("spring开发", "name", "description").minimumShouldMatch("50%").field("name", 10));

        //source源字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp", "description"}, new String[]{});

        //设置分页参数
        int page = 1;
        int size = 2;
        int from = (page - 1) * size;

        searchSourceBuilder.from(from);
        searchSourceBuilder.size(size);

        //设置搜索对象的源对象
        searchRequest.source(searchSourceBuilder);

        //执行搜索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);

        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();

        //查询匹配到的总记录数
        System.out.println(hits.getTotalHits());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (SearchHit searchHit : searchHits) {
            String index = searchHit.getIndex();
            String type = searchHit.getType();
            String id = searchHit.getId();
            float score = searchHit.getScore();
            String sourceAsString = searchHit.getSourceAsString();
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            Date date = simpleDateFormat.parse((String) sourceAsMap.get("timestamp"));
            String description = (String) sourceAsMap.get("description");
            System.out.println(name + " " + studymodel + " " + price + " " + date + " " + description);
        }
    }

    @Test
    public void testBoolQuery() throws IOException, ParseException {
        //搜索对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        //源对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring开发", "name", "description").minimumShouldMatch("50%").field("name", 10);

        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("studymodel", "201002");

        //布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);
        boolQueryBuilder.must(termQueryBuilder);

        searchSourceBuilder.query(boolQueryBuilder);

        //source源字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp", "description"}, new String[]{});

        //设置分页参数
        int page = 1;
        int size = 2;
        int from = (page - 1) * size;

        searchSourceBuilder.from(from);
        searchSourceBuilder.size(size);

        //设置搜索对象的源对象
        searchRequest.source(searchSourceBuilder);

        //执行搜索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);

        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();

        //查询匹配到的总记录数
        System.out.println(hits.getTotalHits());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (SearchHit searchHit : searchHits) {
            String index = searchHit.getIndex();
            String type = searchHit.getType();
            String id = searchHit.getId();
            float score = searchHit.getScore();
            String sourceAsString = searchHit.getSourceAsString();
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            Date date = simpleDateFormat.parse((String) sourceAsMap.get("timestamp"));
            String description = (String) sourceAsMap.get("description");
            System.out.println(name + " " + studymodel + " " + price + " " + date + " " + description);
        }
    }

    @Test
    public void testFilter() throws IOException, ParseException {
        //搜索对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        //源对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring开发", "name", "description").minimumShouldMatch("50%").field("name", 10);

        //布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);

        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(60).lte(100));

        searchSourceBuilder.query(boolQueryBuilder);

        //source源字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp", "description"}, new String[]{});

        //设置分页参数
        int page = 1;
        int size = 2;
        int from = (page - 1) * size;

        searchSourceBuilder.from(from);
        searchSourceBuilder.size(size);

        //设置搜索对象的源对象
        searchRequest.source(searchSourceBuilder);

        //执行搜索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);

        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();

        //查询匹配到的总记录数
        System.out.println(hits.getTotalHits());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (SearchHit searchHit : searchHits) {
            String index = searchHit.getIndex();
            String type = searchHit.getType();
            String id = searchHit.getId();
            float score = searchHit.getScore();
            String sourceAsString = searchHit.getSourceAsString();
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            Date date = simpleDateFormat.parse((String) sourceAsMap.get("timestamp"));
            String description = (String) sourceAsMap.get("description");
            System.out.println(name + " " + studymodel + " " + price + " " + date + " " + description);
        }
    }

    @Test
    public void testSort() throws IOException, ParseException {
        //搜索对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        //源对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring开发", "name", "description").minimumShouldMatch("50%").field("name", 10);

        //布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);

        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(60).lte(100));

        searchSourceBuilder.query(boolQueryBuilder);

        searchSourceBuilder.sort("studymodel", SortOrder.DESC);
        searchSourceBuilder.sort("price", SortOrder.ASC);

        //source源字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp", "description"}, new String[]{});

        //设置分页参数
        int page = 1;
        int size = 2;
        int from = (page - 1) * size;

        searchSourceBuilder.from(from);
        searchSourceBuilder.size(size);

        //设置搜索对象的源对象
        searchRequest.source(searchSourceBuilder);

        //执行搜索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);

        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();

        //查询匹配到的总记录数
        System.out.println(hits.getTotalHits());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (SearchHit searchHit : searchHits) {
            String index = searchHit.getIndex();
            String type = searchHit.getType();
            String id = searchHit.getId();
            float score = searchHit.getScore();
            String sourceAsString = searchHit.getSourceAsString();
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            Date date = simpleDateFormat.parse((String) sourceAsMap.get("timestamp"));
            String description = (String) sourceAsMap.get("description");
            System.out.println(name + " " + studymodel + " " + price + " " + date + " " + description);
        }
    }

    @Test
    public void testHighlight() throws IOException, ParseException {
        //搜索对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        //源对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring开发", "name", "description").minimumShouldMatch("50%").field("name", 10);

        //布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);

        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(60).lte(100));

        searchSourceBuilder.query(boolQueryBuilder);

        searchSourceBuilder.sort("studymodel", SortOrder.DESC);
        searchSourceBuilder.sort("price", SortOrder.ASC);

        //source源字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp", "description"}, new String[]{});

        //设置高亮字段
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<tag>");
        highlightBuilder.postTags("</tag>");
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        highlightBuilder.fields().add(new HighlightBuilder.Field("description"));

        searchSourceBuilder.highlighter(highlightBuilder);


        //设置分页参数
        int page = 1;
        int size = 2;
        int from = (page - 1) * size;

        searchSourceBuilder.from(from);
        searchSourceBuilder.size(size);

        //设置搜索对象的源对象
        searchRequest.source(searchSourceBuilder);

        //执行搜索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);

        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();


        //查询匹配到的总记录数
        System.out.println(hits.getTotalHits());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (SearchHit searchHit : searchHits) {

            String index = searchHit.getIndex();
            String type = searchHit.getType();
            String id = searchHit.getId();
            float score = searchHit.getScore();
            String sourceAsString = searchHit.getSourceAsString();
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");

            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            if (highlightFields != null) {
                HighlightField highlightField = highlightFields.get("name");
                if (highlightField != null) {
                    Text[] fragments = highlightField.getFragments();
                    StringBuilder stringBuilder = new StringBuilder();
                    for (Text text : fragments) {
                        stringBuilder.append(text);
                    }
                    name = stringBuilder.toString();
                }
            }

            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            Date date = simpleDateFormat.parse((String) sourceAsMap.get("timestamp"));
            String description = (String) sourceAsMap.get("description");
            System.out.println(name + " " + studymodel + " " + price + " " + date + " " + description);
        }
    }

}
