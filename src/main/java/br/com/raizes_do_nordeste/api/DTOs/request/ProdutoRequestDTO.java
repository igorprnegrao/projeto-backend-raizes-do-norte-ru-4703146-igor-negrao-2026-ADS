package br.com.raizes_do_nordeste.api.DTOs.request;

import br.com.raizes_do_nordeste.domain.enums.CategoriaComida;
import br.com.raizes_do_nordeste.domain.enums.PeriodoDia;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record ProdutoRequestDTO(

        @NotBlank(message = "O nome do produto é obrigatório")
        String nome,

        String descricao,

        @NotNull(message = "O preço unitário é obrigatório")
        @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
        BigDecimal precoUnitario,

        @NotNull(message = "A categoria é obrigatória")
        CategoriaComida categoriaComida,

        @NotNull(message = "O período do dia é obrigatório")
        PeriodoDia periodoDia,

        @NotNull(message = "O id da unidade é obrigatório")
        UUID idUnidade,

        @Min(value = 0, message = "A quantidade inicial não pode ser negativa")
        Integer quantidadeInicial

) {
}
