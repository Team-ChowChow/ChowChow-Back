package com.petdiet.character.repository;

import com.petdiet.auth.entity.User;
import com.petdiet.character.entity.PetCharacter;
import com.petdiet.pet.entity.UserPet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface PetCharacterRepository extends JpaRepository<PetCharacter, Integer> {

    Optional<PetCharacter> findByPet(UserPet pet);

    boolean existsByPet(UserPet pet);

    List<PetCharacter> findAllByPet_User(User user);

    Optional<PetCharacter> findByCharacterIdAndPet_User(Integer characterId, User user);
}
