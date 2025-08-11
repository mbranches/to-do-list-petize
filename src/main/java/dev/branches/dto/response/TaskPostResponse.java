package dev.branches.dto.response;

import dev.branches.entity.Priority;
import dev.branches.entity.Task;
import dev.branches.entity.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record TaskPostResponse(
        @Schema(example = "uuid.task-1", description = "id da task")
        String id,
        @Schema(example = "Limpar a casa", description = "título da tarefa")
        String title,
        @Schema(example = "Varrer sala e cozinha", description = "descrição da tarefa")
        String description,
        @Schema(example = "2025-08-15", description = "data de vencimento")
        LocalDate dueDate,
        @Schema(example = "PENDENTE", description = "status da tarefa")
        TaskStatus status,
        @Schema(example = "ALTA", description = "prioridade da tarefa")
        Priority priority
) {
    public static TaskPostResponse by(Task task) {
        return new TaskPostResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getStatus(),
                task.getPriority()
        );
    }
}
