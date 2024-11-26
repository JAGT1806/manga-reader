package com.proyecto.mangareader.app.security.config;

import com.proyecto.mangareader.app.security.filter.JwtTokenFilter;
import com.proyecto.mangareader.app.security.filter.PostgeSQLUserContextFilter;
import com.proyecto.mangareader.app.security.service.CustomUserDetailsService;
import com.proyecto.mangareader.app.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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
@RequiredArgsConstructor
public class WebSecurityConfig {
    /**
     * Construcción de MessageUtil
     */
    private final MessageUtil messageUtil;
    /**
     * Proporciona un codificador de contraseñas usando el algoritmo BCrypt.
     *
     * @return Instancia de PasswordEncoder para codificar contraseñas
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura la cadena de filtros de seguridad de Spring con configuraciones de seguridad integrales.
     *
     * Incluye:
     * - Protección CSRF deshabilitada
     * - Mapeos de solicitudes autorizadas
     * - Gestión de sesión sin estado
     * - Manejo personalizado de excepciones
     * - Filtros de autenticación personalizados
     *
     * @param http Instancia de HttpSecurity para configurar opciones de seguridad
     * @param postgeSQLUserContextFilter Filtro personalizado para establecer el contexto de usuario en PostgreSQL
     * @param jwtTokenFilter Filtro de autenticación basado en JWT
     * @return SecurityFilterChain configurado
     * @throws Exception Si ocurre un error durante la configuración de seguridad HTTP
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
                                "/api/manga/**",
                                "/api/img",
                                "/api/reports/**"
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
                            messageUtil.getMessage("auth.error.access.denied"),
                            HttpStatus.UNAUTHORIZED.value()
                    ));
                })
                .accessDeniedHandler((request, response, exception) -> {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType("application/json");
                    response.getWriter().write(String.format(
                            "{\"message\":\"%s\",\"status\":%d}",
                            messageUtil.getMessage("auth.error.access.denied"),
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
     * @param authenticationConfiguration Configuración de autenticación proporcionada por Spring
     * @return AuthenticationManager configurado
     * @throws Exception Si ocurre un error al obtener el gestor de autenticación
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Define el proveedor de autenticación para el sistema.
     *
     * Configura el servicio de detalles de usuario y codificación de contraseñas.
     *
     * @param userDetailsService Servicio para cargar detalles de usuario autenticado
     * @return Proveedor de autenticación configurado
     */
    @Bean
    public AuthenticationProvider authenticationProvider(CustomUserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Define el servicio personalizado de detalles de usuario.
     *
     * @return Instancia de CustomUserDetailsService para gestión de usuarios
     */
    @Bean
    public CustomUserDetailsService customUserDetailsService() {
        return new CustomUserDetailsService();
    }
}