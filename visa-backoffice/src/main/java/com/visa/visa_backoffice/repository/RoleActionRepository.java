package com.visa.visa_backoffice.repository;

import com.visa.visa_backoffice.model.RoleAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoleActionRepository extends JpaRepository<RoleAction, Integer> {

    @Query("select ra.action from RoleAction ra where ra.roleCode = :role")
    List<String> findActionsByRole(@Param("role") String role);
}
