package dev.branches.controller;

import dev.branches.dto.LoginRequest;
import dev.branches.dto.LoginResponse;
import dev.branches.dto.RegisterRequest;
import dev.branches.exception.DefaultErrorMessage;
import dev.branches.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {
    private final UserService service;

    @Operation(
        summary = "Cadastro",
        responses = {
                @ApiResponse(
                        responseCode = "201",
                        description = "usuário cadastrado com sucesso",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "email já está cadastrado",
                        content = @Content(
                                schema = @Schema(implementation = DefaultErrorMessage.class)
                        )
                )
        }
    )
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest request) {
        service.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
