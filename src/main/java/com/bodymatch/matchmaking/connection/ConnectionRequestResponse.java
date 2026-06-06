package com.bodymatch.matchmaking.connection;

import java.time.Instant;

public record ConnectionRequestResponse(
        Long id,
        Long athleteId,
        Long coachId,
        String message,
        String status,
        Instant respondedAt,
        String responseNote) {

    public static ConnectionRequestResponse from(ConnectionRequest request) {
        return new ConnectionRequestResponse(
                request.getId(),
                request.getAthleteId().userId(),
                request.getCoachId().userId(),
                request.getMessage(),
                request.getStatus().name(),
                request.getRespondedAt(),
                request.getResponseNote());
    }
}
