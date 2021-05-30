package icu.planeter.muauction.dto;

import lombok.Data;

import java.io.Serializable;
/**
 * @description: TODO
 * @author Planeter
 * @date 2021/5/27 20:33
 * @status dev
 */
@Data
public class UserInfo implements Serializable {
    String email;
    String password;
}
