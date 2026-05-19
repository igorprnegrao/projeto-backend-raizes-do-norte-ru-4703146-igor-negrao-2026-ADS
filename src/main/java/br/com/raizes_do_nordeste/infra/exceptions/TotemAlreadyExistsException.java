package br.com.raizes_do_nordeste.infra.exceptions;

public class TotemAlreadyExistsException extends RuntimeException {

    public TotemAlreadyExistsException(String codigoIdentificador) {
        super("Já existe um totem com o código identificador: " + codigoIdentificador);
    }
}

