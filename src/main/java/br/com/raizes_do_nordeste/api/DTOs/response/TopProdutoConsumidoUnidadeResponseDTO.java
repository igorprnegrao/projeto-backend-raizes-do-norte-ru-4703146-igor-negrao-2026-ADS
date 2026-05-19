package br.com.raizes_do_nordeste.api.DTOs.response;

import br.com.raizes_do_nordeste.domain.enums.CategoriaComida;

import java.util.UUID;

public record TopProdutoConsumidoUnidadeResponseDTO(
        UUID idProduto,
        String nomeProduto,
        CategoriaComida categoriaComida,
        Long quantidadeConsumida
) {
}

