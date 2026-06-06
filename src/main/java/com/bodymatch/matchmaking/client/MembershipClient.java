package com.bodymatch.matchmaking.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "membership-service")
public interface MembershipClient {

    @GetMapping("/api/v1/subscriptions/user/{userId}/membership-status")
    MembershipValidationDto getMembershipStatus(@PathVariable("userId") Long userId);
}
