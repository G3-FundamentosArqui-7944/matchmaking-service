package com.bodymatch.matchmaking.athlete;

import com.bodymatch.matchmaking.shared.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AthleteProfileRepository extends JpaRepository<AthleteProfile, Long> {
    Optional<AthleteProfile> findByUserId(UserId userId);
    boolean existsByUserId(UserId userId);
}
