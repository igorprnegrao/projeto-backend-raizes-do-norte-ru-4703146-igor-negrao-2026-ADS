package br.com.raizes_do_nordeste.api.DTOs.request;

import br.com.raizes_do_nordeste.domain.enums.MetodoPagamento;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PagamentoRequestDTO(
        @NotNull(message = "O pedido é obrigatório")
        Long idPedido,

        @NotNull(message = "O valor pago é obrigatório")
        @DecimalMin(value = "0.01", message = "O valor pago deve ser maior que zero")
        BigDecimal valorPago,

        @NotNull(message = "O método de pagamento é obrigatório")
        MetodoPagamento metodoPagamento,

        // Se null, o serviço define mock automaticamente.
        Boolean aprovar
) {
}
