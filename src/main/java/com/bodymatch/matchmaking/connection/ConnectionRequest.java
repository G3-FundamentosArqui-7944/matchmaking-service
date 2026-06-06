package com.bodymatch.matchmaking.connection;

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
public class ConnectionRequest extends AuditableAbstractAggregateRoot<ConnectionRequest> {

    @Embedded
    @Getter
    @AttributeOverride(name = "userId", column = @Column(name = "athlete_user_id", nullable = false))
    private UserId athleteId;

    @Embedded
    @Getter
    @AttributeOverride(name = "userId", column = @Column(name = "coach_user_id", nullable = false))
    private UserId coachId;

    @Getter
    @Column(length = 500)
    private String message;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ConnectionRequestStatus status;

    @Getter
    private Instant respondedAt;

    @Getter
    @Column(length = 500)
    private String responseNote;

    public ConnectionRequest(UserId athleteId, UserId coachId, String message) {
        if (athleteId == null || coachId == null) {
            throw new IllegalArgumentException("Athlete and coach ids are required");
        }
        if (athleteId.equals(coachId)) {
            throw new IllegalArgumentException("Athlete and coach cannot be the same user");
        }
        this.athleteId = athleteId;
        this.coachId = coachId;
        this.message = message;
        this.status = ConnectionRequestStatus.PENDING;
    }

    public void approve(String note) {
        if (status != ConnectionRequestStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be approved");
        }
        this.status = ConnectionRequestStatus.APPROVED;
        this.responseNote = note;
        this.respondedAt = Instant.now();
    }

    public void reject(String note) {
        if (status != ConnectionRequestStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be rejected");
        }
        this.status = ConnectionRequestStatus.REJECTED;
        this.responseNote = note;
        this.respondedAt = Instant.now();
    }

    public void cancel() {
        if (status == ConnectionRequestStatus.APPROVED) {
            throw new IllegalStateException("Approved requests cannot be canceled");
        }
        this.status = ConnectionRequestStatus.CANCELED;
        this.respondedAt = Instant.now();
    }
}
