package icu.planeter.muauction.service;

import icu.planeter.muauction.entity.User;

import java.util.List;

public interface PermissionService {
    /** Get permission list */
    List<String> getPermissionsByUsername(User user);
}