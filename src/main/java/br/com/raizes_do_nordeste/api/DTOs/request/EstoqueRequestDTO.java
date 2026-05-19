package br.com.raizes_do_nordeste.api.DTOs.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record EstoqueRequestDTO(
        @NotNull(message = "A quantidade é obrigatória")
        Integer quantidade,

        @NotNull(message = "O produto é obrigatório")
        UUID idProduto,

        @NotNull(message = "A unidade é obrigatória")
        UUID idUnidade

) {
}
