package com.bodymatch.matchmaking.connection;

public record CreateConnectionRequest(Long athleteId, Long coachId, String message) {
}
