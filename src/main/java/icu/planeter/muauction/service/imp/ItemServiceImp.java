package icu.planeter.muauction.service.imp;

import icu.planeter.muauction.entity.Item;
import icu.planeter.muauction.entity.User;
import icu.planeter.muauction.repository.ItemRepository;
import icu.planeter.muauction.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Planeter
 * @description: TODO
 * @date 2021/6/6 12:52
 * @status dev
 */
@Service
@Slf4j
public class ItemServiceImp implements ItemService {
    @Resource
    ItemRepository itemDao;

    @Override
    public List<Item> search(String key) {
        //return itemDao.findAllByVerified(true);
        return null;
    }

    @Override
    public Item getItem(Long itemId) {
        return itemDao.getOne(itemId);
    }

    @Override
    public Item auctionItem(Item upload) {
        return itemDao.save(upload);
    }

    @Override
    public void deleteById(Long id) {
        itemDao.deleteById(id);
    }

    @Override
    public List<Item> getMine(User user, Integer type) {
        List<Item> fronts = new ArrayList<>();
        // 全部
        if (type == 2){
            for (Item i : itemDao.findBy(username)) {
                fronts.add(DtoUtils.toItemFront(i));
            }
            return fronts;
        }
        boolean sold = false;
        //未售出
        if (type == 0) {
            sold = false;
        } else if (type == 1) {//已售出
            sold = true;
        }
        for (Item i : itemDao.findByUsernameAndAndVerified(username, sold)) {
            fronts.add(DtoUtils.toItemFront(i));
        }
        return fronts;
    }
}
