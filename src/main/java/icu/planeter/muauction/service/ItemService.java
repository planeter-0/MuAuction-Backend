package icu.planeter.muauction.service;

import icu.planeter.muauction.entity.Item;
import icu.planeter.muauction.entity.User;

import java.util.List;
import java.util.Map;

public interface ItemService {
    /**
     * Item search engine
     *
     * @param key Search terms
     * @param size page size
     * @param from page index
     * @param sortField sort field
     * @param sortOrder sort order(asc or desc)
     * @return Namely List<Item JSON>
     */
    List<Map<String, Object>> search(String key, Integer size, Integer from, String sortField, String sortOrder);

    /**
     * Get the details of an item
     *
     * @param itemId item id
     * @return Item
     */
    Item getItem(Long itemId);

    /**
     * Upload an auction item and send an email
     * to ask the auction uploader to check the information
     *
     * @param upload Item
     * @return Item
     */
    Item auctionItem(Item upload);

    /**
     * Get all auction items of the current user with classification
     *
     * @return Map<Item type, Item list>
     */
    Map<String, List<Item>> getMine(User user);

    /**
     * Owner chooses a bid to sell
     * @param itemId
     * @return
     */
    boolean sellOne(Long itemId);

    /**
     * Transfer funds, inactivate the bid
     * @return
     */
    boolean confirmReceipt(Long bidId);
}
