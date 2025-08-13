package dev.branches.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.branches.entity.Priority;
import dev.branches.entity.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Optional;

public record TaskPostRequest(
        @NotBlank(message = "O campo 'title' é obrigatório")
        @Schema(example = "Realizar teste técnico", description = "título da tarefa")
        String title,
        @NotBlank(message = "O campo 'description' é obrigatório")
        @Schema(example = "Realizar teste técnico para vaga de estágio em backend da Petize", description = "descrição da tarefa")
        String description,
        @Schema(example = "2025-08-15", description = "data de vencimento, formato yyyy-MM-dd")
        @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$", message = "O formato de 'dueDate' deve ser yyyy-MM-dd")
        @NotNull(message = "O campo 'dueDate' é obrigatório")
        String dueDate,
        @Pattern(regexp = "^(?i)(PENDENTE|EM_ANDAMENTO|CONCLUIDA)$", message = "Status inválido. Valores aceitos: PENDENTE, EM_ANDAMENTO, CONCLUIDA.")
        @Schema(example = "PENDENTE", description = "status da tarefa, caso não seja enviado, o status PENDENTE será definido por padrão")
        String status,
        @Pattern(regexp = "^(?i)(ALTA|REGULAR|BAIXA)$", message = "Prioridade inválida. Valores aceitos: ALTA, REGULAR, BAIXA.")
        @NotNull(message = "O campo 'priority' é obrigatório")
        @Schema(example = "ALTA", description = "prioridade da tarefa")
        String priority
) {}
