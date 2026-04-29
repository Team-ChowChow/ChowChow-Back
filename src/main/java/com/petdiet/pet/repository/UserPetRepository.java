package com.petdiet.pet.repository;

import com.petdiet.auth.entity.User;
import com.petdiet.pet.entity.UserPet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserPetRepository extends JpaRepository<UserPet, Integer> {
    List<UserPet> findAllByUser(User user);
    Optional<UserPet> findByPetIdAndUser(Integer petId, User user);
}
