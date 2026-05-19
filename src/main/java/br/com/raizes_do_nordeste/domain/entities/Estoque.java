package br.com.raizes_do_nordeste.domain.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "tb_estoque", uniqueConstraints = {
        @UniqueConstraint(name = "uk_estoque_produto_unidade", columnNames = {"produto_id", "unidade_id"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_estoque")
    private UUID id;

    @Column(name = "quantidade")
    private Integer quantidade;

    @ManyToOne(optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @ManyToOne(optional = false)
    @JoinColumn(name = "unidade_id", nullable = false)
    private Unidade unidade;

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Estoque estoque)) return false;

        return getId().equals(estoque.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
