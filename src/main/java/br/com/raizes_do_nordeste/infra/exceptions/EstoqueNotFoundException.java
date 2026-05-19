package br.com.raizes_do_nordeste.infra.exceptions;

import java.util.UUID;

public class EstoqueNotFoundException extends RuntimeException {
    public EstoqueNotFoundException(UUID produtoId, UUID unidadeId) {
        super("Estoque não encontrado para o produto '" + produtoId + "' na unidade '" + unidadeId + "'.");
    }
}

