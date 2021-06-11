package icu.planeter.muauction.service.imp;

import icu.planeter.muauction.common.utils.ElasticsearchUtils;
import icu.planeter.muauction.common.utils.MailUtils;
import icu.planeter.muauction.entity.Item;
import icu.planeter.muauction.entity.User;
import icu.planeter.muauction.repository.ItemRepository;
import icu.planeter.muauction.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * @author Planeter
 * @description: TODO
 * @date 2021/6/6 12:52
 * @status dev
 */
@Service
@Slf4j
public class ItemServiceImp implements ItemService {
    @Resource
    private ItemRepository itemDao;
    @Resource
    private JavaMailSender javaMailSender;
    @Resource
    private ElasticsearchUtils elasticsearchUtils;

    @Value("${spring.mail.properties.from}")
    private String sender;

    @Override
    public Item auctionItem(Item upload) {
        // Initialize upload item
        User user = (User) SecurityUtils.getSubject().getPrincipals().getPrimaryPrincipal();
        upload.setUser(user);
        upload.setStatus(0);
        upload.setCreateTime(new Date());

        try {
            // Store in database
            upload = itemDao.save(upload);
            // Send message
            SimpleMailMessage message = MailUtils.generateEmail
                    (sender, user.getEmail(),
                            "MuAuction",
                            "You just uploaded an auction item. Please check the item information:\n" +
                                    upload.toString());
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return upload;
    }

    @Override
    public Map<String, List<Item>> getMine(User user) {
        Map<String, List<Item>> resultMap = new HashMap<>();
        List<Item> unsold = new ArrayList<>();
        List<Item> sold = new ArrayList<>();
        unsold.addAll(itemDao.findByStatusAndUser(0,user));
        sold.addAll(itemDao.findByStatusAndUser(1,user));
        resultMap.put("unsold",unsold);
        resultMap.put("sold", sold);
        return resultMap;
    }

    @Override
    public List<Map<String, Object>> search(String key, Integer size, Integer from, String sortField, String sortOrder) throws IOException {
        MultiMatchQueryBuilder matchQuery = null;
        QueryBuilder totalFilter = null;
        if (!key.equals("*")) {

            matchQuery = QueryBuilders.multiMatchQuery(key, "name", "detail", "tags").analyzer("ik_max_word");

            TermQueryBuilder termQuery = QueryBuilders.termQuery("verified", true);
            totalFilter = QueryBuilders.boolQuery()
                    .filter(matchQuery)
                    .must(termQuery);
        } else {
            TermQueryBuilder termQuery = QueryBuilders.termQuery("verified", true);
            totalFilter = QueryBuilders.boolQuery()
                    .must(termQuery);
        }
        SortOrder order = null;
        if (sortOrder.equals("ASC")) {
            order = SortOrder.ASC;
        } else if (sortOrder.equals("DESC")) {
            order = SortOrder.DESC;
        }
        return elasticsearchUtils.searchListData("item",
                new SearchSourceBuilder().query(totalFilter),
                size,
                from,
                "",
                sortField,
                order,
                "");
    }

    @Override
    public Item getItem(Long itemId) {
        return itemDao.getOne(itemId);
    }
}
