package icu.planeter.muauction.controller;

import com.alibaba.fastjson.JSONObject;
import icu.planeter.muauction.common.response.Response;
import icu.planeter.muauction.common.response.ResponseCode;
import icu.planeter.muauction.entity.Bid;
import icu.planeter.muauction.service.BidService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Planeter
 * @description: TODO
 * @date 2021/6/11 16:12
 * @status dev
 */
@Slf4j
@RestController
public class BidController {
    @Resource
    private BidService bidService;

    @PostMapping("/bidding")
    public Response<Object> bid(@RequestBody String jsonStr) {
        Long itemId = JSONObject.parseObject(jsonStr).getLong("itemId");
        String comment = JSONObject.parseObject(jsonStr).getString("comment");
        String address = JSONObject.parseObject(jsonStr).getString("address");
        Double price = JSONObject.parseObject(jsonStr).getDouble("price");
        int flag = 0;
        try {
            flag = bidService.bid(price, itemId, address, comment);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>(ResponseCode.FAILED);
        }
        if (flag == 1) {
            log.info("Bid SUCCESS");
            return new Response<>(ResponseCode.SUCCESS);
        }
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
        log.info("Get my bids SUCCESS");
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
        log.info("Get bid by itemId SUCCESS");
        return new Response<>(ResponseCode.SUCCESS, list);
    }

    @PutMapping("/bid/cancel")
    public Response<Object> cancel(@RequestParam Long bidId) {
        // Try to unfreeze the fund of the bid if it has not been unfreezed before
        if(bidService.cancel(bidId)){
            log.info("Bid cancel SUCCESS");
            return new Response<>(ResponseCode.SUCCESS);
        }
        // Not your bid, you can not cancel
        return new Response<>(ResponseCode.NoSuchPermission);
    }

}
