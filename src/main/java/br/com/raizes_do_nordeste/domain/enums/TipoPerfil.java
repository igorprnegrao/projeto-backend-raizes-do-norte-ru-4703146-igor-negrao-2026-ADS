package br.com.raizes_do_nordeste.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TipoPerfil {

    ATENDENTE("Atendente da Unidade"),
    COZINHEIRO("Cozinheiro da Unidade"),
    GERENTE("Gerente da Unidade");

    private final String descricao;
}
