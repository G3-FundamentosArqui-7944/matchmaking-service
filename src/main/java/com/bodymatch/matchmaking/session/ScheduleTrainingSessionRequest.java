package com.bodymatch.matchmaking.session;

import java.time.Instant;

public record ScheduleTrainingSessionRequest(
        Long athleteId,
        Long coachId,
        Instant scheduledAt,
        int durationMinutes,
        String location,
        String notes) {
}
