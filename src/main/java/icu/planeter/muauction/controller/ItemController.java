package icu.planeter.muauction.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import icu.planeter.muauction.common.response.Response;
import icu.planeter.muauction.common.response.ResponseCode;
import icu.planeter.muauction.entity.Item;
import icu.planeter.muauction.entity.User;
import icu.planeter.muauction.service.ItemService;
import icu.planeter.muauction.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.bouncycastle.asn1.ocsp.ResponseData;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * @author Planeter
 * @description: TODO
 * @date 2021/6/6 13:26
 * @status dev
 */
@RestController
public class ItemController {
    @Resource
    ItemService itemService;
    @Resource
    UserService userService;


    /**
     * Auction an item
     *
     * @param item name, price, detail, images(<String>"url1,url2,..."), tags<String>
     * @return ResponseCode
     */
    @PostMapping("/auctionItem")
    Response<Object> auctionItem(@RequestBody Item item) {
        try {
            itemService.auctionItem(item);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>(ResponseCode.FAILED);
        }
        return new Response<>(ResponseCode.SUCCESS);
    }

    /**
     * Get all auction items of the current user with classification -- sold, unsold
     *
     * @return Map<String, List<Item>>
     */
    @GetMapping("/getMine")
    public Response<Map<String, List<Item>>> getMine() {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        return new Response<>(ResponseCode.SUCCESS, itemService.getMine(user));
    }

    /**
     * Fuzzy search(Use Elasticsearch)
     *
     * @param text
     * @return List<Map < String, Object>> namely Item(Json Str)
     */
    @GetMapping("/search")
    public Response<List<Map<String, Object>>> getAllVerifiedItem(@RequestParam String text,
                                            @RequestParam(defaultValue = "10") Integer size,
                                            @RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "") String sortField,
                                            @RequestParam(defaultValue = "") String sortOrder) {
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
        return new Response<>(ResponseCode.SUCCESS, itemService.getItem(itemId););
    }

}
