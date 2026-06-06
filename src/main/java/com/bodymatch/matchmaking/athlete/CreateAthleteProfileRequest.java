package com.bodymatch.matchmaking.athlete;

import java.util.Set;

public record CreateAthleteProfileRequest(
        Long userId,
        String trainingLevel,
        Set<String> goals,
        String preferences) {
}
