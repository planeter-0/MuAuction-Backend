package icu.planeter.muauction.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Planeter
 * @description: TODO
 * @date 2021/5/15 2:47
 * @status dev
 */
@Data
@NoArgsConstructor
@Entity
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Double price;

    private String detail;

    private String images; // urls

    private String tags;

    private Integer status = 0; // 0->unsold, 1->sold

    @ManyToOne
    @JoinColumn(name="user_id", referencedColumnName = "id")
    private User user;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    public Item(String name, Double price, String detail, String images, String tags, User user, Date createTime) {
        this.name = name;
        this.price = price;
        this.detail = detail;
        this.images = images;
        this.tags = tags;
        this.user = user;
        this.createTime = createTime;
    }
}
