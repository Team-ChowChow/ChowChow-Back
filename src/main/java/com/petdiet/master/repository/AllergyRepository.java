package com.petdiet.master.repository;

import com.petdiet.master.entity.Allergy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AllergyRepository extends JpaRepository<Allergy, Integer> {
}
