package dev.branches.controller;

import dev.branches.dto.ParentTaskByAddSubtask;
import dev.branches.dto.StatusPatchRequest;
import dev.branches.dto.request.TaskPostRequest;
import dev.branches.dto.request.TaskPutRequest;
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
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@Tag(name = "Tarefas", description = "Operações com tarefas")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@RequestMapping("/api/v1/tasks")
@RestController
public class TaskController {
    private final TaskService service;

    @Operation(
            summary = "Criar tarefa",
            description = "Cria uma tarefa e vincula ao usuário solicitante, caso o status não seja passado o status predefinido é 'PENDENTE'",
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
        Task taskToCreate = Task.by(requestingUser, request);

        Optional<TaskStatus> status = request.status() == null ? Optional.empty() : Optional.of(TaskStatus.valueOf(request.status()));

        Task savedTask = service.create(taskToCreate, status);

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
                            description = "Número de elementos a listar por página (tem o valor padrão de 15)"
                    ),
                    @Parameter(
                            name = "sort",
                            description = "Critério de ordenação: 'campo,direção'. Ex: 'dueDate,desc'. Nesse caso irá ordenar pelo campo 'dueDate' em ordem decrescente",
                            example = "dueDate,desc"
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
    public ResponseEntity<PageResponse<TaskGetResponse>> listAll(@Parameter(hidden = true) @PageableDefault(size = 15) Pageable pageable,
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

    @Operation(
            summary = "Detalhar tarefa por id",
            description = "Retorna a tarefa encontrada via id",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "id da tarefa para detalhar"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Tarefa retornada com sucesso"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "O usuário da requisição não está autenticado",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "id da tarefa não encontrado",
                            content = @Content(schema = @Schema(implementation = DefaultErrorMessage.class))
                    ),
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<TaskGetResponse> findById(@AuthenticationPrincipal User requestingUser, @PathVariable String id) {
        Task task = service.findByIdAndUserOrThrowsNotFoundException(id, requestingUser);

        TaskGetResponse response = TaskGetResponse.by(task);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Adicionar subtarefa",
            description = "Adiciona uma subtarefa a outra já criada e retorna a tarefa pai com suas subtarefas, caso status não seja passado, o status predefinido é 'PENDENTE'",
            parameters = {
                    @Parameter(
                            name = "parentTaskId",
                            description = "id da tarefa pai para adicionar subtarefa"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "subtarefa adicionada com sucesso"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "campo obrigatório não enviado no corpo da requisição ou a tarefa pai possui status CONCLUIDA e a filha possui um status diferente",
                            content = @Content(schema = @Schema(implementation = DefaultErrorMessage.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "usuário solicitante não autenticado",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "id da tarefa pai não encontrado",
                            content = @Content(schema = @Schema(implementation = DefaultErrorMessage.class))
                    )
            }
    )
    @PostMapping("/{parentTaskId}/subtasks")
    public ResponseEntity<ParentTaskByAddSubtask> addSubtask(@AuthenticationPrincipal User requestingUser, @PathVariable String parentTaskId, @RequestBody @Valid TaskPostRequest request) {
        Task subtaskToCreate = Task.by(requestingUser, request);

        Optional<TaskStatus> status = request.status() == null ? Optional.empty() : Optional.of(TaskStatus.valueOf(request.status()));

        Task createdTask = service.addSubtask(
                requestingUser,
                parentTaskId,
                subtaskToCreate,
                status
        );

        ParentTaskByAddSubtask response = ParentTaskByAddSubtask.by(createdTask);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Atualizar uma tarefa",
            description = "Atualiza uma tarefa do usuário solicitante, caso status não seja passado, o status predefinido é 'PENDENTE'",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "id da tarefa a ser atualizada"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Tarefa atualizada com sucesso",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "campo obrigatório não enviado no corpo da requisição, o id da url é diferente do id passado no corpo da requisição ou o status passado é CONCLUIDA, porém a tarefa possui subtasks PENDENTE ou EM_ANDAMENTO",
                            content = @Content(schema = @Schema(implementation = DefaultErrorMessage.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "usuário solicitante não autenticado",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "id da tarefa não encontrado",
                            content = @Content(schema = @Schema(implementation = DefaultErrorMessage.class))
                    )
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@AuthenticationPrincipal User requestingUser,
                                       @PathVariable String id,
                                       @Valid @RequestBody TaskPutRequest request) {
        Task taskToUpdate = Task.by(requestingUser, request);

        Optional<TaskStatus> status = request.status() == null ? Optional.empty() : Optional.of(TaskStatus.valueOf(request.status()));

        service.update(
                requestingUser,
                id,
                taskToUpdate,
                status
        );

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Atualizar status",
            description = "Atualiza status de tarefa",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "id da tarefa para atualizar status"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Status da tarefa atualizada com sucesso",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "campo 'status' não enviado no corpo da requisição ou o status passado é 'CONCLUIDA', porém a tarefa possui subtasks com status 'PENDENTE' ou 'EM_ANDAMENTO'",
                            content = @Content(schema = @Schema(implementation = DefaultErrorMessage.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "usuário solicitante não autenticado",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "id da tarefa não encontrado",
                            content = @Content(schema = @Schema(implementation = DefaultErrorMessage.class))
                    )
            }
    )
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateTaskStatus(@AuthenticationPrincipal User requestingUser, @PathVariable String id, @RequestBody @Valid StatusPatchRequest request) {
        service.updateStatus(requestingUser, id, request.status());

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Deletar tarefa",
            description = "Deleta tarefa do usuário da requisição. Caso a tarefa possua subtarefas, essas também serão deletadas",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "id da tarefa a ser deletada"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Tarefa removida com sucesso"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Id da tarefa não encontrada",
                            content = @Content(schema = @Schema(implementation = DefaultErrorMessage.class))
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@AuthenticationPrincipal User requestingUser, @PathVariable String id) {
        service.deleteById(requestingUser, id);

        return ResponseEntity.noContent().build();
    }
}

