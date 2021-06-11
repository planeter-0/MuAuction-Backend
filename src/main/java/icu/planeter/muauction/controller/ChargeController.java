package icu.planeter.muauction.controller;

import icu.planeter.muauction.common.response.Response;
import icu.planeter.muauction.common.response.ResponseCode;
import icu.planeter.muauction.repository.UserRepository;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Planeter
 * @description: TODO
 * @date 2021/6/11 23:17
 * @status dev
 */
@RestController
public class ChargeController {
    @Resource
    UserRepository userRepository;
    @PutMapping("/charging")
    public Response<Object> cancel(@RequestParam Long userId, @RequestParam Double quantity) {
        userRepository.charging(userId,quantity);
        return new Response<>(ResponseCode.SUCCESS);
    }
}
