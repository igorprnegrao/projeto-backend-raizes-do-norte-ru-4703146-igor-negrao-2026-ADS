package br.com.raizes_do_nordeste.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MetodoPagamento {

    CARTAO_CREDITO("Cartão de crédito"),
    CARTAO_DEBITO("Cartão de débito"),
    DINHEIRO("Dinheiro"),
    PIX("PIX"),
    VALE_ALIMENTACAO("Vale alimentação");

    private final String descricao;
}
