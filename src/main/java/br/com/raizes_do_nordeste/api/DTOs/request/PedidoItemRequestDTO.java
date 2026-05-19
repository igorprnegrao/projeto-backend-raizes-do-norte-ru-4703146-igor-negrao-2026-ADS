package br.com.raizes_do_nordeste.api.DTOs.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PedidoItemRequestDTO(
        @NotNull(message = "O produto é obrigatório")
        UUID idProduto,

        @NotNull(message = "A quantidade é obrigatória")
        @Min(value = 1, message = "A quantidade deve ser maior que zero")
        Integer quantidade
) {
}

