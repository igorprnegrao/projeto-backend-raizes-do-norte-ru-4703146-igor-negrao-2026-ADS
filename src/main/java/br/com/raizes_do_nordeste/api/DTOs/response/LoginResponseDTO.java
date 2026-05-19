package br.com.raizes_do_nordeste.api.DTOs.response;

public record LoginResponseDTO(
        String token,
        String tipo
) {
}

