package com.bodymatch.matchmaking.client;

import java.util.List;

public record UserDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        String phone,
        boolean active,
        boolean emailVerified,
        List<String> roles) {

    public boolean hasRole(String roleName) {
        return roles != null && roles.contains(roleName);
    }
}
