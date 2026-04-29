package com.petdiet.auth.repository;

import com.petdiet.auth.entity.AuthAccount;
import com.petdiet.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthAccountRepository extends JpaRepository<AuthAccount, Integer> {
    Optional<AuthAccount> findByUserAndAuthProvider(User user, String authProvider);
    Optional<AuthAccount> findFirstByUserOrderByAuthCreatedAtAsc(User user);
    boolean existsByAuthEmail(String authEmail);
}