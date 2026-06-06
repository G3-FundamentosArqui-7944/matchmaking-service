package com.bodymatch.matchmaking.coach;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record CoachProfileResponse(
        Long id,
        Long userId,
        String biography,
        int yearsOfExperience,
        BigDecimal hourlyRate,
        String currency,
        Set<String> specialties,
        boolean acceptingClients,
        BigDecimal averageRating,
        int totalReviews,
        List<AvailabilitySlotResponse> availabilitySlots) {

    public static CoachProfileResponse from(CoachProfile coach) {
        var specialties = coach.getSpecialties().stream().map(Specialty::name).collect(Collectors.toSet());
        var slots = coach.getAvailabilitySlots().stream()
                .map(AvailabilitySlotResponse::from)
                .toList();
        return new CoachProfileResponse(
                coach.getId(),
                coach.getUserId().userId(),
                coach.getBiography(),
                coach.getYearsOfExperience(),
                coach.getHourlyRate(),
                coach.getCurrency(),
                specialties,
                coach.isAcceptingClients(),
                coach.getAverageRating(),
                coach.getTotalReviews(),
                slots);
    }
}
