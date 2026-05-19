package br.com.raizes_do_nordeste.infra.exceptions;

public class JwtTokenInvalidoException extends RuntimeException {
    public JwtTokenInvalidoException(String message) {
        super(message);
    }
}

