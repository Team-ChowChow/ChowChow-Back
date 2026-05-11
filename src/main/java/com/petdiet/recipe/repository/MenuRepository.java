package com.petdiet.recipe.repository;

import com.petdiet.recipe.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Integer> {
    List<Menu> findAllByPetTypeAndMenuStatusOrderByMenuIdAsc(String petType, String menuStatus);
    Optional<Menu> findFirstByPetTypeAndMenuCategoryAndMenuStatus(String petType, String menuCategory, String menuStatus);
}
