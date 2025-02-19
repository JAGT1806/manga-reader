package com.jagt1806.mangareader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableFeignClients
@SpringBootApplication
public class MangaReaderApplication {

    public static void main(String[] args) {
        SpringApplication.run(MangaReaderApplication.class, args);
    }

}
