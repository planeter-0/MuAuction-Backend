package icu.planeter.muauction.service.imp;


import icu.planeter.muauction.entity.Item;
import icu.planeter.muauction.entity.Permission;
import icu.planeter.muauction.entity.Role;
import icu.planeter.muauction.entity.User;
import icu.planeter.muauction.repository.UserRepository;
import icu.planeter.muauction.service.PermissionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author Planeter
 * @date 2021/6/5 23:17
 * @status dev
 */
@Service
public class PermissionServiceImp implements PermissionService {
    private static final List<String> dynamicItemPermissions = new ArrayList<>();
    private static final List<String> dynamicOrderPermissions = new ArrayList<>();

    static {
        dynamicItemPermissions.add("item:delete:");
        dynamicItemPermissions.add("item:update:");
        dynamicOrderPermissions.add("order:view:");
        dynamicOrderPermissions.add("order:update:");
    }
    //TODO Cache
    @Override
    public List<String> getPermissionsByUsername(User user) {
        List<String> permissionStrList = new ArrayList<>();
        //Role Permissions
        for (Role r : user.getRoles()) {
            for (Permission p : r.getPermissions())
                permissionStrList.add(p.getName());
        }
        //Instance Permissions
        permissionStrList.addAll(getDynamicPermissions(user));
        return permissionStrList;
    }

    public static List<String> getDynamicPermissions(User user) {
        List<String> permissionStrList = new ArrayList<>();
        List<Item> items = user.getItems();
        for (String s : dynamicItemPermissions) {
            for (Item i : items) {
                permissionStrList.add(s + i.getId());
            }
        }
        return permissionStrList;
    }
}
