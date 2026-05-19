package br.com.raizes_do_nordeste.api.DTOs.response;

import java.util.UUID;

public record EquipeResponseDTO(
        UUID id,
        String nomeCompleto,
        String email,
        UUID unidadeId
) {
}
