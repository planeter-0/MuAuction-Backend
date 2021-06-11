package icu.planeter.muauction.repository;

import icu.planeter.muauction.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Planeter
 * @description: TODO
 * @date 2021/5/15 9:28
 * @status dev
 */
public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);

    boolean existsByEmail(String email);

    void deleteByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE User AS u SET u.balance = u.balance+:quantity WHERE u.id = :userId")
    void charging(Long userId, Double quantity);
}
