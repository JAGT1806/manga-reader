package com.jagt1806.mangareader.security.auxiliary;

import com.jagt1806.mangareader.exceptions.SelfRoleModificationException;
import com.jagt1806.mangareader.security.model.CustomUserDetails;
import com.jagt1806.mangareader.service.RoleService;
import com.jagt1806.mangareader.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserSecurity {
    private final RoleService roleService;
    private final MessageUtil messageUtil;

    @Value("${app.admin.role}")
    private String ROLE_ADMIN;

    public boolean isUserAllowed(Long userId) {
        Authentication auth = getAuthentication();
        if(auth == null || !(auth.getPrincipal() instanceof CustomUserDetails userDetails))
            return false;
        return userDetails.getId().equals(userId);
    }

    public boolean isUserAllowedToEditRole(Long roleId) {
        return isUserAllowedToModifyRole(roleId, "modification");
    }

    public boolean isUserAllowedToDeleteRole(Long roleId) {
        return isUserAllowedToModifyRole(roleId, "delete");
    }

    private Authentication getAuthentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.isAuthenticated()) ? auth : null;
    }

    private boolean isUserAllowedToModifyRole(Long roleId, String action) {
        Authentication auth = getAuthentication();
        if(auth == null) return false;

        Set<String> userRoles = getUserRoles(auth);
        if(!userRoles.contains(ROLE_ADMIN)) return false;

        String roleName = roleService.getById(roleId).getRol();
        if(userRoles.contains(roleName))
            throw new SelfRoleModificationException(messageUtil.getMessage("role.self." + action + ".forbidden"));

        return true;
    }

    private Set<String> getUserRoles(Authentication auth) {
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }
}
