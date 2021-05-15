package icu.planeter.muauction.repository;

import icu.planeter.muauction.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Planeter
 * @description: TODO
 * @date 2021/5/15 9:27
 * @status dev
 */
public interface ItemRepository extends JpaRepository<Item,Long> {
}
