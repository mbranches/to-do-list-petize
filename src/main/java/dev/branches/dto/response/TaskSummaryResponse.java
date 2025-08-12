package dev.branches.dto.response;

import dev.branches.entity.Priority;
import dev.branches.entity.Task;
import dev.branches.entity.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record TaskSummaryResponse(
        @Schema(example = "uuid.task-2", description = "id da task")
        String id,
        @Schema(example = "Documentar teste técnico PETIZE", description = "título da tarefa")
        String title,
        @Schema(example = "Documentar o teste técnico para vaga de estágio em backend da Petize", description = "descrição da tarefa")
        String description,
        @Schema(example = "2025-08-15", description = "data de vencimento")
        LocalDate dueDate,
        @Schema(example = "CONCLUIDA", description = "status da tarefa")
        TaskStatus status,
        @Schema(example = "ALTA", description = "prioridade da tarefa")
        Priority priority
) {
    public static TaskSummaryResponse by(Task task) {
        return new TaskSummaryResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getStatus(),
                task.getPriority()
        );
    }
}
