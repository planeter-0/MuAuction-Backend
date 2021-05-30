package icu.planeter.muauction.service;

import icu.planeter.muauction.dto.UserInfo;
import icu.planeter.muauction.entity.User;

public interface UserService {
    User register(UserInfo info);
}
