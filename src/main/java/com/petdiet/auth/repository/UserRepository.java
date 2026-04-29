package com.petdiet.auth.repository;

import com.petdiet.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByAuthUuid(UUID authUuid);
    boolean existsByUserNickname(String userNickname);
}