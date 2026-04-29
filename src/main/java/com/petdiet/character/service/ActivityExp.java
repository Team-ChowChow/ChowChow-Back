package com.petdiet.character.service;

public final class ActivityExp {

    private ActivityExp() {}

    public static int getExp(String activityType) {
        return switch (activityType) {
            case "DIET_RECOMMEND" -> 10;
            case "DIET_RECORD" -> 20;
            case "POST_WRITE" -> 15;
            case "COMMENT_WRITE" -> 5;
            default -> 0;
        };
    }
}
