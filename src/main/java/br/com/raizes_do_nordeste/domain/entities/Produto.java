package br.com.raizes_do_nordeste.domain.entities;


import br.com.raizes_do_nordeste.domain.enums.CategoriaComida;
import br.com.raizes_do_nordeste.domain.enums.PeriodoDia;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "tb_produto")
@BatchSize(size = 30)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Produto {

    @Id
    @Column(name = "id_produto")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "preco_unitario", nullable = false)
    private BigDecimal precoUnitario;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria_comida", nullable = false)
    private CategoriaComida categoriaComida;

    @Enumerated(EnumType.STRING)
    @Column(name = "periodo_dia", nullable = false)
    private PeriodoDia periodoDia;

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Produto produto)) return false;
        return getId().equals(produto.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
