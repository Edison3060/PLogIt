package io.muzoo.ssc.plogit.web.dto;

import io.muzoo.ssc.plogit.domain.User;

public record UserResponse(
    Long id,
    String email,
    String displayName
) {
    public static UserResponse from(User user) {
        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getDisplayName()
        );
    }
}
