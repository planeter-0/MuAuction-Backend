package icu.planeter.muauction.repository;

import icu.planeter.muauction.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Planeter
 * @description: TODO
 * @date 2021/5/15 9:28
 * @status dev
 */
public interface RoleRepository extends JpaRepository<Role,Long> {
    Role findByName(String user);
}
