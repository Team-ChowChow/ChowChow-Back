package com.petdiet.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbc;

    @Override
    public void run(ApplicationArguments args) {
        log.info("마스터 데이터 초기화 시작");
        loadAllergies();
        loadDiseases();
        loadIngredients();
        loadBreeds();
        loadAllergyIngredients();
        loadMenus();
        loadRecipeTags();
        log.info("마스터 데이터 초기화 완료");
    }

    private void loadAllergies() {
        if (count("Allergies") > 0) return;
        List<Object[]> rows = new ArrayList<>();
        for (CSVRecord r : readCsv("db/csv/allergies.csv")) {
            rows.add(new Object[]{ r.get("allergyName"), r.get("allergyDescription") });
        }
        jdbc.batchUpdate(
            "INSERT INTO \"Allergies\" (\"allergyName\", \"allergyDescription\") VALUES (?, ?) ON CONFLICT DO NOTHING",
            rows
        );
        log.info("Allergies 삽입: {}건", rows.size());
    }

    private void loadDiseases() {
        if (count("Diseases") > 0) return;
        List<Object[]> rows = new ArrayList<>();
        for (CSVRecord r : readCsv("db/csv/diseases.csv")) {
            rows.add(new Object[]{ r.get("diseaseName"), r.get("diseaseDescription") });
        }
        jdbc.batchUpdate(
            "INSERT INTO \"Diseases\" (\"diseaseName\", \"diseaseDescription\") VALUES (?, ?) ON CONFLICT DO NOTHING",
            rows
        );
        log.info("Diseases 삽입: {}건", rows.size());
    }

    private void loadIngredients() {
        if (count("Ingredients") > 0) return;
        List<Object[]> rows = new ArrayList<>();
        for (CSVRecord r : readCsv("db/csv/ingredients.csv")) {
            rows.add(new Object[]{
                r.get("ingredientName"),
                r.get("ingredientDescription"),
                r.get("ingredientCategory"),
                r.get("petType")
            });
        }
        jdbc.batchUpdate(
            "INSERT INTO \"Ingredients\" (\"ingredientName\", \"ingredientDescription\", \"ingredientCategory\", \"petType\") VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING",
            rows
        );
        log.info("Ingredients 삽입: {}건", rows.size());
    }

    private void loadBreeds() {
        if (count("Breeds") > 0) return;
        List<Object[]> rows = new ArrayList<>();
        for (CSVRecord r : readCsv("db/csv/breeds.csv")) {
            String desc = r.get("breedDescription");
            rows.add(new Object[]{
                r.get("petType"),
                r.get("breedName"),
                (desc == null || desc.isBlank()) ? null : desc
            });
        }
        jdbc.batchUpdate(
            "INSERT INTO \"Breeds\" (\"petType\", \"breedName\", \"breedDescription\") VALUES (?, ?, ?) ON CONFLICT DO NOTHING",
            rows
        );
        log.info("Breeds 삽입: {}건", rows.size());
    }

    private void loadAllergyIngredients() {
        if (count("AllergyIngredients") > 0) return;
        List<Object[]> rows = new ArrayList<>();
        for (CSVRecord r : readCsv("db/csv/allergy_ingredients.csv")) {
            rows.add(new Object[]{ r.get("allergyName"), r.get("ingredientName") });
        }
        jdbc.batchUpdate("""
            INSERT INTO "AllergyIngredients" ("allergyId", "ingredientId")
            SELECT a."allergyId", i."ingredientId"
            FROM "Allergies" a, "Ingredients" i
            WHERE a."allergyName" = ? AND i."ingredientName" = ?
            ON CONFLICT DO NOTHING
            """,
            rows
        );
        log.info("AllergyIngredients 삽입: {}건", rows.size());
    }

    private void loadMenus() {
        if (count("Menus") > 0) return;
        List<Object[]> rows = new ArrayList<>();
        for (CSVRecord r : readCsv("db/csv/menus.csv")) {
            rows.add(new Object[]{
                r.get("menuName"),
                r.get("menuDescription"),
                r.get("petType"),
                r.get("menuCategory"),
                r.get("menuStatus")
            });
        }
        jdbc.batchUpdate(
            "INSERT INTO \"Menus\" (\"menuName\", \"menuDescription\", \"petType\", \"menuCategory\", \"menuStatus\") VALUES (?, ?, ?, ?, ?) ON CONFLICT DO NOTHING",
            rows
        );
        log.info("Menus 삽입: {}건", rows.size());
    }

    private void loadRecipeTags() {
        if (count("RecipeTags") > 0) return;
        List<Object[]> rows = new ArrayList<>();
        for (CSVRecord r : readCsv("db/csv/recipe_tags.csv")) {
            rows.add(new Object[]{ r.get("tagName"), r.get("tagType"), r.get("tagDescription") });
        }
        jdbc.batchUpdate(
            "INSERT INTO \"RecipeTags\" (\"tagName\", \"tagType\", \"tagDescription\") VALUES (?, ?, ?) ON CONFLICT DO NOTHING",
            rows
        );
        log.info("RecipeTags 삽입: {}건", rows.size());
    }

    private long count(String table) {
        Long n = jdbc.queryForObject("SELECT COUNT(*) FROM \"" + table + "\"", Long.class);
        return n == null ? 0 : n;
    }

    private Iterable<CSVRecord> readCsv(String path) {
        try {
            byte[] bytes = new ClassPathResource(path).getInputStream().readAllBytes();
            String content = new String(bytes, StandardCharsets.UTF_8);
            if (content.startsWith("﻿")) {
                content = content.substring(1);
            }
            return CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setTrim(true)
                .build()
                .parse(new java.io.StringReader(content));
        } catch (Exception e) {
            throw new RuntimeException("CSV 읽기 실패: " + path, e);
        }
    }
}
