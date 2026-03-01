package com.pratik.aiadgenerator.repository;

import com.pratik.aiadgenerator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // Inside UserRepository.java
    Optional<User> findByResetToken(String resetToken);
}

