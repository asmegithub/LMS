package com.EGM.LMS.config;

import com.EGM.LMS.model.Permission;
import com.EGM.LMS.model.Role;
import com.EGM.LMS.model.RolePermission;
import com.EGM.LMS.repository.PermissionRepository;
import com.EGM.LMS.repository.RolePermissionRepository;
import com.EGM.LMS.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Order(1000)
public class RbacBootstrap implements ApplicationRunner {
    private static final String ADMIN = "ADMIN";
    private static final String INSTRUCTOR = "INSTRUCTOR";
    private static final String STUDENT = "STUDENT";

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    public void run(ApplicationArguments args) {
        var roles = ensureRoles();
        var permissions = ensurePermissions();
        ensureMappings(roles, permissions);
    }

    private Map<String, Role> ensureRoles() {
        var roles = new LinkedHashMap<String, Role>();
        roles.put(ADMIN, upsertRole(ADMIN, "System Administrator", "Full platform access.", true));
        roles.put(INSTRUCTOR, upsertRole(INSTRUCTOR, "Instructor", "Manages courses and learners.", true));
        roles.put(STUDENT, upsertRole(STUDENT, "Student", "Learns, purchases, and participates.", true));
        return roles;
    }

    private Map<String, Permission> ensurePermissions() {
        var permissions = new LinkedHashMap<String, Permission>();
        permissions.put("rbac.manage", upsertPermission("rbac.manage", "Manage RBAC", "SECURITY", "Create and update roles and permissions."));
        permissions.put("roles.manage", upsertPermission("roles.manage", "Manage Roles", "SECURITY", "Create, update, and delete roles."));
        permissions.put("permissions.manage", upsertPermission("permissions.manage", "Manage Permissions", "SECURITY", "Create, update, and delete permissions."));
        permissions.put("role-permissions.manage", upsertPermission("role-permissions.manage", "Manage Role Permissions", "SECURITY", "Assign and revoke permissions on roles."));
        permissions.put("users.manage", upsertPermission("users.manage", "Manage Users", "USERS", "Create, update, and delete platform users."));
        permissions.put("courses.approve", upsertPermission("courses.approve", "Approve Courses", "COURSES", "Review and approve course submissions."));
        permissions.put("instructor-profiles.manage", upsertPermission("instructor-profiles.manage", "Manage Instructor Profiles", "USERS", "Review instructor applications and profiles."));
        permissions.put("payment-accounts.manage", upsertPermission("payment-accounts.manage", "Manage Payment Accounts", "FINANCE", "Create and manage payout and payment accounts."));
        permissions.put("payment-transactions.manage", upsertPermission("payment-transactions.manage", "Manage Payment Transactions", "FINANCE", "Create and manage payment transactions."));
        permissions.put("payments.manage", upsertPermission("payments.manage", "Manage Payments", "FINANCE", "Create and manage payment records."));
        permissions.put("payment-proofs.manage", upsertPermission("payment-proofs.manage", "Manage Payment Proofs", "FINANCE", "Review and approve payment proof uploads."));
        permissions.put("payouts.manage", upsertPermission("payouts.manage", "Manage Payouts", "FINANCE", "Approve or reject payout requests."));
        permissions.put("refunds.manage", upsertPermission("refunds.manage", "Manage Refunds", "FINANCE", "Create and manage refunds."));
        permissions.put("user-sessions.manage", upsertPermission("user-sessions.manage", "Manage User Sessions", "SECURITY", "Inspect and revoke user sessions."));
        permissions.put("system.settings.manage", upsertPermission("system.settings.manage", "Manage System Settings", "SYSTEM", "Create and edit platform settings."));
        permissions.put("audit.logs.view", upsertPermission("audit.logs.view", "View Audit Logs", "SYSTEM", "Inspect the audit trail."));
        permissions.put("email.logs.view", upsertPermission("email.logs.view", "View Email Logs", "SYSTEM", "Inspect email delivery logs."));
        permissions.put("certificate-templates.manage", upsertPermission("certificate-templates.manage", "Manage Certificate Templates", "LEARNING", "Create and edit certificate templates."));
        return permissions;
    }

    private void ensureMappings(Map<String, Role> roles, Map<String, Permission> permissions) {
        grantAll(roles.get(ADMIN), permissions.values().stream().toList());

        grant(roles.get(INSTRUCTOR), permissions, List.of(
                "courses.approve",
                "instructor-profiles.manage",
                "certificate-templates.manage"
        ));
    }

    private void grant(Role role, Map<String, Permission> permissions, List<String> permissionNames) {
        if (role == null) {
            return;
        }
        for (String permissionName : permissionNames) {
            var permission = permissions.get(permissionName);
            if (permission != null) {
                upsertMapping(role, permission);
            }
        }
    }

    private void grantAll(Role role, List<Permission> permissions) {
        if (role == null) {
            return;
        }
        for (Permission permission : permissions) {
            upsertMapping(role, permission);
        }
    }

    private Role upsertRole(String name, String displayName, String description, boolean systemRole) {
        var role = roleRepository.findByNameIgnoreCase(name).orElseGet(Role::new);
        role.setName(name);
        role.setDisplayName(displayName);
        role.setDescription(description);
        role.setIsSystem(systemRole);
        return roleRepository.save(role);
    }

    private Permission upsertPermission(String name, String displayName, String module, String description) {
        var permission = permissionRepository.findByNameIgnoreCase(name).orElseGet(Permission::new);
        permission.setName(name);
        permission.setDisplayName(displayName);
        permission.setModule(module);
        permission.setDescription(description);
        return permissionRepository.save(permission);
    }

    private void upsertMapping(Role role, Permission permission) {
        if (rolePermissionRepository.existsByRole_NameIgnoreCaseAndPermission_NameIgnoreCase(role.getName(), permission.getName())) {
            return;
        }
        rolePermissionRepository.save(RolePermission.builder()
                .role(role)
                .permission(permission)
                .build());
    }
}