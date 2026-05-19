package br.com.raizes_do_nordeste.infra.exceptions;

public class UnidadeNotFoundException extends RuntimeException {
    public UnidadeNotFoundException(String idUnidade) {
        super("A unidade com id '" + idUnidade + "' não foi encontrada. Cadastre a unidade antes de cadastrar a equipe.");
    }
}

