package icu.planeter.muauction.controller;

import icu.planeter.muauction.common.response.Response;
import icu.planeter.muauction.common.response.ResponseCode;
import icu.planeter.muauction.entity.Bid;
import icu.planeter.muauction.entity.User;
import icu.planeter.muauction.repository.BidRepository;
import icu.planeter.muauction.repository.ItemRepository;
import icu.planeter.muauction.service.BidService;
import icu.planeter.muauction.service.ItemService;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Planeter
 * @description: TODO
 * @date 2021/6/11 16:12
 * @status dev
 */
@RestController
public class BidController {
    @Resource
    private BidService bidService;
    @Resource
    private ItemService itemService;

    @PostMapping("/bidding")
    public Response<Object> bid(@RequestParam Long itemId, @RequestParam double price, @RequestParam String address, @RequestParam String comment) {
        int flag = 0;
        try {
            flag = bidService.bid(price, itemId, address, comment);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>(ResponseCode.FAILED);
        }
        if (flag == 1)
            return new Response<>(ResponseCode.SUCCESS);
        else if (flag == 2)
            return new Response<>(ResponseCode.BalanceNotEnough);
        else if(flag == 3)
            return new Response<>(ResponseCode.BelowReservePrice);
        else
            return new Response<>(ResponseCode.Sold);
    }

    @GetMapping("/bid/getMine")
    public Response<List<Bid>> getMine() {
        List<Bid> list;
        try {
            list = bidService.getMine();
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>(ResponseCode.FAILED);
        }
        return new Response<>(ResponseCode.SUCCESS, list);
    }

    @GetMapping("/bid/getByItemId")
    public Response<List<Bid>> getByItemId(@RequestParam Long itemId) {
        List<Bid> list;
        try {
            list = bidService.getByItemId(itemId);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>(ResponseCode.FAILED);
        }
        return new Response<>(ResponseCode.SUCCESS, list);
    }

    @PutMapping("/bid/cancel")
    public Response<Object> cancel(@RequestParam Long bidId) {
        // Try to unfreeze the fund of the bid if it has not been unfreezed before
        if(bidService.cancel(bidId)){
            return new Response<>(ResponseCode.SUCCESS);
        }
        // Not your bid, you can not cancel
        return new Response<>(ResponseCode.NoSuchPermission);
    }

}
