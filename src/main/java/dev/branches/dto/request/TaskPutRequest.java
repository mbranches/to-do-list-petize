package dev.branches.dto.request;

import dev.branches.entity.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

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
        @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$", message = "O formato de 'dueDate' deve ser yyyy-MM-dd")
        @Schema(example = "2025-08-10", description = "data de vencimento")
        String dueDate,
        @Pattern(regexp = "^(?i)(PENDENTE|EM_ANDAMENTO|CONCLUIDA)$", message = "Status inválido. Valores aceitos: PENDENTE, EM_ANDAMENTO, CONCLUIDA.")
        @Schema(example = "PENDENTE", description = "status da tarefa, caso não seja enviado, o status PENDENTE será definido por padrão")
        String status,
        @Pattern(regexp = "^(?i)(ALTA|REGULAR|BAIXA)$", message = "Prioridade inválida. Valores aceitos: ALTA, REGULAR, BAIXA.")
        @NotNull(message = "O campo 'priority' é obrigatório")
        @Schema(example = "ALTA", description = "prioridade da tarefa")
        String priority
) {}
