package br.com.raizes_do_nordeste.infra.exceptions;

public class ProdutoNotFoundException extends RuntimeException {
    public ProdutoNotFoundException(String idProduto) {
        super("O produto com id '" + idProduto + "' não foi encontrado. Cadastre o produto antes de cadastrar o estoque.");
    }
}

