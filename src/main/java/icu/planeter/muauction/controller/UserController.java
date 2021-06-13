package icu.planeter.muauction.controller;

import icu.planeter.muauction.common.response.Response;

import icu.planeter.muauction.common.response.ResponseCode;
import icu.planeter.muauction.dto.UserDetail;
import icu.planeter.muauction.entity.User;
import icu.planeter.muauction.repository.UserRepository;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Planeter
 * @description: User Controller
 * @date 2021/6/11 22:06
 * @status OK
 */
@RestController
public class UserController {
    @Resource
    UserRepository userDao;
    @PutMapping("/editDetails")
    public Response<Object> editDetails(@RequestBody UserDetail detail){
        User user = (User) SecurityUtils.getSubject().getPrincipals().getPrimaryPrincipal();
        user.setAddress(detail.getAddress());
        user.setPhone(detail.getPhone());
        user.setGender(detail.getGender());
        user.setBirthday(detail.getBirthday());
        user.setProfile(detail.getProfile());
        user.setIcon(detail.getIcon());
        userDao.save(user);
        return new Response<>(ResponseCode.SUCCESS);
    }
}
