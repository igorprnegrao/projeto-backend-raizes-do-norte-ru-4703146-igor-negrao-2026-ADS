package br.com.raizes_do_nordeste.infra.exceptions;

import java.util.UUID;

public class TotemUnidadeInvalidaException extends RuntimeException {
    public TotemUnidadeInvalidaException(UUID idTotem, UUID idUnidadePedido) {
        super("O totem '" + idTotem + "' não pertence à unidade do pedido '" + idUnidadePedido + "'.");
    }
}

