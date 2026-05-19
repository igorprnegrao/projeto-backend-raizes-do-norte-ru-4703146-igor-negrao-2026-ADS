package br.com.raizes_do_nordeste.infra.exceptions;

public class TotemNotFoundException extends RuntimeException {
    public TotemNotFoundException(String idTotem) {
        super("Totem não encontrado com o id: " + idTotem);
    }
}

