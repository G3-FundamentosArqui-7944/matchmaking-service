package com.bodymatch.matchmaking.athlete;

import java.util.Set;
import java.util.stream.Collectors;

public record AthleteProfileResponse(
        Long id,
        Long userId,
        String trainingLevel,
        Set<String> goals,
        String preferences) {

    public static AthleteProfileResponse from(AthleteProfile athlete) {
        var goalNames = athlete.getGoals().stream().map(FitnessGoal::name).collect(Collectors.toSet());
        return new AthleteProfileResponse(
                athlete.getId(),
                athlete.getUserId().userId(),
                athlete.getTrainingLevel().name(),
                goalNames,
                athlete.getPreferences());
    }
}
