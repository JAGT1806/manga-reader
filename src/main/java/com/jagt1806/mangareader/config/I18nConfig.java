package com.jagt1806.mangareader.config;


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
        localeResolver.setDefaultLocale(Locale.of("es"));

        localeResolver.setSupportedLocales(Arrays.asList(
                Locale.of("es"),
                Locale.of("en"),
                Locale.of("fr")
        ));

        return localeResolver;
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("i18n/messages");
        source.setDefaultEncoding("UTF-8");

        source.setDefaultLocale(Locale.of("es"));
        source.setUseCodeAsDefaultMessage(true);
        source.setCacheSeconds(3600);

        return source;
    }
}
