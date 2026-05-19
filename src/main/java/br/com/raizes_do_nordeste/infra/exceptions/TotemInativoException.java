package br.com.raizes_do_nordeste.infra.exceptions;

public class TotemInativoException extends RuntimeException {
    public TotemInativoException(String idTotem) {
        super("O totem com id '" + idTotem + "' não está ativo para realizar pedidos.");
    }
}

