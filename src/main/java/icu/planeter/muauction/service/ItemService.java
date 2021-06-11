package icu.planeter.muauction.service;

import icu.planeter.muauction.entity.Item;
import icu.planeter.muauction.entity.User;

import java.util.List;
import java.util.Map;

public interface ItemService {
    /**
     * Item search engine
     * @param text Search terms
     * @return
     */
    List<Map<String, Object>> search(String key, Integer size, Integer from, String sortField, String sortOrder);

    /**
     * Get the details of an item
     * @param itemId
     * @return Item
     */
    Item getItem(Long itemId);

    /**
     * Upload an auction item and send an email
     * to ask the auction uploader to check the information
     * @param upload Item
     * @return Item
     */
    Item auctionItem(Item upload);

    /**
     * Get all auction items of the current user with classification
     * @return Map<Item type, Item list>
     */
    Map<String, List<Item>> getMine(User user);
}
