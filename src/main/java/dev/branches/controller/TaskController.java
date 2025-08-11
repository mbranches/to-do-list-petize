package dev.branches.controller;

import dev.branches.dto.request.TaskPostRequest;
import dev.branches.dto.response.TaskPostResponse;
import dev.branches.entity.Task;
import dev.branches.entity.User;
import dev.branches.exception.DefaultErrorMessage;
import dev.branches.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
