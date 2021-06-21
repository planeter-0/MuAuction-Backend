package icu.planeter.muauction.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Planeter
 * @description: Elasticsearch utils
 * @date 2021/5/15 0:06
 * @status dev
 */
@Slf4j
@Component()
public class ElasticsearchUtils {

    @Resource
    private RestHighLevelClient restHighLevelClient;


    public static final String KEYWORD = ".keyword";


    public boolean createIndex(String index) throws IOException {
        if (isIndexExist(index)) {
            log.error("Index is exits!");
            return false;
        }
        //1.Create index request
        CreateIndexRequest request = new CreateIndexRequest(index);
        //2.Execute client request
        CreateIndexResponse response = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);

        log.info("Create index {} SUCCESS", index);

        return response.isAcknowledged();
    }

    public boolean deleteIndex(String index) throws IOException {
        if (!isIndexExist(index)) {
            log.error("Index is not exits!");
            return false;
        }
        DeleteIndexRequest request = new DeleteIndexRequest(index);
        AcknowledgedResponse delete = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);

        log.info("Delete index {} SUCCESS", index);

        return delete.isAcknowledged();
    }



    public boolean isIndexExist(String index) throws IOException {

        GetIndexRequest request = new GetIndexRequest(index);

        return restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
    }


    /**
     * Add data
     *
     * @param jsonObject Data to be added
     * @param index
     * @param id
     * @return
     */
    public String addData(JSONObject jsonObject, String index, String id) throws IOException {

        IndexRequest request = new IndexRequest(index);
        request.id(id);
        request.timeout(TimeValue.timeValueSeconds(1));
        IndexRequest source = request.source(jsonObject, XContentType.JSON);
        //client send request
        IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        log.info("Add data SUCCESS index: {}, response status: {}, id: {}", index, response.status().getStatus(), response.getId());
        return response.getId();
    }


    /**
     * 数据添加 随机id
     *
     * @param jsonObject 要增加的数据
     * @param index      索引，类似数据库
     * @return
     */
    public String addData(JSONObject jsonObject, String index) throws IOException {
        return addData(jsonObject, index, UUID.randomUUID().toString().replaceAll("-", "").toUpperCase());
    }

    /**
     * 通过ID删除数据
     *
     * @param index 索引，类似数据库
     * @param id    数据ID
     */
    public void deleteDataById(String index, String id) throws IOException {
        //删除请求
        DeleteRequest request = new DeleteRequest(index, id);
        //执行客户端请求
        DeleteResponse delete = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
        log.info("索引为: {}, id为: {}删除数据成功", index, id);
    }


    /**
     * 通过ID 更新数据
     *
     * @param object 要增加的数据
     * @param index  索引，类似数据库
     * @param id     数据ID
     * @return
     */
    public void updateDataById(Object object, String index, String id) throws IOException {
        //更新请求
        UpdateRequest update = new UpdateRequest(index, id);

        //保证数据实时更新
        //update.setRefreshPolicy("wait_for");

        update.timeout("1s");
        update.doc(JSON.toJSONString(object), XContentType.JSON);
        //执行更新请求
        UpdateResponse update1 = restHighLevelClient.update(update, RequestOptions.DEFAULT);
        log.info("索引为: {}, id为: {}, 更新数据成功", index, id);
    }


    /**
     * 通过ID 更新数据,保证实时性
     *
     * @param object 要增加的数据
     * @param index  索引，类似数据库
     * @param id     数据ID
     * @return
     */
    public void updateDataByIdNoRealTime(Object object, String index, String id) throws IOException {
        //更新请求
        UpdateRequest update = new UpdateRequest(index, id);

        //保证数据实时更新
        update.setRefreshPolicy("wait_for");

        update.timeout("1s");
        update.doc(JSON.toJSONString(object), XContentType.JSON);
        //执行更新请求
        UpdateResponse update1 = restHighLevelClient.update(update, RequestOptions.DEFAULT);
        log.info("索引为: {}, id为: {}, 更新数据成功", index, id);
    }


    /**
     * 通过ID获取数据
     *
     * @param index  索引，类似数据库
     * @param id     数据ID
     * @param fields 需要显示的字段，逗号分隔（缺省为全部字段）
     * @return
     */
    public Map<String, Object> searchDataById(String index, String id, String fields) throws IOException {
        GetRequest request = new GetRequest(index, id);
        if (StringUtils.isNotEmpty(fields)) {
            //只查询特定字段。如果需要查询所有字段则不设置该项。
            request.fetchSourceContext(new FetchSourceContext(true, fields.split(","), Strings.EMPTY_ARRAY));
        }
        GetResponse response = restHighLevelClient.get(request, RequestOptions.DEFAULT);
        Map<String, Object> map = response.getSource();
        //为返回的数据添加id
        map.put("id", response.getId());
        return map;
    }

    /**
     * 通过ID判断文档是否存在
     *
     * @param index 索引，类似数据库
     * @param id    数据ID
     * @return
     */
    public boolean existsById(String index, String id) throws IOException {
        GetRequest request = new GetRequest(index, id);
        //不获取返回的_source的上下文
        request.fetchSourceContext(new FetchSourceContext(false));
        request.storedFields("_none_");
        return restHighLevelClient.exists(request, RequestOptions.DEFAULT);
    }

    /**
     * 获取低水平客户端
     *
     * @return
     */
    public RestClient getLowLevelClient() {
        return restHighLevelClient.getLowLevelClient();
    }


    /**
     * 高亮结果集 特殊处理
     * map转对象 JSONObject.parseObject(JSONObject.toJSONString(map), Content.class)
     *
     * @param searchResponse
     * @param highlightField
     */
    public List<Map<String, Object>> setSearchResponse(SearchResponse searchResponse, String highlightField) {
        //解析结果
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            Map<String, HighlightField> high = hit.getHighlightFields();
            HighlightField title = high.get(highlightField);

            hit.getSourceAsMap().put("id", hit.getId());

            Map<String, Object> sourceAsMap = hit.getSourceAsMap();//原来的结果
            //解析高亮字段,将原来的字段换为高亮字段
            if (title != null) {
                Text[] texts = title.fragments();
                String nTitle = "";
                for (Text text : texts) {
                    nTitle += text;
                }
                //替换
                sourceAsMap.put(highlightField, nTitle);
            }
            list.add(sourceAsMap);
        }
        return list;
    }


    /**
     * 查询并分页
     *
     * @param index          索引名称
     * @param query          查询条件
     * @param size           文档大小限制
     * @param from           从第几页开始
     * @param fields         需要显示的字段，逗号分隔（缺省为全部字段）
     * @param sortField      排序字段
     * @param highlightField 高亮字段
     * @return
     */
    public List<Map<String, Object>> searchListData(String index,
                                                    SearchSourceBuilder query,
                                                    Integer size,
                                                    Integer from,
                                                    String fields,
                                                    String sortField,
                                                    SortOrder sortOrder,
                                                    String highlightField) {
        SearchRequest request = new SearchRequest(index);
        SearchSourceBuilder builder = query;
        if (StringUtils.isNotEmpty(fields)) {
            //只查询特定字段。如果需要查询所有字段则不设置该项。
            builder.fetchSource(new FetchSourceContext(true, fields.split(","), Strings.EMPTY_ARRAY));
        }
        from = from <= 0 ? 0 : from * size;
        //设置确定结果要从哪个索引开始搜索的from选项，默认为0
        builder.from(from);
        builder.size(size);
        if (StringUtils.isNotEmpty(sortField)) {
            //排序字段，注意如果proposal_no是text类型会默认带有keyword性质，需要拼接.keyword
            if (sortOrder != null) {
                builder.sort(sortField + ".keyword", sortOrder);
            }
        }
        try {
            //高亮
            HighlightBuilder highlight = new HighlightBuilder();
            highlight.field(highlightField);
            //关闭多个高亮
            highlight.requireFieldMatch(false);
            highlight.preTags("<span style='color:red'>");
            highlight.postTags("</span>");
            builder.highlighter(highlight);
            //不返回源数据。只有条数之类的数据。
            //builder.fetchSource(false);
            request.source(builder);


            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);

            log.error("==" + response.getHits().getTotalHits());
            if (response.status().getStatus() == 200) {
                // 解析对象
                return setSearchResponse(response, highlightField);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}