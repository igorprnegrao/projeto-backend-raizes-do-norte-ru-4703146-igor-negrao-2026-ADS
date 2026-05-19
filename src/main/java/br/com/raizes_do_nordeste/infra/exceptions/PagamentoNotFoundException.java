package br.com.raizes_do_nordeste.infra.exceptions;

public class PagamentoNotFoundException extends RuntimeException {
    public PagamentoNotFoundException(Long idPedido) {
        super("Pagamento nao encontrado para o pedido '" + idPedido + "'.");
    }
}

