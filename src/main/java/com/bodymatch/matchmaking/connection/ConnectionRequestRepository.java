package com.bodymatch.matchmaking.connection;

import com.bodymatch.matchmaking.shared.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConnectionRequestRepository extends JpaRepository<ConnectionRequest, Long> {
    List<ConnectionRequest> findAllByAthleteIdOrderByCreatedAtDesc(UserId athleteId);
    List<ConnectionRequest> findAllByCoachIdOrderByCreatedAtDesc(UserId coachId);
    List<ConnectionRequest> findAllByCoachIdAndStatus(UserId coachId, ConnectionRequestStatus status);
    Optional<ConnectionRequest> findByAthleteIdAndCoachIdAndStatus(UserId athleteId, UserId coachId, ConnectionRequestStatus status);
}
