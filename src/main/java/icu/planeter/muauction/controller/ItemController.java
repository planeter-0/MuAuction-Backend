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
     * Fuzzy search(use Elasticsearch)
     *
     * @param text
     * @return List<Map < String, Object>>
     */
    @GetMapping("/searchItem")
    Response<List<Item>> getAllVerifiedItem(@RequestParam String text,
                                            @RequestParam(defaultValue = "10") Integer size,
                                            @RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "") String sortField,
                                            @RequestParam(defaultValue = "") String sortOrder) throws IOException {
        //TODO elasticsearch 使用key进行关键字搜索
        return new Response<>(ResponseCode.SUCCESS, itemService.search(text, size, from, sortField, sortOrder));
    }


    /**
     * 物品的详细信息
     *
     * @param itemId 物品id
     * @return List<Object> 含Item和User
     */
    @GetMapping("/item/{itemId}")
    ResponseData getItem(@PathVariable Long itemId) {
        //TODO mybatis select 组装 itemDetail dto 减少数据库连接次数
        List<Object> data = new ArrayList<>();
        ItemFront i = DtoUtils.toItemFront(itemService.getItem(itemId));
        UserFront u = DtoUtils.toUserFront(userService.findByUsername(i.getUsername()));
        JSONObject itemJson = JSON.parseObject(JSONObject.toJSONString(i));
        JSONObject userJson = JSON.parseObject((JSONObject.toJSONString(u)));
        Map<String, JSONObject> result = new HashMap<>();
        result.put("item", itemJson);
        result.put("user", userJson);
        return new ResponseData(ExceptionMsg.SUCCESS, result);
    }

    /**
     * auction item
     *
     * @param item
     * @return
     */
    @PostMapping("/auctionItem")
    Response<Object> auctionItem(@RequestBody Item item) {
        Subject subject = SecurityUtils.getSubject();
        if (subject.getPrincipals() != null) {
            User user = (User) subject.getPrincipals().getPrimaryPrincipal();
            itemService.auctionItem(item);
            return new Response<>(ResponseCode.SUCCESS);
        }
        return new Response<>(ResponseCode.FAILED);
    }

    /**
     * 获取自己上传的物品
     * @param type 0->未售出, 1->已售出, 2->全部
     * @return List<ItemFront>
     */
    @GetMapping("/getMyItems")
    public ResponseData getMine(@RequestParam Integer type) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        return new ResponseData(ExceptionMsg.SUCCESS, itemService.getMine(user.getUsername(), type));
    }
}
