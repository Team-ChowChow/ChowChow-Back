package com.petdiet.config;

import java.util.UUID;

public record SupabasePrincipal(
        UUID authUuid,
        String email,
        String name,
        String avatarUrl
) {}