package br.com.raizes_do_nordeste.api.DTOs.request;

import br.com.raizes_do_nordeste.domain.enums.MetodoPagamento;
import br.com.raizes_do_nordeste.domain.enums.StatusPagamento;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PedidoPagamentoRequestDTO(
        @NotNull(message = "O método de pagamento é obrigatório")
        MetodoPagamento metodoPagamento,
        StatusPagamento statusPagamento,
        @NotNull(message = "O valor pago é obrigatório")
        @DecimalMin(value = "0.01", message = "O valor pago deve ser maior que zero")
        BigDecimal valorPago
) {
}

