package com.cagkankantarci.e_ticaret.repository;

import com.cagkankantarci.e_ticaret.entity.ERole;
import com.cagkankantarci.e_ticaret.entity.Role;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}