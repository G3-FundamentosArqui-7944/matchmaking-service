package com.bodymatch.matchmaking.client;

import com.bodymatch.matchmaking.shared.UserId;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MembershipGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(MembershipGateway.class);

    private final MembershipClient membershipClient;

    public MembershipGateway(MembershipClient membershipClient) {
        this.membershipClient = membershipClient;
    }

    public boolean hasActiveMembership(UserId userId) {
        try {
            return membershipClient.getMembershipStatus(userId.userId()).active();
        } catch (FeignException e) {
            LOGGER.warn("Failed to validate membership for user {}: {}", userId.userId(), e.getMessage());
            return false;
        }
    }
}
