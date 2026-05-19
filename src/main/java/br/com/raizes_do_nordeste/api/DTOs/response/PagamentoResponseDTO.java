package br.com.raizes_do_nordeste.api.DTOs.response;

import br.com.raizes_do_nordeste.domain.enums.MetodoPagamento;
import br.com.raizes_do_nordeste.domain.enums.StatusPagamento;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PagamentoResponseDTO(


        UUID id,

        Long idPedido,

        BigDecimal valorPago,

        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime dataPagamento,

        MetodoPagamento metodoPagamento,

        StatusPagamento statusPagamento
) {
}
