package com.bodymatch.matchmaking.coach;

import com.bodymatch.matchmaking.shared.AuditableModel;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
public class AvailabilitySlot extends AuditableModel {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @Getter
    private DayOfWeekSlot slot;

    @Getter
    @Column(nullable = false)
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coach_profile_id")
    @Getter
    @Setter
    private CoachProfile coachProfile;

    public AvailabilitySlot(DayOfWeekSlot slot) {
        this.slot = slot;
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
