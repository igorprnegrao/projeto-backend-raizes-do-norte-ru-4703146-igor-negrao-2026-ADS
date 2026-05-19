package br.com.raizes_do_nordeste.api.DTOs.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.UUID;

public record ClienteRequestDTO(

        @NotBlank(message = "O nome não pode estar em branco")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        String nomeCompleto,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "O e-mail deve ser válido")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
        String senha,

        @NotNull(message = "Data de nascimento da sua equipe é obrigatória")
        @Past(message = "A data de nascimento deve ser uma data no passado")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
        @Schema(type = "string", pattern = "dd-MM-yyyy", example = "25-12-1990")
        LocalDate dataNascimento,

        @Pattern(
                regexp = "^55\\s?\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$",
                message = "Telefone inválido. O prefixo 55 é obrigatório. Exemplo: 55 (91) 98888-7777"
        )
        @NotBlank(message = "O número do telefone é obrigatório")
        String fonePrincipal,

        @Pattern(
                regexp = "^55\\s?\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$",
                message = "Telefone inválido. O prefixo 55 é obrigatório. Exemplo: 55 (91) 98888-7777"
        )
        String foneSecundario,


        Boolean consentimentoLGPD,


        UUID idAtendente
) {
}
