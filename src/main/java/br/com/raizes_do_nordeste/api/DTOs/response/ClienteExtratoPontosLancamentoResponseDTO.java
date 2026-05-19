package br.com.raizes_do_nordeste.api.DTOs.response;

import br.com.raizes_do_nordeste.domain.enums.CanalPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ClienteExtratoPontosLancamentoResponseDTO(
        Long idPedido,
        LocalDateTime dataPedido,
        CanalPedido canalPedido,
        BigDecimal valorPedido,
        Integer pontosGanhos
) {
}

