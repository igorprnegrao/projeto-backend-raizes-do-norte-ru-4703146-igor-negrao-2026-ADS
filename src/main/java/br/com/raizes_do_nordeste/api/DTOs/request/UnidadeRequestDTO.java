package br.com.raizes_do_nordeste.api.DTOs.request;

import br.com.raizes_do_nordeste.domain.enums.TipoCozinha;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

public record UnidadeRequestDTO(

        @NotBlank(message = "O nome da loja não pode estar em branco")
        @Size(min = 3, max = 100, message = "O nome da loja deve ter entre 3 e 100 caracteres")
        String nomeLoja,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "O e-mail deve ser válido")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
        String senha,


        @Pattern(
                regexp = "^55\\s?\\(?\\d{2}\\)?\\s?\\d{4}-?\\d{4}$",
                message = "Telefone fixo inválido. O prefixo 55 é obrigatório. Exemplo: 55 (91) 3247-2455"
        )
        String fonePrincipalFixo,


        @Pattern(
                regexp = "^55\\s?\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$",
                message = "Telefone inválido. O prefixo 55 é obrigatório. Exemplo: 55 (91) 98888-7777"
        )
        @NotBlank(message = "O número do telefone é obrigatório")
        String foneSecundarioCelular,


        Integer metaDesempenhoVenda,

        @NotNull(message = "O tipo de cozinha é obrigatório")
        TipoCozinha tipoCozinha,

        @NotNull(message = "O endereço é obrigatório")
        @Valid
        EnderecoRequestDTO endereco
) {
}
