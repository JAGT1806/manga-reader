package com.proyecto.mangareader.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Aplica a todas las rutas
                .allowedOrigins("http://localhost:4200") // Aplica para Angular
                .allowedMethods("GET", "POST", "PUT", "DELETE") // métodos HTTP
                .allowedHeaders("*"); // permite cualquier header
    }
}