package com.petdiet.character.repository;

import com.petdiet.character.entity.CharacterGrowthLog;
import com.petdiet.character.entity.PetCharacter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CharacterGrowthLogRepository extends JpaRepository<CharacterGrowthLog, Integer> {

    List<CharacterGrowthLog> findAllByCharacterOrderByCreatedAtDesc(PetCharacter character);
}
