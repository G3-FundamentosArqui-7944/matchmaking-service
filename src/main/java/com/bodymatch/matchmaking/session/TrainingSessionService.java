package com.bodymatch.matchmaking.session;

import com.bodymatch.matchmaking.connection.ConnectionRequestRepository;
import com.bodymatch.matchmaking.connection.ConnectionRequestStatus;
import com.bodymatch.matchmaking.shared.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class TrainingSessionService {
    private final TrainingSessionRepository trainingSessionRepository;
    private final ConnectionRequestRepository connectionRequestRepository;

    public TrainingSessionService(TrainingSessionRepository trainingSessionRepository,
                                  ConnectionRequestRepository connectionRequestRepository) {
        this.trainingSessionRepository = trainingSessionRepository;
        this.connectionRequestRepository = connectionRequestRepository;
    }

    public List<TrainingSession> findByAthlete(UserId athleteId) {
        return trainingSessionRepository.findAllByAthleteIdOrderByScheduledAtDesc(athleteId);
    }

    public List<TrainingSession> findByCoach(UserId coachId) {
        return trainingSessionRepository.findAllByCoachIdOrderByScheduledAtDesc(coachId);
    }

    @Transactional
    public Optional<TrainingSession> schedule(ScheduleTrainingSessionRequest request) {
        var athleteId = new UserId(request.athleteId());
        var coachId = new UserId(request.coachId());
        var approved = connectionRequestRepository.findByAthleteIdAndCoachIdAndStatus(
                athleteId, coachId, ConnectionRequestStatus.APPROVED);
        if (approved.isEmpty()) {
            throw new IllegalStateException("Athlete and coach are not connected");
        }
        var sessionEnd = request.scheduledAt().plus(request.durationMinutes(), ChronoUnit.MINUTES);
        var conflicting = trainingSessionRepository.findAllByCoachIdAndScheduledAtBetween(
                coachId, request.scheduledAt(), sessionEnd);
        if (!conflicting.isEmpty()) {
            throw new IllegalStateException("Coach has a conflicting session at the requested time");
        }
        var session = new TrainingSession(athleteId, coachId, request.scheduledAt(),
                request.durationMinutes(), request.location(), request.notes());
        trainingSessionRepository.save(session);
        return Optional.of(session);
    }

    @Transactional
    public Optional<TrainingSession> complete(Long sessionId, String coachNotes) {
        var session = trainingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Training session not found"));
        session.complete(coachNotes);
        trainingSessionRepository.save(session);
        return Optional.of(session);
    }

    @Transactional
    public Optional<TrainingSession> cancel(Long sessionId) {
        var session = trainingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Training session not found"));
        session.cancel();
        trainingSessionRepository.save(session);
        return Optional.of(session);
    }
}
