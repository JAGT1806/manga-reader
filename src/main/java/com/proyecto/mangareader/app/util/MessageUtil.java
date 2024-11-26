package com.proyecto.mangareader.app.util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * Utilidad de internacionalización para manejar mensajes localizados en la aplicación Manga Reader.
 *
 * Proporciona métodos de conveniencia para recuperar mensajes configurados en archivos de recursos,
 * utilizando el contexto de locale actual del sistema.
 *
 * Esta clase simplifica la obtención de mensajes localizados, permitiendo la internacionalización
 * de textos de manera sencilla en toda la aplicación.
 *
 * @author Jhon Alexander Gómez Trujillo
 * @since 2024
 */
@Component
@RequiredArgsConstructor
public class MessageUtil {
    /**
     * Fuente de mensajes para recuperación de textos localizados.
     *
     * Inyectado por Spring Framework para manejar la carga de recursos de mensajes.
     */
    private final MessageSource messageSource;

    /**
     * Recupera un mensaje localizado utilizando su código.
     *
     * Obtiene el mensaje correspondiente al código proporcionado usando el locale actual del contexto.
     * Útil para mensajes simples sin parámetros de reemplazo.
     *
     * @param code Código del mensaje a recuperar definido en los archivos de recursos
     * @return Mensaje localizado correspondiente al código
     * @throws org.springframework.context.NoSuchMessageException Si no se encuentra el código de mensaje
     */
    public String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }

    /**
     * Recupera un mensaje localizado utilizando su código y argumentos de reemplazo.
     *
     * Permite obtener mensajes con parámetros dinámicos, reemplazando marcadores
     * en la cadena de mensaje con los argumentos proporcionados.
     *
     * Ejemplo:
     * - Archivo de recursos: "user.welcome=Bienvenido, {0}!"
     * - Uso: getMessage("user.welcome", new Object[]{"Juan"})
     *   Resultado: "Bienvenido, Juan!"
     *
     * @param code Código del mensaje a recuperar definido en los archivos de recursos
     * @param args Argumentos para reemplazar marcadores en el mensaje
     * @return Mensaje localizado con argumentos procesados
     * @throws org.springframework.context.NoSuchMessageException Si no se encuentra el código de mensaje
     */
    public String getMessage(String code, Object[] args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
