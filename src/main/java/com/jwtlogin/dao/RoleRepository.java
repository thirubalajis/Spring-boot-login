package com.jwtlogin.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jwtlogin.model.Role;
import com.jwtlogin.model.Roles;

public interface RoleRepository extends JpaRepository<Role, Integer> {
	
	Optional<Role> findByRoleName(Roles role);

}
