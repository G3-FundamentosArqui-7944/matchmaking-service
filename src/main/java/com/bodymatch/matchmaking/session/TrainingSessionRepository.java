package com.bodymatch.matchmaking.session;

import com.bodymatch.matchmaking.shared.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {
    List<TrainingSession> findAllByAthleteIdOrderByScheduledAtDesc(UserId athleteId);
    List<TrainingSession> findAllByCoachIdOrderByScheduledAtDesc(UserId coachId);
    List<TrainingSession> findAllByCoachIdAndScheduledAtBetween(UserId coachId, Instant start, Instant end);
}
