package br.com.raizes_do_nordeste.api.DTOs.update;

import br.com.raizes_do_nordeste.domain.enums.TipoCozinha;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UnidadeUpdateDTO(
        @Size(min = 3, max = 100, message = "O nome da loja deve ter entre 3 e 100 caracteres")
        String nomeLoja,

        @Email(message = "O e-mail deve ser válido")
        String email,

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
        String foneSecundarioCelular,

        Integer metaDesempenhoVenda,

        TipoCozinha tipoCozinha,

        @Valid
        EnderecoUpdateDTO endereco
) {
}

