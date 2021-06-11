package icu.planeter.muauction.repository;

import icu.planeter.muauction.entity.Item;
import icu.planeter.muauction.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

/**
 * @author Planeter
 * @description: TODO
 * @date 2021/5/15 9:27
 * @status dev
 */
public interface ItemRepository extends JpaRepository<Item,Long> {
    List<Item> findByStatusAndUser(Integer status, User user);
}
