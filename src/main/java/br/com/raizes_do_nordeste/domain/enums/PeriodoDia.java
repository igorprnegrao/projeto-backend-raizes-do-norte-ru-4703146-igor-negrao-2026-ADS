package br.com.raizes_do_nordeste.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PeriodoDia {

    ALMOCO("Cardapio do Almoço"),
    JANTAR("Cardapio do Jantar");

    private final String descricao;
}
