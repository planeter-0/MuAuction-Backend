package icu.planeter.muauction.controller;

import icu.planeter.muauction.common.response.Response;
import icu.planeter.muauction.common.response.ResponseCode;
import icu.planeter.muauction.dto.UserInfo;
import icu.planeter.muauction.entity.User;
import icu.planeter.muauction.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.bouncycastle.asn1.ocsp.ResponseData;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Planeter
 * @description: TODO
 * @date 2021/6/6 13:15
 * @status dev
 */
@RestController
public class LoginController {
    @Resource
    UserService userService;
    /**
     * login, DbRealm handle, set jwt header
     * @param info
     * @return
     */
    @PostMapping("/login")
    public Response<Object> login(@RequestBody UserInfo info, HttpServletResponse response) {
        String email = info.getUsername();
        String password = info.getPassword();
        Subject subject = SecurityUtils.getSubject();
        String jwt = null;
        User user = null;
        try {
            //UsernamePasswordToken DbShiroRealm
            subject.login(new UsernamePasswordToken(email, password));
            user = (User) subject.getPrincipal();
            // unbanned
            if(user.getStatus()==1) {
                //generate jwt and save it
                jwt = userService.generateJwtToken(email);
                response.setHeader("Json-Web-Token", jwt);
            } else
                return new Response<>(ResponseCode.AccountForbidden);
        } catch (UnknownAccountException e) {
            e.printStackTrace();
            return new Response<>(ResponseCode.EmailWrong);
        } catch (IncorrectCredentialsException e) {
            e.printStackTrace();
            return new Response<>(ResponseCode.PasswordWrong);
        } catch (Exception e) {
            return new Response<>(ResponseCode.FAILED);
        }
        return new Response<>(ResponseCode.SUCCESS, user);
    }
    /**
     * logout
     * @return
     */
    @PutMapping("/logout")
    public Response<Object> logout() {
        Subject subject = SecurityUtils.getSubject();
        if(subject.getPrincipals() != null) {
            User user = (User) subject.getPrincipals().getPrimaryPrincipal();
            userService.deleteJwtUser(user.getEmail());
        }
        SecurityUtils.getSubject().logout();
        return new Response<>(ResponseCode.SUCCESS);
    }
}
