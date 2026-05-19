package br.com.raizes_do_nordeste.api.DTOs.request;

import br.com.raizes_do_nordeste.domain.enums.Estado;

public record EnderecoRequestDTO(
        String logradouro,
        String numero,
        String bairro,
        String cidade,
        Estado estado
) {
}

