package br.com.raizes_do_nordeste.api.DTOs.update;

import br.com.raizes_do_nordeste.domain.enums.Estado;

public record EnderecoUpdateDTO(
        String logradouro,
        String numero,
        String bairro,
        String cidade,
        Estado estado
) {
}

