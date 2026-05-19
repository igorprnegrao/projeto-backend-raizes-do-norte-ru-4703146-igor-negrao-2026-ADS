package br.com.raizes_do_nordeste.api.DTOs.request;

import br.com.raizes_do_nordeste.domain.enums.TipoPerfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record EquipeRequestDTO(

        @NotBlank(message = "O nome não pode estar em branco")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        String nomeCompleto,

        @Pattern(
                regexp = "^55\\s?\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$",
                message = "Telefone inválido. O prefixo 55 é obrigatório. Exemplo: 55 (91) 98888-7777"
        )
        @NotBlank(message = "O número do telefone é obrigatório")
        String fonePrincipal,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "O e-mail deve ser válido")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
        String senha,

        @NotNull(message = "O tipo de perfil é obrigatório")
        TipoPerfil tipoPerfil,

        // Opcional: um GERENTE pode ser criado antes de existir uma unidade (bootstrap)
        UUID idUnidade
) {
}
