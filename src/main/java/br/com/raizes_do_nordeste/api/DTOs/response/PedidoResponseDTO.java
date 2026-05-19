package br.com.raizes_do_nordeste.api.DTOs.response;

import br.com.raizes_do_nordeste.domain.enums.CanalPedido;
import br.com.raizes_do_nordeste.domain.enums.StatusPedido;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PedidoResponseDTO(
        Long id,

        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime registroData,

        CanalPedido canalPedido,

        StatusPedido statusPedido,

        BigDecimal valorTotal,

        UUID idCliente,

        UUID idUnidade,

        UUID idAtendente,

        List<PedidoItemResponseDTO> itens,

        UUID idPagamento
) {
}
