package dev.branches.controller;

import dev.branches.dto.request.TaskPostRequest;
import dev.branches.dto.response.PageResponse;
import dev.branches.dto.response.TaskGetResponse;
import dev.branches.dto.response.TaskPostResponse;
import dev.branches.entity.Priority;
import dev.branches.entity.Task;
import dev.branches.entity.TaskStatus;
import dev.branches.entity.User;
import dev.branches.exception.DefaultErrorMessage;
import dev.branches.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
    import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Tarefas", description = "Operações com tarefas")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@RequestMapping("/api/v1/tasks")
@RestController
public class TaskController {
    private final TaskService service;

    @Operation(
            summary = "Criar tarefa",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Tarefa criada com sucesso",
                            content = @Content(schema = @Schema(implementation = TaskPostResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "algum campo obrigatório não enviado no corpo da requisição",
                            content = @Content(schema = @Schema(implementation = DefaultErrorMessage.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "usuário solicitante não está autenticado",
                            content = @Content
                    )
            }
    )
    @PostMapping
    public ResponseEntity<TaskPostResponse> create(@AuthenticationPrincipal User requestingUser, @RequestBody @Valid TaskPostRequest request) {
        Task taskToCreate = Task.builder()
                .user(requestingUser)
                .title(request.title())
                .description(request.description())
                .dueDate(request.dueDate())
                .priority(request.priority())
                .build();

        Task savedTask = service.create(taskToCreate, request.status());

        TaskPostResponse response = TaskPostResponse.by(savedTask);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Listar tarefas com paginação",
            description = "Retorna uma lista paginada de tarefas pertencentes ao usuário autenticado, com filtros opcionais",
            parameters = {
                    @Parameter(
                            name = "page",
                            description = "Número da página que deseja obter (inicia e tem o valor padrão de 0)"
                    ),
                    @Parameter(
                            name = "size",
                            description = "Número de elementos a listar por página (tem o valor padrão de 10)"
                    ),
                    @Parameter(
                            name = "status",
                            description = "Filtra tarefas por um status específico (PENDENTE, EM_ANDAMENTO, CONCLUIDA)"
                    ),
                    @Parameter(
                            name = "priority",
                            description = "Filtra tarefas por uma prioridade específica (ALTA, REGULAR, BAIXA)"
                    ),
                    @Parameter(
                            name = "dueDateFrom",
                            description = "Filtra tarefas com data de vencimento a partir desta data (formato YYYY-MM-DD)"
                    ),
                    @Parameter(
                            name = "dueDateTo",
                            description = "Filtra tarefas com data de vencimento até esta data (formato YYYY-MM-DD)"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Listagem de tarefas realizada com sucesso"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "O usuário da requisição não está autenticado"
                    )
            }
    )
    @GetMapping
    public ResponseEntity<PageResponse<TaskGetResponse>> listAll(@Parameter(hidden = true) Pageable pageable,
                                                                 @AuthenticationPrincipal User requestingUser,
                                                                 @RequestParam(required = false) TaskStatus status,
                                                                 @RequestParam(required = false) Priority priority,
                                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDateFrom,
                                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDateTo) {
        Page<Task> tasks = service.listAll(pageable, requestingUser, status, priority, dueDateFrom, dueDateTo);

        Page<TaskGetResponse> taskGetResponseList = tasks
                .map(TaskGetResponse::by);

        PageResponse<TaskGetResponse> response = PageResponse.by(taskGetResponseList);

        return ResponseEntity.ok(response);
    }
}
