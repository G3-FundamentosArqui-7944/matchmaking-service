package com.bodymatch.matchmaking.connection;

import com.bodymatch.matchmaking.client.IamGateway;
import com.bodymatch.matchmaking.client.MembershipGateway;
import com.bodymatch.matchmaking.coach.CoachProfileRepository;
import com.bodymatch.matchmaking.shared.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ConnectionRequestService {
    private final ConnectionRequestRepository connectionRepository;
    private final CoachProfileRepository coachRepository;
    private final IamGateway iamGateway;
    private final MembershipGateway membershipGateway;

    public ConnectionRequestService(ConnectionRequestRepository connectionRepository,
                                    CoachProfileRepository coachRepository,
                                    IamGateway iamGateway,
                                    MembershipGateway membershipGateway) {
        this.connectionRepository = connectionRepository;
        this.coachRepository = coachRepository;
        this.iamGateway = iamGateway;
        this.membershipGateway = membershipGateway;
    }

    public List<ConnectionRequest> findByAthlete(UserId athleteId) {
        return connectionRepository.findAllByAthleteIdOrderByCreatedAtDesc(athleteId);
    }

    public List<ConnectionRequest> findByCoach(UserId coachId) {
        return connectionRepository.findAllByCoachIdOrderByCreatedAtDesc(coachId);
    }

    public List<ConnectionRequest> findCoachClients(UserId coachId) {
        return connectionRepository.findAllByCoachIdAndStatus(coachId, ConnectionRequestStatus.APPROVED);
    }

    @Transactional
    public Optional<ConnectionRequest> request(CreateConnectionRequest request) {
        var athleteId = new UserId(request.athleteId());
        var coachId = new UserId(request.coachId());
        if (!iamGateway.isAthlete(athleteId)) {
            throw new IllegalStateException("Requester is not an athlete");
        }
        if (!membershipGateway.hasActiveMembership(athleteId)) {
            throw new IllegalStateException("Athlete must have an active membership to request coaching");
        }
        var coach = coachRepository.findByUserId(coachId)
                .orElseThrow(() -> new IllegalArgumentException("Coach profile not found"));
        if (!coach.isAcceptingClients()) {
            throw new IllegalStateException("Coach is not accepting new clients");
        }
        var existingPending = connectionRepository.findByAthleteIdAndCoachIdAndStatus(
                athleteId, coachId, ConnectionRequestStatus.PENDING);
        if (existingPending.isPresent()) {
            throw new IllegalStateException("A pending request already exists for this coach");
        }
        var existingApproved = connectionRepository.findByAthleteIdAndCoachIdAndStatus(
                athleteId, coachId, ConnectionRequestStatus.APPROVED);
        if (existingApproved.isPresent()) {
            throw new IllegalStateException("Athlete is already connected with this coach");
        }
        var entity = new ConnectionRequest(athleteId, coachId, request.message());
        connectionRepository.save(entity);
        return Optional.of(entity);
    }

    @Transactional
    public Optional<ConnectionRequest> respond(Long requestId, RespondConnectionRequest body) {
        var entity = connectionRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Connection request not found"));
        if (body.approve()) {
            entity.approve(body.responseNote());
        } else {
            entity.reject(body.responseNote());
        }
        connectionRepository.save(entity);
        return Optional.of(entity);
    }
}
