package com.proyecto.mangareader.app.security.auxiliary;

import com.proyecto.mangareader.app.exceptions.SelfRoleModificationException;
import com.proyecto.mangareader.app.security.entity.CustomUserDetails;
import com.proyecto.mangareader.app.service.imp.RolesService;
import com.proyecto.mangareader.app.util.MessageUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserSecurity {
    private final RolesService rolesService;
    private final MessageUtil messageSource;

    public boolean isUserAllowedToModify(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }

        // Para usuarios normales, verificar si es el mismo usuario
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getId().equals(userId);
        }

        return false;
    }

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
            throw new SelfRoleModificationException(messageSource.getMessage("role.self.modification.forbidden"));
        }

        return true;
    }

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
            throw new SelfRoleModificationException(messageSource.getMessage("role.self.delete.forbidden"));
        }

        return true;
    }

}
