package com.example.demo.model.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.persistence.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);
}
