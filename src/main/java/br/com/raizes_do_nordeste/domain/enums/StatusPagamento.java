package br.com.raizes_do_nordeste.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StatusPagamento {

    PENDENTE("Pagamento pendente"),
    APROVADO("Pagamento aprovado"),
    RECUSADO("Pagamento recusado"),
    CANCELADO("Pagamento cancelado");

    private final String descricao;
}
