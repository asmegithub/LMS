package com.EGM.LMS.repository;

import com.EGM.LMS.model.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RolePermissionRepository extends JpaRepository<RolePermission, UUID> {
	List<RolePermission> findByRole_NameIgnoreCase(String roleName);

	boolean existsByRole_NameIgnoreCaseAndPermission_NameIgnoreCase(String roleName, String permissionName);
}
