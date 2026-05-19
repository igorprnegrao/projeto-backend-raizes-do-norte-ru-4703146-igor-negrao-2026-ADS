package br.com.raizes_do_nordeste.api.DTOs.response;

import java.util.UUID;

public record ProdutoResponseDTO(
        UUID id,
        String nome,
        String descricao,
        String precoUnitario,
        String categoriaComida,
        String periodoDia,
        EstoqueResponseDTO estoque
) {
}
