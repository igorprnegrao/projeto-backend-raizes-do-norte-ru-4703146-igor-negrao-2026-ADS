package br.com.raizes_do_nordeste.api.DTOs.request;

import br.com.raizes_do_nordeste.domain.enums.StatusMaquina;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TotemRequestDTO(
        @NotBlank(message = "O código identificador é obrigatório")
        String codigoIdentificador,

        String tokenAutenticacao,

        @NotNull(message = "O status da máquina é obrigatório")
        StatusMaquina statusMaquina,

        @NotNull(message = "A unidade é obrigatória")
        UUID idUnidade
) {
}

