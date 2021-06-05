package icu.planeter.muauction.service;

import icu.planeter.muauction.dto.UserInfo;
import icu.planeter.muauction.entity.User;

/**
 * @description: UserService interface
 * @author Planeter
 * @date 2021/6/5 22:08
 * @status dev
 */
public interface UserService {

    boolean isValid(String email);

    User register(String username, String password);
}
