package icu.planeter.muauction.service;

import icu.planeter.muauction.entity.Item;

import java.util.List;

public interface ItemService {
    /** search Item */
    List<Item> search(String key);
    /** get Item details */
    Item getItem(Long itemId);
    /** upload Item */
    Item auctionItem(Item upload);
    /** delete Item by id*/
    void deleteById(Long id);
    /** get my items */
    List<Item> getMine(String username, Integer type);

}
