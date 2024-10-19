package com.proyecto.mangareader.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MangaReaderApplication {

	public static void main(String[] args) {
		SpringApplication.run(MangaReaderApplication.class, args);
	}

}
