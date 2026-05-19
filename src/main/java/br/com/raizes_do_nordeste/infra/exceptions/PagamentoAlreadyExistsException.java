package br.com.raizes_do_nordeste.infra.exceptions;

public class PagamentoAlreadyExistsException extends RuntimeException {
    public PagamentoAlreadyExistsException(Long idPedido) {
        super("Já existe pagamento para o pedido '" + idPedido + "'.");
    }
}

