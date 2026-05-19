package br.com.raizes_do_nordeste.api.DTOs.response;

import java.math.BigDecimal;
import java.util.UUID;

public record PedidoItemResponseDTO(
        UUID idProduto,
        Integer quantidade,
        BigDecimal precoMomento
) {
}

