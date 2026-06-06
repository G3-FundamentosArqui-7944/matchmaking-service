package com.bodymatch.matchmaking.athlete;

import com.bodymatch.matchmaking.client.IamGateway;
import com.bodymatch.matchmaking.shared.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AthleteProfileService {
    private final AthleteProfileRepository athleteRepository;
    private final IamGateway iamGateway;

    public AthleteProfileService(AthleteProfileRepository athleteRepository, IamGateway iamGateway) {
        this.athleteRepository = athleteRepository;
        this.iamGateway = iamGateway;
    }

    public Optional<AthleteProfile> findByUserId(UserId userId) {
        return athleteRepository.findByUserId(userId);
    }

    @Transactional
    public Optional<AthleteProfile> create(CreateAthleteProfileRequest request) {
        var userId = new UserId(request.userId());
        if (!iamGateway.existsUser(userId)) {
            throw new IllegalArgumentException("User does not exist: " + request.userId());
        }
        if (!iamGateway.isAthlete(userId)) {
            throw new IllegalStateException("User does not have ROLE_ATHLETE");
        }
        if (athleteRepository.existsByUserId(userId)) {
            throw new IllegalStateException("Athlete profile already exists for user");
        }
        var goals = request.goals().stream().map(FitnessGoal::valueOf).collect(Collectors.toSet());
        var profile = new AthleteProfile(
                userId,
                TrainingLevel.valueOf(request.trainingLevel()),
                goals,
                request.preferences());
        athleteRepository.save(profile);
        return Optional.of(profile);
    }
}
