package icu.planeter.muauction.service.imp;


import icu.planeter.muauction.entity.Role;
import icu.planeter.muauction.entity.User;
import icu.planeter.muauction.repository.RoleRepository;
import icu.planeter.muauction.repository.UserRepository;
import icu.planeter.muauction.service.UserService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    UserRepository userDao;
    @Resource
    RoleRepository roleDao;

    @Override
    public boolean isValid(String email) {
        return userDao.existsByEmail(email);
    }

    @Override
    public User register(String email, String password) {
        User user = new User();
        try {
            //Bcrypt encoded
            String encodedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            List<Role> roles = new ArrayList<>();
            roles.add(roleDao.findByName("user"));//
            user = new User(email, encodedPassword, roles);
            // insert
            userDao.save(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }
}
