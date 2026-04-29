package com.petdiet.character.dto;

import com.petdiet.character.entity.CharacterGrowthLog;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder
public class GrowthLogResponse {
    private Integer growthLogId;
    private String activityType;
    private String growthType;
    private Integer expGained;
    private Integer growthValue;
    private Integer currentExp;
    private Integer currentLevel;
    private String description;
    private OffsetDateTime createdAt;

    public static GrowthLogResponse from(CharacterGrowthLog log) {
        return GrowthLogResponse.builder()
                .growthLogId(log.getGrowthLogId())
                .activityType(log.getActivityType())
                .growthType(log.getActivityType())
                .expGained(log.getExpGained())
                .growthValue(log.getExpGained())
                .currentExp(log.getCurrentExp())
                .currentLevel(log.getCurrentLevel())
                .description(null)
                .createdAt(log.getCreatedAt())
                .build();
    }
}
