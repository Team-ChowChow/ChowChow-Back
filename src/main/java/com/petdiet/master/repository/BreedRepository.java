package com.petdiet.master.repository;

import com.petdiet.master.entity.Breed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BreedRepository extends JpaRepository<Breed, Integer> {
}
