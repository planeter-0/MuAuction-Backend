package icu.planeter.muauction.controller;

import icu.planeter.muauction.common.response.Response;

import icu.planeter.muauction.common.response.ResponseCode;
import icu.planeter.muauction.dto.UserDetail;
import icu.planeter.muauction.entity.User;
import icu.planeter.muauction.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author Planeter
 * @description: User Controller
 * @date 2021/6/11 22:06
 * @status OK
 */
@RestController
@Slf4j
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
        log.info(user.getEmail()+" edit details SUCCESS");
        return new Response<>(ResponseCode.SUCCESS);
    }
    @GetMapping("/user/details")
    public Response<User> userDetails(){
        User user = (User) SecurityUtils.getSubject().getPrincipals().getPrimaryPrincipal();
        log.info(user.getEmail()+" Get user details SUCCESS");
        return new Response<>(ResponseCode.SUCCESS,user);
    }
    @GetMapping("/user/{userId}")
    public Response<User> userDetailsById(@PathVariable Long userId){
        User user = userDao.getOne(userId);
        log.info(user.getEmail()+" Get user details SUCCESS");
        return new Response<>(ResponseCode.SUCCESS,user);
    }
    @GetMapping("/getUserByEmail")
    public Response<User> userDetailsByEmail(@RequestParam String email){
        User user = userDao.findByEmail(email);
        log.info(user.getEmail()+" Get user details SUCCESS");
        return new Response<>(ResponseCode.SUCCESS,user);
    }
}
