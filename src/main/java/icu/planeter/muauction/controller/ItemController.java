package icu.planeter.muauction.controller;


import icu.planeter.muauction.common.response.Response;
import icu.planeter.muauction.common.response.ResponseCode;
import icu.planeter.muauction.entity.Item;
import icu.planeter.muauction.entity.User;
import icu.planeter.muauction.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author Planeter
 * @description: TODO
 * @date 2021/6/6 13:26
 * @status dev
 */
@RestController
@Slf4j
public class ItemController {
    @Resource
    ItemService itemService;
    @Resource
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.properties.from}")
    private String sender;


    /**
     * Auction an item
     *
     * @param item name, price, detail, images(<String>"url1,url2,..."), tags<String>
     * @return ResponseCode
     */
    @PostMapping("/item/auction")
    Response<Object> auctionItem(@RequestBody Item item) {
        try {
            itemService.auctionItem(item);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>(ResponseCode.FAILED);
        }
        log.info("Auction item SUCCESS");
        return new Response<>(ResponseCode.SUCCESS);
    }

    /**
     * Get all auction items of the current user with classification -- sold, unsold
     *
     * @return Map<String, List < Item>>
     */
    @GetMapping("/item/getMine")
    public Response<Map<String, List<Item>>> getMine() {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        log.info("Get my items SUCCESS");
        return new Response<>(ResponseCode.SUCCESS, itemService.getMine(user));
    }

    /**
     * Fuzzy search(Use Elasticsearch)
     *
     * @param text Search items
     * @return List<Map < String, Object>> namely Item(Json Str)
     */
    @GetMapping("/item/search")
    public Response<List<Map<String, Object>>> getAllVerifiedItem(@RequestParam String text,
                                                                  @RequestParam(defaultValue = "10") Integer size,
                                                                  @RequestParam(defaultValue = "0") Integer from,
                                                                  @RequestParam(defaultValue = "") String sortField,
                                                                  @RequestParam(defaultValue = "") String sortOrder) {
        log.info("Search SUCCESS");
        return new Response<>(ResponseCode.SUCCESS, itemService.search(text, size, from, sortField, sortOrder));
    }


    /**
     * Access an item by ID
     *
     * @param itemId item id
     * @return Item
     */
    @GetMapping("/item/{itemId}")
    Response<Item> getItem(@PathVariable Long itemId) {
        Item item = itemService.getItem(itemId);
        log.info("Get Item by id SUCCESS");
        return new Response<>(ResponseCode.SUCCESS, item);
    }

    @PutMapping("/item/sell")
    Response<Item> sell(@RequestParam Long bidId) {
        if (itemService.sellOne(bidId)) {
            log.info("Sell item SUCCESS");
            return new Response<>(ResponseCode.SUCCESS);
        } else return new Response<>(ResponseCode.NoSuchPermission);//Not the owner
    }

    @PutMapping("/item/confirmReceipt")
    public Response<Object> confirmReceipt(@RequestParam Long bidId) {
        // Try to transfer the fund of the bid if receipt not confirmed before
        if (itemService.confirmReceipt(bidId)) {
            log.info("Confirm Receipt SUCCESS");
            return new Response<>(ResponseCode.SUCCESS);
        }
        // Cannot confirmed receipt again
        log.warn("DuplicateConfirm");
        return new Response<>(ResponseCode.DuplicateConfirm);
    }
}
