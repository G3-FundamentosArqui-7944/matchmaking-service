package com.bodymatch.matchmaking.session;

import java.time.Instant;

public record TrainingSessionResponse(
        Long id,
        Long athleteId,
        Long coachId,
        Instant scheduledAt,
        int durationMinutes,
        String location,
        String notes,
        String status,
        Instant completedAt) {

    public static TrainingSessionResponse from(TrainingSession session) {
        return new TrainingSessionResponse(
                session.getId(),
                session.getAthleteId().userId(),
                session.getCoachId().userId(),
                session.getScheduledAt(),
                session.getDurationMinutes(),
                session.getLocation(),
                session.getNotes(),
                session.getStatus().name(),
                session.getCompletedAt());
    }
}
