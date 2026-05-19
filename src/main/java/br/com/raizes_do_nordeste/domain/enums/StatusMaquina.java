package br.com.raizes_do_nordeste.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StatusMaquina {

    ATIVO("ATIVO"),
    MANUTENCAO("MANUTENCAO"),
    DESATIVADO("DESATIVADO");

    private final String descricao;
}
