package icu.planeter.muauction.repository;

import icu.planeter.muauction.entity.Bid;
import icu.planeter.muauction.entity.Item;
import icu.planeter.muauction.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Planeter
 * @description: TODO
 * @date 2021/5/15 9:27
 * @status dev
 */
public interface BidRepository extends JpaRepository<Bid,Long> {
    List<Bid> getByItem(Item item);
    List<Bid> getByUser(User user);
    @Modifying
    @Transactional
    @Query("UPDATE Bid AS b SET b.active = false WHERE b.id = :bidId")
    void inactivate(Long bidId);
    List<Bid> findByItemAndActive(Item item, boolean active);
}
