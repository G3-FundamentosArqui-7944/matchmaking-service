package com.bodymatch.matchmaking.coach;

public record AvailabilitySlotResponse(
        Long id,
        String dayOfWeek,
        String startTime,
        String endTime,
        boolean active) {

    public static AvailabilitySlotResponse from(AvailabilitySlot slot) {
        return new AvailabilitySlotResponse(
                slot.getId(),
                slot.getSlot().dayOfWeek().name(),
                slot.getSlot().startTime().toString(),
                slot.getSlot().endTime().toString(),
                slot.isActive());
    }
}
