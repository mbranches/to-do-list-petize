package dev.branches.dto.request;

import dev.branches.entity.Priority;
import dev.branches.entity.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Optional;

public record TaskPutRequest(
        @NotBlank(message = "O campo 'id' é obrigatório")
        @Schema(example = "uuid.task-1", description = "id da tarefa a atualizar")
        String id,
        @NotBlank(message = "O campo 'title' é obrigatório")
        @Schema(example = "Realizar teste técnico da PETIZE", description = "título da tarefa")
        String title,
        @NotBlank(message = "O campo 'description' é obrigatório")
        @Schema(example = "Nova descrição", description = "descrição da tarefa")
        String description,
        @NotNull(message = "O campo 'dueDate' é obrigatório")
        @Schema(example = "2025-08-10", description = "data de vencimento")
        LocalDate dueDate,
        @Schema(example = "PENDENTE", description = "status da tarefa, caso não enviada o status definido será PENDENTE")
        Optional<TaskStatus> status,
        @NotNull(message = "O campo 'priority' é obrigatório")
        @Schema(example = "ALTA", description = "prioridade da tarefa")
        Priority priority
) {}
