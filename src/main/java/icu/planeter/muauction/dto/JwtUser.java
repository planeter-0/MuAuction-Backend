package icu.planeter.muauction.dto;

import lombok.Data;

import java.io.Serializable;
/**
 * @description: JwtUser
 * @author Planeter
 * @date 2021/5/27 20:33
 * @status dev
 */
@Data
public class JwtUser implements Serializable {
    String email;
    String jwt;

    public JwtUser(String email, String token) {
        this.email = email;
        this.jwt = token;
    }
}
