package dev.branches.exception;

import io.swagger.v3.oas.annotations.media.Schema;

public record DefaultErrorMessage(
        @Schema(description = "response error status code")
        Integer status,
        @Schema(example = "message", description = "response error message")
        String message
) {}
