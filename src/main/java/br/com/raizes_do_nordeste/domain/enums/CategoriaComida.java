package br.com.raizes_do_nordeste.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoriaComida {

    SOBREMESA("Sobremesa"),
    SALGADOS("Salgados"),
    PIZZAS("Pizzas"),
    MASSAS("Massas"),
    BEBIDAS("Bebidas"),
    SANDUICHE("Sanduiche"),
    TAPIOCAS("Tapocas"),
    DRINKS("drinks alcoolícos");

    private final String descricao;

}
