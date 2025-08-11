package dev.branches.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(
        @Schema(example = "access-jwt-token", description = "token jwt de acesso")
        String accessToken
) {}
