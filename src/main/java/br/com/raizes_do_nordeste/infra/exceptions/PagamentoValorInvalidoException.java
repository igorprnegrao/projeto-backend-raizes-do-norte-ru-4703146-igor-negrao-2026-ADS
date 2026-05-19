package br.com.raizes_do_nordeste.infra.exceptions;

import java.math.BigDecimal;

public class PagamentoValorInvalidoException extends RuntimeException {
    public PagamentoValorInvalidoException(Long idPedido, BigDecimal valorPago, BigDecimal valorTotalPedido) {
        super("O valor pago do pedido '" + idPedido + "' deve ser exatamente '" + valorTotalPedido +
                "', mas foi informado '" + valorPago + "'.");
    }
}

