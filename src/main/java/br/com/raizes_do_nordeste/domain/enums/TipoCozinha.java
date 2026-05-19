package br.com.raizes_do_nordeste.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TipoCozinha {

    COMPLETA("Cozinha completa"),
    REDUZIDA("Cozinha redudida");

    private final String descricao;
}
