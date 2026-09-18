package com.starshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Điểm khởi động StarShop.
 * Chạy dev: mvn spring-boot:run   |  Đóng gói: mvn clean package -> target/starshop.war
 */
@SpringBootApplication
public class StarShopApplication {
    public static void main(String[] args) {
        SpringApplication.run(StarShopApplication.class, args);
    }
}
