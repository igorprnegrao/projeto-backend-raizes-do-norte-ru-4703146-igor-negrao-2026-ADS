package br.com.raizes_do_nordeste.api.DTOs.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record MetaMensalUnidadeResponseDTO(
        UUID idUnidade,
        LocalDate inicioPeriodo,
        LocalDate fimPeriodo,
        BigDecimal metaMensal,
        BigDecimal valorArrecadado,
        BigDecimal percentualAtingido,
        boolean bateuMeta
) {
}

