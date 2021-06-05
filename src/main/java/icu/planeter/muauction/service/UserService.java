package icu.planeter.muauction.service;

import icu.planeter.muauction.entity.User;

/**
 * @description: UserService interface
 * @author Planeter
 * @date 2021/6/5 22:08
 * @status dev
 */
public interface UserService {

    boolean isEmailExist(String email);

    boolean isEmailValid(String email, String code);

    User register(String username, String password);

    User findByEmail(String email);

    String getJwt(String email);

    String generateJwtToken(String email);
}
