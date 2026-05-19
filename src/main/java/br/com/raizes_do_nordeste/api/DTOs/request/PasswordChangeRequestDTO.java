package br.com.raizes_do_nordeste.api.DTOs.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordChangeRequestDTO(
        @NotBlank(message = "A senha atual e obrigatoria")
        String senhaAtual,

        @NotBlank(message = "A nova senha e obrigatoria")
        @Size(min = 6, message = "A nova senha deve ter no minimo 6 caracteres")
        String novaSenha
) {
}

