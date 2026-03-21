package com.example.lab05;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class Lab05Application {
    public static void main(String[] args) {
        SpringApplication.run(Lab05Application.class, args);
    }
}
