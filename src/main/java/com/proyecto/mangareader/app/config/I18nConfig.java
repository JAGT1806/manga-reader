package com.proyecto.mangareader.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Arrays;
import java.util.Locale;

@Configuration
public class I18nConfig {
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(new Locale("es"));

        localeResolver.setSupportedLocales(Arrays.asList(
                new Locale("es"),
                new Locale("en"),
                new Locale("fr")
        ));

        return localeResolver;
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("i18n/messages");
        source.setDefaultEncoding("UTF-8");

        source.setDefaultLocale(new Locale("es"));
        source.setUseCodeAsDefaultMessage(true);
        source.setCacheSeconds(3600); // Refrescar chache cada hora

        return source;
    }
}
