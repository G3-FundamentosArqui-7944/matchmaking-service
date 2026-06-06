package com.bodymatch.matchmaking.coach;

import com.bodymatch.matchmaking.shared.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoachProfileRepository extends JpaRepository<CoachProfile, Long> {
    Optional<CoachProfile> findByUserId(UserId userId);
    boolean existsByUserId(UserId userId);
    List<CoachProfile> findAllByAcceptingClientsTrue();
}
