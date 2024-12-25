package com.jagt1806.mangareader.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition
@SecurityScheme(
        name = "Auth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "Bearer",
        description = "Autenticación JWT para acceso seguro a la API"
)
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Auth");

        return new OpenAPI()
                .addSecurityItem(securityRequirement)
                .info(new Info()
                        .title("Manga Reader API")
                        .description("API para gestión y lectura de mangas con autenticación segura")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Jhon Alexander Gómez Trujillo")
                                .url("https://github.com/JAGT1806")
                        )
                ).addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Servidor de desarrollo local")
                ).addServersItem(new Server().description("Servidor de producción"));
    }
}
