package dev.branches.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "O campo 'email' é obrigatório")
        @Email(message = "Email inválido")
        @Schema(example = "marcusbranches@dev.com", description = "email de acesso já cadastrado")
        String email,
        @NotBlank(message = "O campo 'password' é obrigatório")
        @Schema(example = "mbranches123", description = "senha de acesso já cadastrada")
        String password
) {}
