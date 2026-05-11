package com.petdiet.master.repository;

import com.petdiet.master.entity.AllergyIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AllergyIngredientRepository extends JpaRepository<AllergyIngredient, Integer> {

    List<AllergyIngredient> findAllByAllergyAllergyIdIn(List<Integer> allergyIds);
}
