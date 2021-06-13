package icu.planeter.muauction.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import icu.planeter.muauction.entity.Role;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author Planeter
 * @description: UserDetail
 * @date 2021/6/11 22:09
 * @status dev
 */
@Data
public class UserDetail {

    private String nickname;

    private String address;

    private String phone;

    // 0->unknown, 1->male, 2->female
    private Short gender = 0;

    private Date birthday;

    private String profile;

    private String icon; //url
}
