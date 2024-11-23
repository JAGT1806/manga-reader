package com.proyecto.mangareader.app.security.config;

import com.proyecto.mangareader.app.security.filter.JwtTokenFilter;
import com.proyecto.mangareader.app.security.filter.PostgeSQLUserContextFilter;
import com.proyecto.mangareader.app.security.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * Configuración de seguridad para la aplicación. Define los filtros de autenticación,
 * manejo de excepciones, política de sesiones y configuración de proveedores de autenticación.
 * @author Jhon Alexander Gómez Trujillo
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
    /**
     * Proporciona el codificador de contraseñas para el sistema, usando BCrypt.
     *
     * @return una instancia de PasswordEncoder para codificar contraseñas
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura la cadena de filtros de seguridad de Spring, estableciendo la política de sesiones,
     * los permisos de acceso y los filtros personalizados de autenticación y contexto de usuario.
     *
     * @param http instancia de HttpSecurity para configurar las opciones de seguridad
     * @param postgeSQLUserContextFilter filtro personalizado para establecer el contexto de usuario en PostgreSQL
     * @param jwtTokenFilter filtro para la autenticación basada en JWT
     * @return una instancia de SecurityFilterChain con las configuraciones aplicadas
     * @throws Exception si ocurre un error al configurar la seguridad HTTP
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   PostgeSQLUserContextFilter postgeSQLUserContextFilter,
                                                   JwtTokenFilter jwtTokenFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/v3/api-docs/",
                                "/v3/api-docs.yaml",
                                "/swagger-ui/",
                                "/doc/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api-docsss/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/api/auth/**",
                                "/api/user/forgot-password",
                                "/api/user/reset-password",
                                "/api/manga/**",
                                "/api/img"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                ).exceptionHandling(exc -> exc
                .authenticationEntryPoint((request, response, exception) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json");
                    response.getWriter().write(String.format(
                            "{\"message\":\"%s\",\"status\":%d}",
                            "Acceso denegado: No tienes los permisos necesarios",
                            HttpStatus.UNAUTHORIZED.value()
                    ));
                })
                .accessDeniedHandler((request, response, exception) -> {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType("application/json");
                    response.getWriter().write(String.format(
                            "{\"message\":\"%s\",\"status\":%d}",
                            "Acceso denegado: No tienes los permisos necesarios",
                            HttpStatus.FORBIDDEN.value()
                    ));
                }));

        http.addFilterAfter(postgeSQLUserContextFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configura el gestor de autenticación para el sistema.
     *
     * @param authenticationConfiguration configuración de autenticación proporcionada por Spring
     * @return una instancia de AuthenticationManager para gestionar la autenticación
     * @throws Exception si ocurre un error al obtener el gestor de autenticación
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Define el proveedor de autenticación para el sistema, configurando el servicio de detalles del usuario
     * y el codificador de contraseñas.
     *
     * @param userDetailsService servicio para cargar detalles del usuario autenticado
     * @return un proveedor de autenticación configurado
     */
    @Bean
    public AuthenticationProvider authenticationProvider(CustomUserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Define el servicio personalizado de detalles del usuario.
     *
     * @return una instancia de CustomUserDetailsService para la gestión de usuarios
     */
    @Bean
    public CustomUserDetailsService customUserDetailsService() {
        return new CustomUserDetailsService();
    }
}