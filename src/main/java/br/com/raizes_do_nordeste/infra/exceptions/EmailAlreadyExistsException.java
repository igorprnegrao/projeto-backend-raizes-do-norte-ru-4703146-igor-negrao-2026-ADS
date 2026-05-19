package br.com.raizes_do_nordeste.infra.exceptions;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("O email '" + email + "' já está em uso.");
    }
}
