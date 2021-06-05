package icu.planeter.muauction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MuAuctionApplication {

    public static void main(String[] args) {
        SpringApplication.run(MuAuctionApplication.class, args);
    }

}
