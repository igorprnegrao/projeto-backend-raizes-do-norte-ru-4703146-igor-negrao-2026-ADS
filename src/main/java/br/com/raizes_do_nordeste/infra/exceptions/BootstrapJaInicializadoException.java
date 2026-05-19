package br.com.raizes_do_nordeste.infra.exceptions;

public class BootstrapJaInicializadoException extends RuntimeException {
    public BootstrapJaInicializadoException() {
        super("Bootstrap já concluído: o primeiro gerente já foi criado.");
    }
}

