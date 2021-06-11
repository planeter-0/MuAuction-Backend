package icu.planeter.muauction.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;

    private String nickname;

    private String address;

    private String phone;

    private String profile;

    // 0->unknown, 1->male, 2->female
    private Short gender = 0;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthday;

    private String icon; //url

    @JsonIgnoreProperties("roles")
    @ManyToMany
    @JoinTable(name = "user_role",
            joinColumns = {@JoinColumn(name = "user_id",referencedColumnName="id")},
            inverseJoinColumns={@JoinColumn(name = "role_id",referencedColumnName="id")})
    private List<Role> roles;

    private double balance;

    private double frozenBalance;

    // 0-> disabled, 1-> available
    private int status = 1;

    @OneToMany
    private List<Item> items;

    public User(String email, String password, List<Role> roles) {
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public double getUnfrozenBalance(){
        return (balance - frozenBalance);
    }

}
