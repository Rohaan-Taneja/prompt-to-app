package com.PromptToApp.core.repository;

import com.PromptToApp.core.Entity.User;
import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface userRepository extends JpaRepository<User , UUID> {


    Optional<User> findByEmail(String email);
}
