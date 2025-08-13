package dev.branches.controller;

import dev.branches.dto.request.LoginRequest;
import dev.branches.dto.response.LoginResponse;
import dev.branches.dto.request.RegisterRequest;
import dev.branches.entity.User;
import dev.branches.exception.DefaultErrorMessage;
import dev.branches.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Autenticação", description = "Rotas de autenticação")
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {
    private final UserService service;

    @Operation(
        summary = "Cadastro",
        description = "Cadastra usuário",
        responses = {
                @ApiResponse(
                        responseCode = "201",
                        description = "usuário cadastrado com sucesso",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "email já está cadastrado ou algum campo obrigatório não enviado no corpo da requisição",
                        content = @Content(
                                schema = @Schema(implementation = DefaultErrorMessage.class)
                        )
                )
        }
    )
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest request) {
        User userToRegister = new User();
        userToRegister.setName(request.name());
        userToRegister.setEmail(request.email());

        service.create(userToRegister, request.password());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "Login",
            description = "Autentica usuário no sistema através do email e senha",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "usuário logado com sucesso",
                            content = @Content(
                                    schema = @Schema(implementation = LoginResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "campo obrigatório não enviado no corpo da requisição",
                            content = @Content(
                                    schema = @Schema(implementation = DefaultErrorMessage.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "email ou senha inválidos",
                            content = @Content(
                                    schema = @Schema(implementation = DefaultErrorMessage.class)
                            )
                    )
            }
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        String accessToken = service.login(request.email(), request.password());

        LoginResponse response = new LoginResponse(accessToken);

        return ResponseEntity.ok(response);
    }
}
