package br.com.raizes_do_nordeste.api.DTOs.response;

import java.util.List;
import java.util.UUID;

public record ClienteExtratoPontosResponseDTO(
        UUID idCliente,
        Integer saldoAtual,
        long totalLancamentos,
        int pagina,
        int tamanho,
        List<ClienteExtratoPontosLancamentoResponseDTO> lancamentos
) {
}

