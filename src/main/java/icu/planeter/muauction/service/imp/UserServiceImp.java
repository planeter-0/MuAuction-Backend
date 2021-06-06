package icu.planeter.muauction.service.imp;


import icu.planeter.muauction.common.utils.JwtUtils;
import icu.planeter.muauction.entity.Role;
import icu.planeter.muauction.entity.User;
import icu.planeter.muauction.repository.RoleRepository;
import icu.planeter.muauction.repository.UserRepository;
import icu.planeter.muauction.service.UserService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImp implements UserService {
    @Resource
    UserRepository userDao;
    @Resource
    RoleRepository roleDao;

    @Resource
    RedisTemplate<String,Object> redisTemplate;

    @Override
    public boolean isEmailExist(String email) {
        return userDao.existsByEmail(email);
    }

    @Override
    public boolean isEmailValid(String email, String code) {
        return code.equals(redisTemplate.opsForValue().get("Register-" + email));
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

    @Override
    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public String getJwt(String email) {
        return (String) redisTemplate.opsForValue().get("Jwt-"+email);
    }
    @Override
    public String generateJwtToken(String email) {
        String salt = JwtUtils.generateSalt();
        redisTemplate.opsForValue().set("Jwt-"+email, salt, 3000, TimeUnit.SECONDS);
        return JwtUtils.sign(email, salt, 3600);
    }
}
