package dev.branches.dto;

import dev.branches.entity.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record StatusPatchRequest(
        @Schema(description = "novo status")
        TaskStatus status
) {}
