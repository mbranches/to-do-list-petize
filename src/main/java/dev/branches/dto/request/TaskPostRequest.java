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
        @Schema(example = "Realizar teste técnico", description = "título da tarefa")
        String title,
        @NotBlank(message = "O campo 'description' é obrigatório")
        @Schema(example = "Realizar teste técnico para vaga de estágio em backend da Petize", description = "descrição da tarefa")
        String description,
        @Schema(example = "2025-08-15", description = "data de vencimento")
        @NotNull(message = "O campo 'dueDate' é obrigatório")
        LocalDate dueDate,
        @Schema(example = "PENDENTE", description = "status da tarefa, caso não seja enviado, o status PENDENTE será definido por padrão")
        Optional<TaskStatus> status,
        @NotNull(message = "O campo 'priority' é obrigatório")
        @Schema(example = "ALTA", description = "prioridade da tarefa")
        Priority priority
) {}
