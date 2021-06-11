package icu.planeter.muauction.controller;

import icu.planeter.muauction.common.response.Response;
import icu.planeter.muauction.common.response.ResponseCode;
import icu.planeter.muauction.entity.Item;
import icu.planeter.muauction.repository.ItemRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Planeter
 * @description: TODO
 * @date 2021/6/11 21:51
 * @status dev
 */
@RestController
public class IndexController {
    @Resource
    ItemRepository itemDao;
    @GetMapping("/index/newUploaded")
    public Response<List<Item>> getNewUploaded(){
        return new Response<>(ResponseCode.SUCCESS,itemDao.findFirst10ByStatusOrderByCreateTimeDesc(0));//unsold
    }
}
