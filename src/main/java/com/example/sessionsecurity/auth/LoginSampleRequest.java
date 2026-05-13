package com.example.sessionsecurity.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginSampleRequest(
        @NotBlank String userId,
        String userName,
        @NotBlank String uuid,
        @NotBlank String client
) {
}
