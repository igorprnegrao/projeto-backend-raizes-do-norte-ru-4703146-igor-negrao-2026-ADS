package br.com.raizes_do_nordeste.infra.exceptions;

import java.util.UUID;

public class EstoqueAlreadyExistsException extends RuntimeException {
    public EstoqueAlreadyExistsException(UUID produtoId, UUID unidadeId) {
        super("Já existe estoque para o produto '" + produtoId + "' na unidade '" + unidadeId + "'.");
    }
}

