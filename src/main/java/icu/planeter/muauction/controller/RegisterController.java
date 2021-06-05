package icu.planeter.muauction.controller;

import icu.planeter.muauction.common.response.ResponseCode;
import icu.planeter.muauction.common.utils.MailUtils;
import icu.planeter.muauction.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import icu.planeter.muauction.common.response.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author Planeter
 * @description: TODO
 * @date 2021/5/27 20:40
 * @status dev
 */
@RestController
@Slf4j
@RequestMapping("/tourist")
public class RegisterController {

    @Resource
    private JavaMailSender javaMailSender;
    @Resource
    RedisTemplate<String,Object> redisTemplate;
    @Resource
    UserService userService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${spring.mail.properties.from}")
    private String sender;

    @GetMapping("/sendEmail")
    public Response<Object> sendEmail(@RequestParam String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        String code = MailUtils.generateCode(6);    //Random generating 6-bit verification code
        message.setFrom(sender);
        message.setTo(email);
        message.setSubject("MuAuction Register");
        message.setText("Hi, Your verification code is: "+code+", which is valid for five minutes (please ignore this email if not yours)");// 内容
        try {
            javaMailSender.send(message);
            logger.info("Send success!");
            redisTemplate.opsForValue().set("Register-"+email,code, 5, TimeUnit.MINUTES);
            return new Response<>(ResponseCode.SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            return new Response<>(ResponseCode.FAILED);
        }
    }

    @PostMapping("/register")
    public Response<Object> register(@RequestParam String email, @RequestParam String password, @RequestParam String verifyCode){
        // Check whether the mailbox is in use
        if (userService.isEmailExist(email)) {
            return new Response<>(ResponseCode.EmailUsed);
        }
        if (!userService.isEmailValid(email,verifyCode)){
            return new Response<>(ResponseCode.VerifyCodeWrong);
        }
        // Save user in database, password hashed by Bcrypt algorithm
        userService.register(email,password);
        return new Response<>(ResponseCode.SUCCESS);
    }
}
