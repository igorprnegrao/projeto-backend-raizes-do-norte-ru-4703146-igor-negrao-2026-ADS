package br.com.raizes_do_nordeste.infra.exceptions;

import java.util.UUID;

public class EstoqueInsuficienteException extends RuntimeException {
    public EstoqueInsuficienteException(UUID produtoId, UUID unidadeId, Integer solicitado, Integer disponivel) {
        super("Estoque insuficiente para o produto '" + produtoId + "' na unidade '" + unidadeId + "'. " +
                "Solicitado: " + solicitado + ", disponível: " + disponivel + ".");
    }
}

