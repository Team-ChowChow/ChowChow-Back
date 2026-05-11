package com.petdiet.master.repository;

import com.petdiet.master.entity.Disease;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiseaseRepository extends JpaRepository<Disease, Integer> {
}
