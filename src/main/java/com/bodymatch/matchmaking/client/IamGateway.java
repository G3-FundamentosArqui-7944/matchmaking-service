package com.bodymatch.matchmaking.client;

import com.bodymatch.matchmaking.shared.UserId;
import feign.FeignException;
import org.springframework.stereotype.Service;

@Service
public class IamGateway {
    private final IamClient iamClient;

    public IamGateway(IamClient iamClient) {
        this.iamClient = iamClient;
    }

    public boolean existsUser(UserId userId) {
        try {
            iamClient.getUserById(userId.userId());
            return true;
        } catch (FeignException.NotFound e) {
            return false;
        }
    }

    public boolean isAthlete(UserId userId) {
        return hasRole(userId, "ROLE_ATHLETE");
    }

    public boolean isCoach(UserId userId) {
        return hasRole(userId, "ROLE_COACH");
    }

    private boolean hasRole(UserId userId, String role) {
        try {
            return iamClient.getUserById(userId.userId()).hasRole(role);
        } catch (FeignException.NotFound e) {
            return false;
        }
    }
}
