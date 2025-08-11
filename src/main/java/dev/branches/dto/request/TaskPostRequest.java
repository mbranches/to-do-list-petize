package dev.branches.dto.request;

import dev.branches.entity.Priority;
import dev.branches.entity.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Optional;

public record TaskPostRequest(
        @NotBlank(message = "O campo 'title' é obrigatório")
        @Schema(example = "Limpar a casa", description = "título da tarefa")
        String title,
        @NotBlank(message = "O campo 'description' é obrigatório")
        @Schema(example = "Varrer sala e cozinha", description = "descrição da tarefa")
        String description,
        @Schema(example = "2025-08-15", description = "data de vencimento")
        @NotNull(message = "O campo 'dueDate' é obrigatório")
        LocalDate dueDate,
        @Schema(example = "PENDENTE", description = "status da tarefa")
        Optional<TaskStatus> status,
        @NotNull(message = "O campo 'priority' é obrigatório")
        @Schema(example = "ALTA", description = "prioridade da tarefa")
        Priority priority
) {}
