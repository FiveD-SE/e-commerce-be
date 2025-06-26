package com.pm.userservice.repository;

import com.pm.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByCredentialUsername(final String username);
}
