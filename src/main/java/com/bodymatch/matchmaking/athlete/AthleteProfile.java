package com.bodymatch.matchmaking.athlete;

import com.bodymatch.matchmaking.shared.AuditableAbstractAggregateRoot;
import com.bodymatch.matchmaking.shared.UserId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
public class AthleteProfile extends AuditableAbstractAggregateRoot<AthleteProfile> {

    @Embedded
    @Getter
    @AttributeOverride(name = "userId", column = @Column(name = "user_id", nullable = false, unique = true))
    private UserId userId;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TrainingLevel trainingLevel;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "athlete_profile_goals", joinColumns = @JoinColumn(name = "athlete_profile_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "goal", nullable = false, length = 30)
    @Getter
    private Set<FitnessGoal> goals;

    @Getter
    @Column(length = 500)
    private String preferences;

    public AthleteProfile(UserId userId, TrainingLevel trainingLevel, Set<FitnessGoal> goals, String preferences) {
        if (trainingLevel == null) {
            throw new IllegalArgumentException("Training level required");
        }
        if (goals == null || goals.isEmpty()) {
            throw new IllegalArgumentException("Athlete must declare at least one goal");
        }
        this.userId = userId;
        this.trainingLevel = trainingLevel;
        this.goals = new HashSet<>(goals);
        this.preferences = preferences;
    }

    public void updateProfile(TrainingLevel level, Set<FitnessGoal> goals, String preferences) {
        if (level != null) this.trainingLevel = level;
        if (goals != null && !goals.isEmpty()) this.goals = new HashSet<>(goals);
        this.preferences = preferences;
    }
}
