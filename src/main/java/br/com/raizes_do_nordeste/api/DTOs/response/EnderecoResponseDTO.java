package br.com.raizes_do_nordeste.api.DTOs.response;

import br.com.raizes_do_nordeste.domain.enums.Estado;

import java.util.UUID;

public record EnderecoResponseDTO(
        UUID id,
        String logradouro,
        String numero,
        String bairro,
        String cidade,
        Estado estado
) {
}

