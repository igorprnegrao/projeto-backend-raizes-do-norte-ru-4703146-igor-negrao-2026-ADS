package br.com.raizes_do_nordeste.infra.exceptions;

public class PedidoNotFoundException extends RuntimeException {
    public PedidoNotFoundException(Long idPedido) {
        super("Pedido não encontrado com o id: " + idPedido);
    }
}

