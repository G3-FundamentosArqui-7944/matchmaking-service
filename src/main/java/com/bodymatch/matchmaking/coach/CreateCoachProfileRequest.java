package com.bodymatch.matchmaking.coach;

import java.math.BigDecimal;
import java.util.Set;

public record CreateCoachProfileRequest(
        Long userId,
        String biography,
        int yearsOfExperience,
        BigDecimal hourlyRate,
        String currency,
        Set<String> specialties) {
}
