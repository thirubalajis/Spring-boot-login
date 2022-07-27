package com.jwtlogin.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jwtlogin.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	
	public Optional<User> findByUserName(String userName);
	public boolean existsByEmail(String email);
	public boolean existsByUserName(String userName);

}
