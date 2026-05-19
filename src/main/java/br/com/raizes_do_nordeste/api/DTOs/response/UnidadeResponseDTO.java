package br.com.raizes_do_nordeste.api.DTOs.response;

import br.com.raizes_do_nordeste.domain.enums.TipoCozinha;

import java.util.UUID;

public record UnidadeResponseDTO(
        UUID id,
        String nomeLoja,
        Integer metaDesempenhoVenda,
        TipoCozinha tipoCozinha,
        EnderecoResponseDTO endereco
) {
}
