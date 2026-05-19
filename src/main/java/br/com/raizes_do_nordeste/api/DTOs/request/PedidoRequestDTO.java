package br.com.raizes_do_nordeste.api.DTOs.request;

import br.com.raizes_do_nordeste.domain.enums.CanalPedido;
import br.com.raizes_do_nordeste.domain.enums.StatusPedido;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record PedidoRequestDTO(
        @NotNull(message = "O canal do pedido é obrigatório")
        CanalPedido canalPedido,

        StatusPedido statusPedido,

        BigDecimal valorTotal,

        UUID idCliente,

        String foneCliente,

        @NotNull(message = "A unidade é obrigatória")
        UUID idUnidade,

        UUID idAtendente,

        UUID idTotem,

        @Valid
        @NotEmpty(message = "O pedido deve conter pelo menos um item")
        List<PedidoItemRequestDTO> itens,

        @Valid
        @NotNull(message = "O pagamento é obrigatório")
        PedidoPagamentoRequestDTO pagamento
) {
}
