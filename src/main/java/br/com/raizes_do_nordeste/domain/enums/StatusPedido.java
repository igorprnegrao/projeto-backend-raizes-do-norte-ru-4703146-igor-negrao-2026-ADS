package br.com.raizes_do_nordeste.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StatusPedido {

    EM_PREPARO("Pedido em preparo"),
    PRONTO("Pedido pronto para entrega"),
    ENTREGUE("Pedido entregue"),
    CANCELADO("Pedido cancelado");

    private final String descricao;
}
