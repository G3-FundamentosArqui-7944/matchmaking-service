package com.bodymatch.matchmaking.coach;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Embeddable
public record DayOfWeekSlot(
        @Enumerated(EnumType.STRING)
        @Column(name = "day_of_week", length = 20)
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime) {
    public DayOfWeekSlot {
        if (dayOfWeek == null) throw new IllegalArgumentException("Day of week required");
        if (startTime == null || endTime == null) throw new IllegalArgumentException("Start and end times required");
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
    }

    public DayOfWeekSlot() {
        this(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(9, 0));
    }
}
