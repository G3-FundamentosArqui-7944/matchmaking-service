package com.bodymatch.matchmaking.session;

import com.bodymatch.matchmaking.shared.AuditableAbstractAggregateRoot;
import com.bodymatch.matchmaking.shared.UserId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@NoArgsConstructor
public class TrainingSession extends AuditableAbstractAggregateRoot<TrainingSession> {

    @Embedded
    @Getter
    @AttributeOverride(name = "userId", column = @Column(name = "athlete_user_id", nullable = false))
    private UserId athleteId;

    @Embedded
    @Getter
    @AttributeOverride(name = "userId", column = @Column(name = "coach_user_id", nullable = false))
    private UserId coachId;

    @Getter
    @Column(nullable = false)
    private Instant scheduledAt;

    @Getter
    @Column(nullable = false)
    private int durationMinutes;

    @Getter
    @Column(length = 500)
    private String location;

    @Getter
    @Column(length = 500)
    private String notes;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TrainingSessionStatus status;

    @Getter
    private Instant completedAt;

    public TrainingSession(UserId athleteId, UserId coachId, Instant scheduledAt,
                           int durationMinutes, String location, String notes) {
        if (athleteId == null || coachId == null) {
            throw new IllegalArgumentException("Athlete and coach ids are required");
        }
        if (athleteId.equals(coachId)) {
            throw new IllegalArgumentException("Athlete and coach cannot be the same user");
        }
        if (scheduledAt == null || scheduledAt.isBefore(Instant.now())) {
            throw new IllegalArgumentException("Scheduled time must be in the future");
        }
        if (durationMinutes <= 0 || durationMinutes > 240) {
            throw new IllegalArgumentException("Duration must be between 1 and 240 minutes");
        }
        this.athleteId = athleteId;
        this.coachId = coachId;
        this.scheduledAt = scheduledAt;
        this.durationMinutes = durationMinutes;
        this.location = location;
        this.notes = notes;
        this.status = TrainingSessionStatus.SCHEDULED;
    }

    public void confirm() {
        if (status != TrainingSessionStatus.SCHEDULED) {
            throw new IllegalStateException("Only scheduled sessions can be confirmed");
        }
        this.status = TrainingSessionStatus.CONFIRMED;
    }

    public void complete(String coachNotes) {
        if (status != TrainingSessionStatus.CONFIRMED && status != TrainingSessionStatus.SCHEDULED) {
            throw new IllegalStateException("Only confirmed/scheduled sessions can be completed");
        }
        this.status = TrainingSessionStatus.COMPLETED;
        this.notes = coachNotes != null ? coachNotes : this.notes;
        this.completedAt = Instant.now();
    }

    public void cancel() {
        if (status == TrainingSessionStatus.COMPLETED) {
            throw new IllegalStateException("Completed sessions cannot be canceled");
        }
        this.status = TrainingSessionStatus.CANCELED;
    }

    public void markNoShow() {
        if (status != TrainingSessionStatus.CONFIRMED && status != TrainingSessionStatus.SCHEDULED) {
            throw new IllegalStateException("Only scheduled/confirmed sessions can be marked as no-show");
        }
        this.status = TrainingSessionStatus.NO_SHOW;
    }
}
