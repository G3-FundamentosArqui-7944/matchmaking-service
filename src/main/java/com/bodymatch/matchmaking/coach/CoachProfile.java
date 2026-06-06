package com.bodymatch.matchmaking.coach;

import com.bodymatch.matchmaking.shared.AuditableAbstractAggregateRoot;
import com.bodymatch.matchmaking.shared.UserId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
public class CoachProfile extends AuditableAbstractAggregateRoot<CoachProfile> {

    @Embedded
    @Getter
    @AttributeOverride(name = "userId", column = @Column(name = "user_id", nullable = false, unique = true))
    private UserId userId;

    @Getter
    @Column(length = 1000)
    private String biography;

    @Getter
    @Column(nullable = false)
    private int yearsOfExperience;

    @Getter
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @Getter
    @Column(length = 3)
    private String currency;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "coach_profile_specialties", joinColumns = @JoinColumn(name = "coach_profile_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "specialty", nullable = false, length = 30)
    @Getter
    private Set<Specialty> specialties;

    @OneToMany(mappedBy = "coachProfile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Getter
    private List<AvailabilitySlot> availabilitySlots;

    @Getter
    @Column(nullable = false)
    private boolean acceptingClients;

    @Getter
    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal averageRating;

    @Getter
    @Column(nullable = false)
    private int totalReviews;

    public CoachProfile(UserId userId, String biography, int yearsOfExperience,
                        BigDecimal hourlyRate, String currency, Set<Specialty> specialties) {
        if (yearsOfExperience < 0) {
            throw new IllegalArgumentException("Years of experience must be non-negative");
        }
        if (hourlyRate == null || hourlyRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Hourly rate must be non-negative");
        }
        if (specialties == null || specialties.isEmpty()) {
            throw new IllegalArgumentException("Coach must declare at least one specialty");
        }
        this.userId = userId;
        this.biography = biography;
        this.yearsOfExperience = yearsOfExperience;
        this.hourlyRate = hourlyRate;
        this.currency = currency == null ? "USD" : currency;
        this.specialties = new HashSet<>(specialties);
        this.availabilitySlots = new ArrayList<>();
        this.acceptingClients = true;
        this.averageRating = BigDecimal.ZERO;
        this.totalReviews = 0;
    }

    public void addAvailability(AvailabilitySlot slot) {
        slot.setCoachProfile(this);
        this.availabilitySlots.add(slot);
    }

    public void removeAvailability(Long slotId) {
        this.availabilitySlots.removeIf(s -> s.getId().equals(slotId));
    }

    public void updateProfile(String biography, int yearsOfExperience, BigDecimal hourlyRate, String currency) {
        if (yearsOfExperience < 0) {
            throw new IllegalArgumentException("Years of experience must be non-negative");
        }
        if (hourlyRate == null || hourlyRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Hourly rate must be non-negative");
        }
        this.biography = biography;
        this.yearsOfExperience = yearsOfExperience;
        this.hourlyRate = hourlyRate;
        if (currency != null) this.currency = currency;
    }

    public void replaceSpecialties(Set<Specialty> specialties) {
        if (specialties == null || specialties.isEmpty()) {
            throw new IllegalArgumentException("Coach must declare at least one specialty");
        }
        this.specialties = new HashSet<>(specialties);
    }

    public void closeForClients() { this.acceptingClients = false; }
    public void openForClients() { this.acceptingClients = true; }

    public void registerRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        var totalScore = this.averageRating.multiply(BigDecimal.valueOf(this.totalReviews))
                .add(BigDecimal.valueOf(rating));
        this.totalReviews += 1;
        this.averageRating = totalScore.divide(BigDecimal.valueOf(this.totalReviews), 2, RoundingMode.HALF_UP);
    }
}
