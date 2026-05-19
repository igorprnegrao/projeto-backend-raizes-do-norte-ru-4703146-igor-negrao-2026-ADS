package br.com.raizes_do_nordeste.api.DTOs.response;

import java.util.UUID;

public record ClientePontosResponseDTO(
        UUID idCliente,
        Integer saldoPontos
) {
}

