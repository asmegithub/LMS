package com.EGM.LMS.security;

import com.EGM.LMS.model.User;
import com.EGM.LMS.repository.RolePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class RbacAuthorityServiceImpl implements RbacAuthorityService {
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    public Collection<? extends GrantedAuthority> buildAuthorities(User user) {
        var authorities = new LinkedHashSet<GrantedAuthority>();
        if (user == null || user.getRole() == null || user.getRole().isBlank()) {
            return authorities;
        }

        var roleName = normalizeRoleName(user.getRole());
        if (!roleName.isBlank()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));
            rolePermissionRepository.findByRole_NameIgnoreCase(roleName).stream()
                    .map(mapping -> mapping.getPermission() != null ? mapping.getPermission().getName() : null)
                    .filter(name -> name != null && !name.isBlank())
                    .map(String::trim)
                    .map(SimpleGrantedAuthority::new)
                    .forEach(authorities::add);
        }

        return authorities;
    }

    private String normalizeRoleName(String role) {
        var normalized = role.trim().toUpperCase(Locale.ROOT);
        return normalized.startsWith("ROLE_") ? normalized.substring(5) : normalized;
    }
}