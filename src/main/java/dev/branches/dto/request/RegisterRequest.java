package dev.branches.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.With;

@With
public record RegisterRequest(
        @NotBlank(message = "O campo 'name' é obrigatório")
        @Schema(example = "Marcus Branches", description = "nome do usuário a ser registrado")
        String name,
        @NotBlank(message = "O campo 'email' é obrigatório")
        @Email(message = "Email inválido")
        @Schema(example = "marcusbranches@dev.com", description = "email do usuário a ser registrado")
        String email,
        @NotBlank(message = "O campo 'password' é obrigatório")
        @Schema(example = "mbranches123", description = "senha de acesso do usuário a ser registrado")
        String password
) {}
