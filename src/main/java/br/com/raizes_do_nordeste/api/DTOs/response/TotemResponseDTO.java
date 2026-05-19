package br.com.raizes_do_nordeste.api.DTOs.response;

import br.com.raizes_do_nordeste.domain.enums.StatusMaquina;

import java.util.UUID;

public record TotemResponseDTO(
        UUID id,
        String codigoIdentificador,
        StatusMaquina statusMaquina,
        UUID idUnidade
) {
}

