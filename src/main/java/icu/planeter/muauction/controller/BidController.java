package icu.planeter.muauction.controller;

import icu.planeter.muauction.common.response.Response;
import icu.planeter.muauction.common.response.ResponseCode;
import icu.planeter.muauction.entity.Bid;
import icu.planeter.muauction.entity.User;
import icu.planeter.muauction.repository.BidRepository;
import icu.planeter.muauction.repository.ItemRepository;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Planeter
 * @description: TODO
 * @date 2021/6/11 16:12
 * @status dev
 */
@RestController
public class BidController {
    @Resource
    private ItemRepository itemDao;
    @Resource
    private BidRepository bidDao;

    @PostMapping("/bid")
    public Response<Object> bid(@RequestParam Long itemId, @RequestParam double price, @RequestParam String address, @RequestParam String comment) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        Bid bid = new Bid(user, price, itemDao.getOne(itemId), address, comment, true);
        bidDao.save(bid);
        return new Response<>(ResponseCode.SUCCESS);
    }
}
