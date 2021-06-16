package icu.planeter.muauction.controller;

import icu.planeter.muauction.common.response.Response;
import icu.planeter.muauction.common.response.ResponseCode;
import icu.planeter.muauction.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Planeter
 * @description: Charge Controller
 * @date 2021/6/11 23:17
 * @status OK
 */
@Slf4j
@RestController
public class ChargeController {
    @Resource
    UserRepository userRepository;

    @PutMapping("/charging")
    public Response<Object> cancel(@RequestParam Long userId, @RequestParam Double quantity) {
        try {
            userRepository.charging(userId, quantity);
        } catch (Exception e){
            e.printStackTrace();
            return new Response<>(ResponseCode.FAILED);
        }
        log.info("Charge SUCCESS");
        return new Response<>(ResponseCode.SUCCESS);
    }
}
