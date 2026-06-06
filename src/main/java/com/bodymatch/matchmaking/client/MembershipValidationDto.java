package com.bodymatch.matchmaking.client;

import java.time.Instant;

public record MembershipValidationDto(
        Long userId,
        boolean active,
        String planCode,
        Instant currentPeriodEnd) {
}
