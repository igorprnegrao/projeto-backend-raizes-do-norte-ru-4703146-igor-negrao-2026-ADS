package br.com.raizes_do_nordeste.api.DTOs.update;

public record ClienteUpdateDTO(

        String nomeCompleto,
        String email,
        String senha,
        String fonePrincipal
) {
}
