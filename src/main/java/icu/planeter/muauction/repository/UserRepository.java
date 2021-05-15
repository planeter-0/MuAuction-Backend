package icu.planeter.muauction.repository;

import icu.planeter.muauction.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Planeter
 * @description: TODO
 * @date 2021/5/15 9:28
 * @status dev
 */
public interface UserRepository extends JpaRepository<User,Long> {
}
