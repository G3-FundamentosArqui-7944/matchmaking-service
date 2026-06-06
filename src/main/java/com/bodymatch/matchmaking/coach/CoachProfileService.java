package com.bodymatch.matchmaking.coach;

import com.bodymatch.matchmaking.athlete.AthleteProfile;
import com.bodymatch.matchmaking.athlete.AthleteProfileRepository;
import com.bodymatch.matchmaking.athlete.FitnessGoal;
import com.bodymatch.matchmaking.client.IamGateway;
import com.bodymatch.matchmaking.shared.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CoachProfileService {
    private final CoachProfileRepository coachRepository;
    private final AthleteProfileRepository athleteRepository;
    private final IamGateway iamGateway;

    public CoachProfileService(CoachProfileRepository coachRepository,
                               AthleteProfileRepository athleteRepository,
                               IamGateway iamGateway) {
        this.coachRepository = coachRepository;
        this.athleteRepository = athleteRepository;
        this.iamGateway = iamGateway;
    }

    public Optional<CoachProfile> findByUserId(UserId userId) {
        return coachRepository.findByUserId(userId);
    }

    public List<CoachProfile> search(Set<Specialty> specialties, Integer minYearsOfExperience,
                                     BigDecimal maxHourlyRate, BigDecimal minRating, boolean onlyAccepting) {
        var coaches = onlyAccepting
                ? coachRepository.findAllByAcceptingClientsTrue()
                : coachRepository.findAll();
        return coaches.stream()
                .filter(c -> matchesSpecialties(c, specialties))
                .filter(c -> minYearsOfExperience == null || c.getYearsOfExperience() >= minYearsOfExperience)
                .filter(c -> maxHourlyRate == null || c.getHourlyRate().compareTo(maxHourlyRate) <= 0)
                .filter(c -> minRating == null || c.getAverageRating().compareTo(minRating) >= 0)
                .sorted(Comparator.comparing(CoachProfile::getAverageRating).reversed())
                .toList();
    }

    public List<CoachProfile> recommendFor(UserId athleteId, int limit) {
        var limited = limit <= 0 ? 10 : limit;
        var athleteOpt = athleteRepository.findByUserId(athleteId);
        var allCoaches = coachRepository.findAllByAcceptingClientsTrue();
        if (athleteOpt.isEmpty()) {
            return allCoaches.stream()
                    .sorted(Comparator.comparing(CoachProfile::getAverageRating).reversed())
                    .limit(limited)
                    .toList();
        }
        AthleteProfile athlete = athleteOpt.get();
        return allCoaches.stream()
                .sorted(Comparator.comparingInt((CoachProfile c) -> -scoreFor(c, athlete))
                        .thenComparing(CoachProfile::getAverageRating, Comparator.reverseOrder()))
                .limit(limited)
                .toList();
    }

    @Transactional
    public Optional<CoachProfile> create(CreateCoachProfileRequest request) {
        var userId = new UserId(request.userId());
        if (!iamGateway.existsUser(userId)) {
            throw new IllegalArgumentException("User does not exist: " + request.userId());
        }
        if (!iamGateway.isCoach(userId)) {
            throw new IllegalStateException("User does not have ROLE_COACH");
        }
        if (coachRepository.existsByUserId(userId)) {
            throw new IllegalStateException("Coach profile already exists for user");
        }
        var specialties = request.specialties().stream().map(Specialty::valueOf).collect(Collectors.toSet());
        var profile = new CoachProfile(
                userId,
                request.biography(),
                request.yearsOfExperience(),
                request.hourlyRate(),
                request.currency(),
                specialties);
        coachRepository.save(profile);
        return Optional.of(profile);
    }

    @Transactional
    public Optional<CoachProfile> addAvailability(UserId coachId, AddAvailabilityRequest request) {
        var profile = coachRepository.findByUserId(coachId)
                .orElseThrow(() -> new IllegalArgumentException("Coach profile not found"));
        var slot = new AvailabilitySlot(new DayOfWeekSlot(
                DayOfWeek.valueOf(request.dayOfWeek()),
                LocalTime.parse(request.startTime()),
                LocalTime.parse(request.endTime())));
        profile.addAvailability(slot);
        coachRepository.save(profile);
        return Optional.of(profile);
    }

    private boolean matchesSpecialties(CoachProfile coach, Set<Specialty> requested) {
        if (requested == null || requested.isEmpty()) return true;
        return coach.getSpecialties().stream().anyMatch(requested::contains);
    }

    private int scoreFor(CoachProfile coach, AthleteProfile athlete) {
        int score = 0;
        var goalSpecialties = goalsToSpecialties(athlete);
        for (var spec : coach.getSpecialties()) {
            if (goalSpecialties.contains(spec)) score += 5;
        }
        if (coach.getYearsOfExperience() >= 5) score += 2;
        if (coach.getAverageRating().compareTo(BigDecimal.valueOf(4)) >= 0) score += 3;
        return score;
    }

    private Set<Specialty> goalsToSpecialties(AthleteProfile athlete) {
        var result = new HashSet<Specialty>();
        athlete.getGoals().forEach(goal -> {
            switch (goal) {
                case WEIGHT_LOSS -> { result.add(Specialty.HIIT); result.add(Specialty.CARDIO); result.add(Specialty.NUTRITION_COACHING); }
                case MUSCLE_GAIN -> { result.add(Specialty.STRENGTH_TRAINING); result.add(Specialty.BODYBUILDING); }
                case STRENGTH -> { result.add(Specialty.POWERLIFTING); result.add(Specialty.STRENGTH_TRAINING); }
                case ENDURANCE -> { result.add(Specialty.CARDIO); result.add(Specialty.HIIT); }
                case MOBILITY -> { result.add(Specialty.MOBILITY); result.add(Specialty.YOGA); }
                case GENERAL_FITNESS -> { result.add(Specialty.CROSSFIT); result.add(Specialty.HIIT); }
                case COMPETITION_PREP -> { result.add(Specialty.BODYBUILDING); result.add(Specialty.STRENGTH_TRAINING); result.add(Specialty.NUTRITION_COACHING); }
            }
        });
        return result;
    }
}
