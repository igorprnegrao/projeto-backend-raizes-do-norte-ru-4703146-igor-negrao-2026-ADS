package br.com.raizes_do_nordeste.infra.exceptions;

public class ClienteNotFoundException extends RuntimeException {
    public ClienteNotFoundException(String idCliente) {
        super("Cliente não encontrado com o id: " + idCliente);
    }
}

