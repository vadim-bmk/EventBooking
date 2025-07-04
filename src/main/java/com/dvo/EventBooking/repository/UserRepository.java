package com.dvo.EventBooking.repository;

import com.dvo.EventBooking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByUsernameAndEmail(String username, String email);
    Optional<User> findByUsername(String username);
    void deleteByUsername(String username);
}
