package br.com.raizes_do_nordeste.api.DTOs.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ClienteResponseDTO(
        UUID id,
        String nomeCompleto,
        String email,
        String fonePrincipal,
        String foneSecundario,

        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate dataNascimento,

        Integer saldoPontos,

        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime dataRegistro,

        Boolean consentimentoLGPD,

        UUID idAtendente
) {
}
