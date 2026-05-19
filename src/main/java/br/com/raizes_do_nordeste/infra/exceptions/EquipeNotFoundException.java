package br.com.raizes_do_nordeste.infra.exceptions;

public class EquipeNotFoundException extends RuntimeException {
    public EquipeNotFoundException(String idEquipe) {
        super("Equipe não encontrada com o id: " + idEquipe);
    }
}

