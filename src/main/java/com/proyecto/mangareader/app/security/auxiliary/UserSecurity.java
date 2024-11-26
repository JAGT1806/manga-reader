package com.proyecto.mangareader.app.security.auxiliary;

import com.proyecto.mangareader.app.exceptions.SelfRoleModificationException;
import com.proyecto.mangareader.app.security.entity.CustomUserDetails;
import com.proyecto.mangareader.app.service.imp.RolesService;
import com.proyecto.mangareader.app.util.MessageUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Clase de seguridad para gestionar permisos y autorizaciones de usuarios.
 *
 * Esta clase proporciona métodos para verificar si un usuario tiene permisos
 * para realizar diferentes acciones como modificar usuarios, editar y eliminar roles.
 *
 * @author Jhon Alexander Gómez Trujillo
 * @since 2024
 */
@Component
@AllArgsConstructor
public class UserSecurity {
    /** Servicio para gestionar roles de usuarios. */
    private final RolesService rolesService;
    /** Utilidad para manejar mensajes internacionalizados. */
    private final MessageUtil messageUtil;

    /**
     * Verifica si el usuario actual tiene permiso para modificar un usuario específico.
     *
     * @param userId Identificador del usuario que se intenta modificar
     * @return true si el usuario tiene permiso para modificar, false en caso contrario
     */
    public boolean isUserAllowed(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getId().equals(userId);
        }
        return false;
    }

    /**
     * Verifica si el usuario actual tiene permiso para editar un rol específico.
     *
     * Solo los administradores pueden editar roles. Un administrador no puede
     * modificar su propio rol.
     *
     * @param roleId Identificador del rol que se intenta editar
     * @return true si el usuario tiene permiso para editar el rol
     * @throws SelfRoleModificationException si se intenta modificar el propio rol
     */
    public boolean isUserAllowedToEditRole(Long roleId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        if (auth.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return false;
        }

        String roleName = rolesService.getById(roleId).getRol();

        if (auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(roleName))) {
            throw new SelfRoleModificationException(messageUtil.getMessage("role.self.modification.forbidden"));
        }
        return true;
    }

    /**
     * Verifica si el usuario actual tiene permiso para eliminar un rol específico.
     *
     * Solo los administradores pueden eliminar roles. Un administrador no puede
     * eliminar su propio rol.
     *
     * @param roleId Identificador del rol que se intenta eliminar
     * @return true si el usuario tiene permiso para eliminar el rol
     * @throws SelfRoleModificationException si se intenta eliminar el propio rol
     */
    public boolean isUserAllowedToDeleteRole(Long roleId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        if (auth.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return false;
        }

        String roleName = rolesService.getById(roleId).getRol();

        if (auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(roleName))) {
            throw new SelfRoleModificationException(messageUtil.getMessage("role.self.delete.forbidden"));
        }

        return true;
    }

}
