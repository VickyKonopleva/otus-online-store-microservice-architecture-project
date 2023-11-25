package com.konopleva.crudeapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class OrdersAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrdersAppApplication.class, args);
    }

}
