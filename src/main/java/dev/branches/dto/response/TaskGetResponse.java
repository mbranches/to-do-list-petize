package dev.branches.dto.response;

import dev.branches.entity.Priority;
import dev.branches.entity.Task;
import dev.branches.entity.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

public record TaskGetResponse(
        @Schema(example = "uuid.task-1", description = "id da task")
        String id,
        @Schema(example = "Realizar teste técnico", description = "título da tarefa")
        String title,
        @Schema(example = "Realizar teste técnico para vaga de estágio em backend da Petize", description = "descrição da tarefa")
        String description,
        @Schema(example = "2025-08-15", description = "data de vencimento")
        LocalDate dueDate,
        @Schema(example = "EM_ANDAMENTO", description = "status da tarefa")
        TaskStatus status,
        @Schema(example = "ALTA", description = "prioridade da tarefa")
        Priority priority,
        @Schema(example = "uuid.task-pai-exemplo", description = "ID da tarefa pai. Será nulo se for uma tarefa principal.", nullable = true)
        String parentId,
        @Schema(description = "Lista de subtarefas associadas a esta tarefa.", nullable = true)
        List<TaskSummaryResponse> subtasks
) {
        public static TaskGetResponse by(Task task) {
                String parentId = task.getParent() != null ? task.getParent().getId() : null;
                List<TaskSummaryResponse> subtasks = task.getSubtasks().stream().map(TaskSummaryResponse::by).toList();

                return new TaskGetResponse(
                        task.getId(),
                        task.getTitle(),
                        task.getDescription(),
                        task.getDueDate(),
                        task.getStatus(),
                        task.getPriority(),
                        parentId,
                        subtasks
                );
        }
}
