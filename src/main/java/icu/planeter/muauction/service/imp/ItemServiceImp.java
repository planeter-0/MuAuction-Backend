package icu.planeter.muauction.service.imp;

import icu.planeter.muauction.common.utils.ElasticsearchUtils;
import icu.planeter.muauction.common.utils.MailUtils;
import icu.planeter.muauction.entity.Bid;
import icu.planeter.muauction.entity.Item;
import icu.planeter.muauction.entity.SellRecord;
import icu.planeter.muauction.entity.User;
import icu.planeter.muauction.repository.BidRepository;
import icu.planeter.muauction.repository.ItemRepository;
import icu.planeter.muauction.repository.SellRecordRepository;
import icu.planeter.muauction.repository.UserRepository;
import icu.planeter.muauction.service.BidService;
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
import java.util.*;

/**
 * @author Planeter
 * @description: ItemService Implement
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
    @Resource
    private BidService bidService;
    @Resource
    private BidRepository bidDao;
    @Resource
    private SellRecordRepository sellRecordDao;
    @Resource
    private UserRepository userDao;

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
        List<Item> unsold = new ArrayList<>(itemDao.findByStatusAndUser(0, user));
        List<Item> sold = new ArrayList<>(itemDao.findByStatusAndUser(1, user));
        resultMap.put("unsold",unsold);
        resultMap.put("sold", sold);
        return resultMap;
    }

    @Override
    public boolean sellOne(Long bidId) {
        Bid bid = bidDao.getOne(bidId);
        Item item =  bid.getItem();
        User seller = (User) SecurityUtils.getSubject().getPrincipals().getPrimaryPrincipal();
        User buyer = bid.getUser();
        // Check whether it is the owner
        if(!seller.getId().equals(item.getUser().getId()))
            return false;
        // Set status to "sold"
        item.setStatus(1);
        itemDao.save(item);
        // Generate sell record
        sellRecordDao.save(new SellRecord(seller.getId(),bid.getUser().getId(),false,bid));
        // Unfreeze other failed bids if they have not been unfreezed before
        List<Bid> others = bidDao.findByItemAndActive(item,true);
        System.out.println(others.remove(bid));
        for(Bid b:others){
            bidService.cancel(b.getId());
        }
        //Email buyer
        SimpleMailMessage s =  MailUtils.generateEmail(sender,
                buyer.getEmail(),"MuAuction",
                "You've got this at auction"+'\n'+item.toString());
        javaMailSender.send(s);
        return true;
    }

    @Override
    public boolean confirmReceipt(Long bidId) {
        User buyer = (User) SecurityUtils.getSubject().getPrincipals().getPrimaryPrincipal();
        Bid bid = bidDao.getOne(bidId);
        SellRecord record = bid.getSellRecord();
        // Received item cannot be reconfirmed
        if(record.isReceived())
            return false;
        // Set status of bid
        bid.setActive(true);
        bidDao.save(bid);
        // Set status of sell record
        record.setReceived(true);
        sellRecordDao.save(record);
        // Reduce buyer's balance
        buyer.setFrozenBalance(buyer.getFrozenBalance()-bid.getPrice());
        buyer.setBalance(buyer.getBalance()-bid.getPrice());
        userDao.save(buyer);
        // Increase seller's balance
        User seller = bid.getItem().getUser();
        seller.setBalance(seller.getBalance()+bid.getPrice());
        userDao.save(seller);
        // Email seller
        Item item = bid.getItem();
        SimpleMailMessage s =  MailUtils.generateEmail(sender,
                seller.getEmail(),"MuAuction",
                "The seller has received the good: "+item.toString());
        javaMailSender.send(s);
        return true;
    }

    @Override
    public List<Map<String, Object>> search(String key, Integer size, Integer from, String sortField, String sortOrder) {
        MultiMatchQueryBuilder matchQuery;
        QueryBuilder totalFilter;
        if (!key.equals("*")) {

            matchQuery = QueryBuilders.multiMatchQuery(key, "name", "detail", "tags").analyzer("ik_max_word");

            TermQueryBuilder termQuery = QueryBuilders.termQuery("status", 0);
            totalFilter = QueryBuilders.boolQuery()
                    .filter(matchQuery)
                    .must(termQuery);
        } else {
            TermQueryBuilder termQuery = QueryBuilders.termQuery("status", 0);
            totalFilter = QueryBuilders.boolQuery()
                    .must(termQuery);
        }
        SortOrder order = null;
        if (sortOrder.equals("ASC")) {
            order = SortOrder.ASC;
        } else if (sortOrder.equals("DESC")) {
            order = SortOrder.DESC;
        }
        return elasticsearchUtils.searchListData("muauction_item",
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
