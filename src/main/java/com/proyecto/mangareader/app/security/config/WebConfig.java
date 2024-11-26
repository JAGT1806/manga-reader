package com.proyecto.mangareader.app.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración de CORS (Cross-Origin Resource Sharing) para la aplicación web.
 *
 * Esta clase configura las políticas de intercambio de recursos entre orígenes,
 * permitiendo solicitudes desde cualquier origen con métodos HTTP estándar.
 *
 * @author Jhon Alexander Gómez Trujillo
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    /**
     * Configura los mappings de CORS para todas las rutas de la aplicación.
     *
     * Características de la configuración:
     * - Permite orígenes desde cualquier dominio
     * - Soporta métodos HTTP: GET, POST, PUT, DELETE, OPTIONS
     * - Permite todas las cabeceras
     * - Expone todas las cabeceras de respuesta
     * - Habilita el envío de credenciales
     * - Establece un tiempo máximo de cacheo de preflight de 3600 segundos
     *
     * @param registry Registro de configuración CORS
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://*", "https://*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
