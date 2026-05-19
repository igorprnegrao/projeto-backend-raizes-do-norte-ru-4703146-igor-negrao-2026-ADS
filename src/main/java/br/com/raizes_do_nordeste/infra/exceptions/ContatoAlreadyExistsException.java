package br.com.raizes_do_nordeste.infra.exceptions;

public class ContatoAlreadyExistsException extends RuntimeException {
    public ContatoAlreadyExistsException(String message) {
        super("O contato '" + message + "' já está em uso.");
    }
}
