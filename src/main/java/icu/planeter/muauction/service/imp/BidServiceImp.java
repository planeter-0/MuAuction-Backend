package icu.planeter.muauction.service.imp;

import icu.planeter.muauction.entity.Bid;
import icu.planeter.muauction.entity.Item;
import icu.planeter.muauction.entity.User;
import icu.planeter.muauction.repository.BidRepository;
import icu.planeter.muauction.repository.ItemRepository;
import icu.planeter.muauction.repository.UserRepository;
import icu.planeter.muauction.service.BidService;
import org.apache.shiro.SecurityUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Planeter
 * @description: BidService Implement
 * @date 2021/6/11 19:22
 * @status dev
 */
public class BidServiceImp implements BidService {
    @Resource
    BidRepository bidDao;
    @Resource
    ItemRepository itemDao;
    @Resource
    UserRepository userDao;

    @Override
    public int bid(double price, Long itemId, String address, String comment) {
        User user = (User) SecurityUtils.getSubject().getPrincipals().getPrimaryPrincipal();
        Item item = itemDao.getOne(itemId);
        if (user.getUnfrozenBalance() < price)
            return 2;//Balance Not Enough
        else if(item.getStatus() == 1){
            return 0;//Item have been sold
        } else{
            bidDao.save(new Bid(user, price, item, address, comment, true));
            return 1;//Success
        }
    }

    @Override
    public List<Bid> getMine() {
        User user = (User) SecurityUtils.getSubject().getPrincipals().getPrimaryPrincipal();
        return bidDao.getByUser(user);
    }

    @Override
    public List<Bid> getByItemId(Long itemId) {
        return bidDao.getByItem(itemDao.getOne(itemId));
    }

    @Override
    public boolean cancel(Long bidId) {
        User user = (User) SecurityUtils.getSubject().getPrincipals().getPrimaryPrincipal();
        Bid bid = bidDao.getOne(bidId);
        // Check whether it is the bid user
        if(!user.getId().equals(bid.getUser().getId()))
            return false;
        // Not activated, Unfreezing some balance
        if (bid.getActive()){
            bidDao.inactivate(bidId);
            user.setFrozenBalance(user.getFrozenBalance()-bid.getPrice());
            userDao.save(user);
        }
        return true;
    }
}
