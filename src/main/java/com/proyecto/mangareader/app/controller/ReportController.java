package com.proyecto.mangareader.app.controller;

import com.proyecto.mangareader.app.util.MessageUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.sql.DataSource;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para generación de reportes de usuarios. <br>
 *
 * Características principales: <br>
 * - Genera reportes PDF de usuarios con filtros personalizados <br>
 * - Utiliza JasperReports para la generación de informes <br>
 * - Soporte de internacionalización para títulos y descripciones <br>
 * - Conexión a base de datos mediante DataSource <br>
 * <br><br>
 * Dependencias: <br>
 * - MessageUtil: Gestión de mensajes internacionalizados <br>
 * - ResourceLoader: Carga de recursos (plantillas, imágenes)<br>
 * - DataSource: Conexión a la base de datos<br>
 * @author Jhon Alexander Gómez Trujillo
 * @since 2024
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name="Reports", description = "Reportes de usuarios")
public class ReportController {
    /** Utilidad para obtener mensajes internacionalizados. */
    private final MessageUtil messageUtil;

    /** Cargador de recursos para acceder a archivos de plantilla y assets. */
    private final ResourceLoader resourceLoader;

    /** Fuente de datos para conexión a la base de datos. */
    private final DataSource dataSource;

    /**
     * Genera un reporte PDF de usuarios con filtros opcionales.
     *
     * Características del reporte:
     * - Generación dinámica basada en parámetros de filtrado
     * - Exportación directa a PDF
     * - Descarga automática del archivo
     *
     * @param response Respuesta HTTP para la descarga del archivo
     * @param username Filtro opcional por nombre de usuario
     * @param email Filtro opcional por correo electrónico
     * @param enabled Filtro opcional por estado de habilitación
     * @throws Exception Si ocurre un error durante la generación del reporte
     */
    @GetMapping("/users")
    public void generateFilteredReport(
            HttpServletResponse response,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean enabled
    ) throws Exception {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=Reporte_usuarios_filtrado.pdf");

        JasperReport report = JasperCompileManager.compileReport(resourceLoader.getResource("classpath:assets/user_reports.jrxml").getInputStream());

        Resource resource = resourceLoader.getResource("classpath:assets/img.png");
        BufferedImage image = ImageIO.read(resource.getInputStream());
        String title= messageUtil.getMessage("report.user.title");
        String description= messageUtil.getMessage("report.user.text");

        Map<String, Object> params = new HashMap<>();
        params.put("TITLE", title);
        params.put("DESCRIPTION", description);
        params.put("IMG_PATH", image);
        params.put("USERNAME", username );
        params.put("EMAIL", email );
        params.put("ENABLED", enabled);


        try (Connection conn = dataSource.getConnection()) {
            JasperPrint jasperPrint = JasperFillManager.fillReport(report, params, conn);
            try (OutputStream output = response.getOutputStream()) {
                JasperExportManager.exportReportToPdfStream(jasperPrint, output);
                output.flush();
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
