package com.petdiet.user.repository;

import com.petdiet.user.entity.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSettingsRepository extends JpaRepository<UserSettings, Integer> {
}