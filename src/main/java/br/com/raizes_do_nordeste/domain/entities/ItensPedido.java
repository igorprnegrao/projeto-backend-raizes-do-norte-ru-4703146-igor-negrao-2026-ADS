package br.com.raizes_do_nordeste.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

import java.util.UUID;

@Entity
@Table(name = "tb_itens_pedido")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ItensPedido {

    @Id
    @Column(name = "id_itens_pedido")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name="quantidade")
    private Integer quantidade;

    @Column(name = "preco_momento")
    private BigDecimal precoMomento;

    @ManyToOne(optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof ItensPedido that)) return false;

        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}